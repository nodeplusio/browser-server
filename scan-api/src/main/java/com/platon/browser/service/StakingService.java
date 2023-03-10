package com.platon.browser.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.platon.browser.bean.CustomDelegation.YesNoEnum;
import com.platon.browser.bean.*;
import com.platon.browser.bean.CustomStaking.StatusEnum;
import com.platon.browser.client.PlatOnClient;
import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.constant.Browser;
import com.platon.browser.dao.custommapper.CustomDelegationMapper;
import com.platon.browser.dao.custommapper.CustomNodeMapper;
import com.platon.browser.dao.custommapper.CustomVoteMapper;
import com.platon.browser.dao.entity.*;
import com.platon.browser.dao.mapper.*;
import com.platon.browser.elasticsearch.dto.NodeOpt;
import com.platon.browser.enums.AddressTypeEnum;
import com.platon.browser.enums.I18nEnum;
import com.platon.browser.enums.RetEnum;
import com.platon.browser.enums.StakingStatusEnum;
import com.platon.browser.request.staking.*;
import com.platon.browser.response.BaseResp;
import com.platon.browser.response.RespPage;
import com.platon.browser.response.staking.*;
import com.platon.browser.service.elasticsearch.EsBlockRepository;
import com.platon.browser.service.elasticsearch.EsNodeOptRepository;
import com.platon.browser.service.elasticsearch.bean.ESResult;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.browser.utils.*;
import com.platon.contracts.ppos.dto.resp.Reward;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ?????????????????????
 *
 * @author zhangrj
 * @file StakingServiceImpl.java
 * @description
 * @data 2019???8???31???
 */
@Service
public class StakingService {

    private final Logger logger = LoggerFactory.getLogger(StakingService.class);

    @Resource
    private StatisticCacheService statisticCacheService;

    @Resource
    private CustomVoteMapper customVoteMapper;

    @Resource
    private CustomDelegationMapper customDelegationMapper;

    @Resource
    private CustomNodeMapper customNodeMapper;

    @Resource
    private NodeMapper nodeMapper;

    @Resource
    private EsNodeOptRepository esNodeOptRepository;

    @Resource
    private EsBlockRepository esBlockRepository;

    @Resource
    private I18nUtil i18n;

    @Resource
    private BlockChainConfig blockChainConfig;

    @Resource
    private PlatOnClient platonClient;

    @Resource
    private CommonService commonService;

    @Resource
    private AddressMapper addressMapper;
    @Resource
    private ProposalMapper proposalMapper;
    @Resource
    private NodeHistoryTotalAndStatDelegateValueMapper nodeHistoryTotalAndStatDelegateValueMapper;
    @Resource
    private NodeHistoryDeleAnnualizedRateMapper nodeHistoryDeleAnnualizedRateMapper;

    public StakingStatisticNewResp stakingStatisticNew() {
        /** ?????????????????? */
        NetworkStat networkStatRedis = statisticCacheService.getNetworkStatCache();
        StakingStatisticNewResp stakingStatisticNewResp = new StakingStatisticNewResp();
        if (networkStatRedis != null) {
            BeanUtils.copyProperties(networkStatRedis, stakingStatisticNewResp);
            stakingStatisticNewResp.setCurrentNumber(networkStatRedis.getCurNumber());
            stakingStatisticNewResp.setNextSetting(networkStatRedis.getNextSettle());
            // ???????????? = ???????????????????????? - ??????????????????
            stakingStatisticNewResp.setDelegationValue(networkStatRedis.getStakingDelegationValue().subtract(networkStatRedis.getStakingValue()));
            stakingStatisticNewResp.setStakingReward(networkStatRedis.getStakingReward());
            stakingStatisticNewResp.setIssueValue(networkStatRedis.getIssueValue());
            StakingBO bo = commonService.getTotalStakingValueAndStakingDenominator(networkStatRedis);
            stakingStatisticNewResp.setStakingDenominator(bo.getStakingDenominator());
            stakingStatisticNewResp.setStakingDelegationValue(bo.getTotalStakingValue());
        }
        return stakingStatisticNewResp;
    }

    public RespPage<AliveStakingListResp> aliveStakingList(AliveStakingListReq req) {
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        Integer status = null;
        Integer isSettle = null;

        // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        boolean exitingAsActive = false;
        /**
         *  ?????????????????????????????????????????????
         */
        switch (StakingStatusEnum.valueOf(req.getQueryStatus().toUpperCase())) {
            case ALL:
                /** ??????????????? */
                status = StakingStatusEnum.CANDIDATE.getCode();
                exitingAsActive = true;
                break;
            case ACTIVE:
                /** ???????????????????????????????????????????????????????????? */
                status = StakingStatusEnum.CANDIDATE.getCode();
                isSettle = CustomStaking.YesNoEnum.YES.getCode();
                exitingAsActive = true;
                break;
            case CANDIDATE:
                /** ??????????????? */
                status = StakingStatusEnum.CANDIDATE.getCode();
                isSettle = CustomStaking.YesNoEnum.NO.getCode();
                break;
            default:
                break;
        }
        RespPage<AliveStakingListResp> respPage = new RespPage<>();
        List<AliveStakingListResp> lists = new LinkedList<>();
        /** ??????????????????????????????????????? */
        NodeExample nodeExample = new NodeExample();
        nodeExample.setOrderByClause(" big_version desc, total_value desc,staking_block_num asc, staking_tx_index asc");
        NodeExample.Criteria criteria1 = nodeExample.createCriteria();
        criteria1.andStatusEqualTo(status);
        if (StringUtils.isNotBlank(req.getKey())) {
            criteria1.andNodeNameLike("%" + req.getKey() + "%");
        }
        if (isSettle != null) {
            criteria1.andIsSettleEqualTo(isSettle);
        }

        if (exitingAsActive) {
            /**
             * ?????????????????????????????????????????????????????????????????????
             */
            NodeExample.Criteria criteria2 = nodeExample.createCriteria();
            criteria2.andStatusEqualTo(CustomStaking.StatusEnum.EXITING.getCode());
            if (StringUtils.isNotBlank(req.getKey())) {
                criteria2.andNodeNameLike("%" + req.getKey() + "%");
            }
            criteria2.andIsSettleEqualTo(CustomStaking.YesNoEnum.YES.getCode());
            nodeExample.or(criteria2);
        }

        Page<Node> stakingPage = customNodeMapper.selectListByExample(nodeExample);
        List<Node> stakings = stakingPage.getResult();
        /** ?????????????????? */
        NetworkStat networkStatRedis = statisticCacheService.getNetworkStatCache();
        int i = (req.getPageNo() - 1) * req.getPageSize();
        for (Node staking : stakings) {
            AliveStakingListResp aliveStakingListResp = new AliveStakingListResp();
            BeanUtils.copyProperties(staking, aliveStakingListResp);
            aliveStakingListResp.setBlockQty(staking.getStatBlockQty());
            aliveStakingListResp.setDelegateQty(staking.getStatValidAddrs());
            aliveStakingListResp.setExpectedIncome(staking.getAnnualizedRate().toString());
            /** ??????????????????=?????????????????????(???????????????)+?????????????????????(???????????????) */
            String sumAmount = staking.getStatDelegateValue().toString();
            aliveStakingListResp.setDelegateValue(sumAmount);
            aliveStakingListResp.setIsInit(staking.getIsInit() == 1);
            aliveStakingListResp.setStakingIcon(staking.getNodeIcon());
            if (staking.getIsRecommend() != null) {
                aliveStakingListResp.setIsRecommend(CustomStaking.YesNoEnum.YES.getCode() == staking.getIsRecommend());
            }
            /** ???????????? */
            aliveStakingListResp.setRanking(i + 1);
            aliveStakingListResp.setSlashLowQty(staking.getStatSlashLowQty());
            aliveStakingListResp.setSlashMultiQty(staking.getStatSlashMultiQty());
            /** ????????????????????????????????????????????????????????????????????????????????? */
            if (staking.getNodeId().equals(networkStatRedis.getNodeId())) {
                aliveStakingListResp.setStatus(StakingStatusEnum.BLOCK.getCode());
            } else {
                aliveStakingListResp.setStatus(StakingStatusEnum.getCodeByStatus(staking.getStatus(), staking.getIsConsensus(), staking.getIsSettle()));
            }
            /** ????????????=???????????????+?????? */
            aliveStakingListResp.setTotalValue(staking.getTotalValue().toString());
            aliveStakingListResp.setDeleAnnualizedRate(staking.getDeleAnnualizedRate().toString());
            try {
                String nodeSettleStatisInfo = staking.getNodeSettleStatisInfo();
                NodeSettleStatis nodeSettleStatis = NodeSettleStatis.jsonToBean(nodeSettleStatisInfo);
                BigInteger settleEpochRound = EpochUtil.getEpoch(BigInteger.valueOf(networkStatRedis.getCurNumber()), blockChainConfig.getSettlePeriodBlockCount());
                aliveStakingListResp.setGenBlocksRate(nodeSettleStatis.computeGenBlocksRate(settleEpochRound));
            } catch (Exception e) {
                logger.error("????????????24?????????????????????", e);
            }
            aliveStakingListResp.setDelegatedRewardRatio(new BigDecimal(staking.getRewardPer()).divide(Browser.PERCENTAGE).toString() + "%");
            if (staking.getProgramVersion() != 0) {
                aliveStakingListResp.setVersion(ChainVersionUtil.toStringVersion(BigInteger.valueOf(staking.getProgramVersion())));
            } else {
                aliveStakingListResp.setVersion(ChainVersionUtil.toStringVersion(BigInteger.valueOf(staking.getBigVersion())));
            }
            lists.add(aliveStakingListResp);
            i++;
        }
        Page<?> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setTotal(stakingPage.getTotal());
        respPage.init(page, lists);
        return respPage;
    }

    public RespPage<HistoryStakingListResp> historyStakingList(HistoryStakingListReq req) {
        /** ???????????????????????????????????? */
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        List<Integer> status = new ArrayList<>();
        status.add(CustomStaking.StatusEnum.EXITING.getCode());
        status.add(CustomStaking.StatusEnum.EXITED.getCode());
        RespPage<HistoryStakingListResp> respPage = new RespPage<>();
        List<HistoryStakingListResp> lists = new LinkedList<>();
        /** ??????????????????????????????????????? */
        NodeExample nodeExample = new NodeExample();
        nodeExample.setOrderByClause(" leave_time desc");
        NodeExample.Criteria criteria = nodeExample.createCriteria();
        criteria.andStatusIn(status);
        /**
         * ????????????????????????????????????????????????
         */
        criteria.andIsSettleEqualTo(CustomStaking.YesNoEnum.NO.getCode());

        if (StringUtils.isNotBlank(req.getKey())) {
            criteria.andNodeNameLike("%" + req.getKey() + "%");
        }
        Page<Node> stakings = customNodeMapper.selectListByExample(nodeExample);

        for (Node stakingNode : stakings.getResult()) {
            HistoryStakingListResp historyStakingListResp = new HistoryStakingListResp();
            BeanUtils.copyProperties(stakingNode, historyStakingListResp);
            if (stakingNode.getLeaveTime() != null) {
                historyStakingListResp.setLeaveTime(stakingNode.getLeaveTime().getTime());
            }
            historyStakingListResp.setNodeName(stakingNode.getNodeName());
            historyStakingListResp.setStakingIcon(stakingNode.getNodeIcon());
            historyStakingListResp.setSlashLowQty(stakingNode.getStatSlashLowQty());
            historyStakingListResp.setSlashMultiQty(stakingNode.getStatSlashMultiQty());
            /**
             * ????????????????????????hes+lock
             */
            historyStakingListResp.setStatDelegateReduction(stakingNode.getStatDelegateValue().add(stakingNode.getStatDelegateReleased()));
            historyStakingListResp.setStatus(StakingStatusEnum.getCodeByStatus(stakingNode.getStatus(), stakingNode.getIsConsensus(), stakingNode.getIsSettle()));
            historyStakingListResp.setBlockQty(stakingNode.getStatBlockQty());

            // ?????????????????????????????????
            Long unlockBlockNum = stakingNode.getUnStakeEndBlock();
            historyStakingListResp.setUnlockBlockNum(unlockBlockNum);

            lists.add(historyStakingListResp);
        }
        Page<?> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setTotal(stakings.getTotal());
        respPage.init(page, lists);
        return respPage;
    }

    public BaseResp<StakingDetailsResp> stakingDetails(StakingDetailsReq req) {
        /**
         * ??????????????????????????????????????????????????????????????????
         */
        Node stakingNode = nodeMapper.selectByPrimaryKey(req.getNodeId());
        StakingDetailsResp resp = new StakingDetailsResp();
        // ??????????????????
        if (stakingNode != null) {
            BeanUtils.copyProperties(stakingNode, resp);
            resp.setIsInit(stakingNode.getIsInit() == 1);
            resp.setStatus(StakingStatusEnum.getCodeByStatus(stakingNode.getStatus(), stakingNode.getIsConsensus(), stakingNode.getIsSettle()));
            resp.setSlashLowQty(stakingNode.getStatSlashLowQty());
            resp.setSlashMultiQty(stakingNode.getStatSlashMultiQty());
            resp.setBlockQty(stakingNode.getStatBlockQty());
            resp.setExpectBlockQty(stakingNode.getStatExpectBlockQty());
            resp.setVerifierTime(stakingNode.getStatVerifierTime());
            resp.setJoinTime(stakingNode.getJoinTime().getTime());
            resp.setDenefitAddr(stakingNode.getBenefitAddr());
            Address denefitAddr = addressMapper.selectByPrimaryKey(stakingNode.getBenefitAddr());
            resp.setDenefitAddrType(CommonUtil.ofNullable(() -> denefitAddr.getType()).orElse(AddressTypeEnum.ACCOUNT.getCode()));
            Address stakingAddr = addressMapper.selectByPrimaryKey(stakingNode.getStakingAddr());
            resp.setStakingAddrType(CommonUtil.ofNullable(() -> stakingAddr.getType()).orElse(AddressTypeEnum.ACCOUNT.getCode()));
            resp.setStakingIcon(stakingNode.getNodeIcon());
            resp.setDeleAnnualizedRate(stakingNode.getDeleAnnualizedRate().toString());
            resp.setRewardPer(new BigDecimal(stakingNode.getRewardPer()).divide(Browser.PERCENTAGE).toString());
            resp.setNextRewardPer(new BigDecimal(stakingNode.getNextRewardPer()).divide(Browser.PERCENTAGE).toString());
            resp.setTotalDeleReward(stakingNode.getTotalDeleReward().add(stakingNode.getPreTotalDeleReward()));
            try {
                String nodeSettleStatisInfo = stakingNode.getNodeSettleStatisInfo();
                NodeSettleStatis nodeSettleStatis = NodeSettleStatis.jsonToBean(nodeSettleStatisInfo);
                NetworkStat networkStatRedis = statisticCacheService.getNetworkStatCache();
                BigInteger settleEpochRound = EpochUtil.getEpoch(BigInteger.valueOf(networkStatRedis.getCurNumber()), blockChainConfig.getSettlePeriodBlockCount());
                resp.setGenBlocksRate(nodeSettleStatis.computeGenBlocksRate(settleEpochRound));
            } catch (Exception e) {
                logger.error("????????????24?????????????????????", e);
            }
            resp.setVersion(ChainVersionUtil.toStringVersion(BigInteger.valueOf(stakingNode.getProgramVersion())));
            /**
             * ????????????????????? ???????????????????????????????????????????????????????????????
             */
            resp.setDeleRewardRed(stakingNode.getTotalDeleReward().add(stakingNode.getPreTotalDeleReward()).subtract(stakingNode.getHaveDeleReward()));
            /** ??????????????????????????????????????????  */
            if (CustomStaking.YesNoEnum.YES.getCode() != stakingNode.getIsInit()) {
                resp.setExpectedIncome(String.valueOf(stakingNode.getAnnualizedRate()));
                resp.setRewardValue(stakingNode.getStatFeeRewardValue().add(stakingNode.getStatBlockRewardValue()).add(stakingNode.getStatStakingRewardValue()));
                logger.info("??????????????????[{}]=??????????????????(?????????)[{}]+??????????????????(?????????)[{}]+??????????????????(?????????)[{}]",
                        resp.getRewardValue(),
                        stakingNode.getStatFeeRewardValue(),
                        stakingNode.getStatBlockRewardValue(),
                        stakingNode.getStatStakingRewardValue());
            } else {
                resp.setRewardValue(stakingNode.getStatFeeRewardValue());
                logger.info("??????????????????[{}]=??????????????????(?????????)[{}]", resp.getRewardValue(), stakingNode.getStatFeeRewardValue());
                resp.setExpectedIncome("");
            }
            String webSite = "";
            if (StringUtils.isNotBlank(stakingNode.getWebSite())) {
                /**
                 * ??????????????????http???????????????
                 */
                if (stakingNode.getWebSite().startsWith(Browser.HTTP) || stakingNode.getWebSite().startsWith(Browser.HTTPS)) {
                    webSite = stakingNode.getWebSite();
                } else {
                    webSite = Browser.HTTP + stakingNode.getWebSite();
                }
            }
            resp.setWebsite(webSite);
            /** ?????????????????????url??????????????? */
            if (StringUtils.isNotBlank(stakingNode.getExternalName())) {
                resp.setExternalUrl(blockChainConfig.getKeyBase() + stakingNode.getExternalName());
            } else {
                resp.setExternalUrl(blockChainConfig.getKeyBase());
            }
            if (stakingNode.getLeaveTime() != null) {
                resp.setLeaveTime(stakingNode.getLeaveTime().getTime());
            }
            // ?????????????????????
            resp.setDelegateValue(stakingNode.getStatDelegateValue());
            // ???????????????????????????
            resp.setTotalValue(stakingNode.getTotalValue());

            /**
             * ???????????????true???????????????????????????
             * ????????????????????????????????????
             */
            if (stakingNode.getStatus().intValue() == StatusEnum.CANDIDATE.getCode()) {
                // ?????????????????????????????????????????????
                resp.setDelegateQty(stakingNode.getStatValidAddrs());
                /** ????????????=?????????????????????+ ?????????????????????  */
                BigDecimal stakingValue = stakingNode.getStakingHes().add(stakingNode.getStakingLocked());
                resp.setStakingValue(stakingValue);
            } else {
                // ????????????????????????????????????????????????
                resp.setDelegateQty(stakingNode.getStatInvalidAddrs());
                /**
                 * ????????????????????????????????????0
                 */
                if (stakingNode.getIsSettle().intValue() == YesNoEnum.YES.getCode()) {
                    resp.setTotalValue(BigDecimal.ZERO);
                    resp.setStakingValue(BigDecimal.ZERO);
                } else {
                    if (stakingNode.getStatus().intValue() == StatusEnum.LOCKED.getCode()) {
                        resp.setStakingValue(stakingNode.getStakingLocked());
                    } else {
                        resp.setStakingValue(stakingNode.getStakingReduction());
                    }
                }
                // ????????????????????????(von)
                resp.setStatDelegateReduction(resp.getDelegateValue().add(stakingNode.getStatDelegateReleased()));
            }
        }
        return BaseResp.build(RetEnum.RET_SUCCESS.getCode(), i18n.i(I18nEnum.SUCCESS), resp);
    }

    public RespPage<StakingOptRecordListResp> stakingOptRecordList(StakingOptRecordListReq req) {
        RespPage<StakingOptRecordListResp> respPage = new RespPage<>();
        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.must(new ESQueryBuilders().term("nodeId", req.getNodeId()));
        ESResult<NodeOpt> items = new ESResult<>();
        constructor.setDesc("id");
        try {
            items = esNodeOptRepository.search(constructor, NodeOpt.class, req.getPageNo(), req.getPageSize());
        } catch (Exception e) {
            logger.error("???????????????????????????", e);
            return respPage;
        }
        List<NodeOpt> nodeOpts = items.getRsData();
        List<StakingOptRecordListResp> lists = new LinkedList<>();
        for (NodeOpt nodeOpt : nodeOpts) {
            StakingOptRecordListResp stakingOptRecordListResp = new StakingOptRecordListResp();
            BeanUtils.copyProperties(nodeOpt, stakingOptRecordListResp);
            stakingOptRecordListResp.setType(String.valueOf(nodeOpt.getType()));
            stakingOptRecordListResp.setTimestamp(nodeOpt.getTime().getTime());
            stakingOptRecordListResp.setBlockNumber(nodeOpt.getBNum());
            if (StringUtils.isNotBlank(nodeOpt.getDesc())) {
                String[] desces = nodeOpt.getDesc().split(Browser.OPT_SPILT);
                /** ?????????????????????????????? */
                switch (NodeOpt.TypeEnum.getEnum(String.valueOf(nodeOpt.getType()))) {
                    /**
                     *???????????????
                     */
                    case MODIFY:
                        if (desces.length > 1) {
                            stakingOptRecordListResp.setBeforeRate(new BigDecimal(desces[0]).divide(Browser.PERCENTAGE).toString());
                            stakingOptRecordListResp.setAfterRate(new BigDecimal(desces[1]).divide(Browser.PERCENTAGE).toString());
                        }
                        break;
                    /** ???????????? */
                    case PROPOSALS:
                        Proposal proposal = proposalMapper.selectByPrimaryKey(nodeOpt.getTxHash());
                        if (ObjectUtil.isNotNull(proposal) && StrUtil.isNotBlank(proposal.getTopic())) {
                            String desc = StrUtil.replace(stakingOptRecordListResp.getDesc(), Browser.INQUIRY, proposal.getTopic());
                            nodeOpt.setDesc(desc);
                            stakingOptRecordListResp.setDesc(desc);
                            desces = nodeOpt.getDesc().split(Browser.OPT_SPILT);
                        }
                        stakingOptRecordListResp.setId(Browser.PIP_NAME + desces[0]);
                        stakingOptRecordListResp.setTitle(Browser.INQUIRY.equals(desces[1]) ? "" : desces[1]);
                        stakingOptRecordListResp.setProposalType(desces[2]);
                        if (desces.length > 3) {
                            stakingOptRecordListResp.setVersion(desces[3]);
                        }
                        break;
                    /** ???????????? */
                    case VOTE:
                        // ???????????????????????????????????????????????????????????????????????????????????????
                        CustomVoteProposal customVoteProposal = customVoteMapper.selectVotePropal(nodeOpt.getTxHash());
                        if (ObjectUtil.isNotNull(customVoteProposal) && StrUtil.isNotBlank(customVoteProposal.getTopic())) {
                            String desc = StrUtil.replace(stakingOptRecordListResp.getDesc(), Browser.INQUIRY, customVoteProposal.getTopic());
                            nodeOpt.setDesc(desc);
                            stakingOptRecordListResp.setDesc(desc);
                            desces = nodeOpt.getDesc().split(Browser.OPT_SPILT);
                        }
                        stakingOptRecordListResp.setTitle(Browser.INQUIRY.equals(desces[1]) ? "" : desces[1]);
                        stakingOptRecordListResp.setId(Browser.PIP_NAME + desces[0]);
                        stakingOptRecordListResp.setOption(desces[2]);
                        stakingOptRecordListResp.setProposalType(desces[3]);
                        if (desces.length > 4) {
                            stakingOptRecordListResp.setVersion(desces[4]);
                        }
                        break;
                    /** ?????? */
                    case MULTI_SIGN:
                        stakingOptRecordListResp.setPercent(desces[0]);
                        stakingOptRecordListResp.setAmount(new BigDecimal(desces[1]));
                        break;
                    /** ???????????? */
                    case LOW_BLOCK_RATE:
                        stakingOptRecordListResp.setPercent(desces[1]);
                        stakingOptRecordListResp.setAmount(new BigDecimal(desces[2]));
                        stakingOptRecordListResp.setIsFire(Integer.parseInt(desces[3]));
                        break;
                    /**
                     * ????????????
                     */
                    case PARAMETER:
                        stakingOptRecordListResp.setId(Browser.PIP_NAME + desces[0]);
                        stakingOptRecordListResp.setTitle(Browser.INQUIRY.equals(desces[1]) ? "" : desces[1]);
                        stakingOptRecordListResp.setProposalType(desces[2]);
                        stakingOptRecordListResp.setType("4");
                        break;
                    /**
                     * ????????????
                     */
                    case VERSION:
                        String v = desces[2];
                        if (StringUtils.isNotBlank(v)) {
                            v = ChainVersionUtil.toStringVersion(new BigInteger(v));
                        } else {
                            v = "0";
                        }
                        stakingOptRecordListResp.setVersion(v);
                        stakingOptRecordListResp.setType("12");
                        break;
                    default:
                        break;
                }
            }

            lists.add(stakingOptRecordListResp);
        }
        /** ?????????????????? */
        Page<?> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setTotal(items.getTotal());
        respPage.init(page, lists);
        return respPage;
    }

    public RespPage<DelegationListByStakingResp> delegationListByStaking(DelegationListByStakingReq req) {
        Node node = nodeMapper.selectByPrimaryKey(req.getNodeId());
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        List<DelegationListByStakingResp> lists = new LinkedList<>();
        /** ????????????id????????????????????????????????? */
        Page<DelegationStaking> delegationStakings = customDelegationMapper.selectStakingByNodeId(req.getNodeId());
        for (DelegationStaking delegationStaking : delegationStakings.getResult()) {
            DelegationListByStakingResp byStakingResp = new DelegationListByStakingResp();
            BeanUtils.copyProperties(delegationStaking, byStakingResp);
            byStakingResp.setDelegateAddr(delegationStaking.getDelegateAddr());
            /**??????????????????LAT???????????????????????????????????????????????????????????????????????????????????????delegation???  */
            byStakingResp.setDelegateTotalValue(node.getStatDelegateValue());
            /**
             * ??????????????????has????????????lock??????
             */
            BigDecimal delValue = delegationStaking.getDelegateHes().add(delegationStaking.getDelegateLocked());
            byStakingResp.setDelegateValue(delValue);
            lists.add(byStakingResp);
        }
        /** ?????????????????? */
        Page<?> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setTotal(delegationStakings.getTotal());
        RespPage<DelegationListByStakingResp> respPage = new RespPage<>();
        respPage.init(page, lists);
        return respPage;
    }

    public RespPage<DelegationListByAddressResp> delegationListByAddress(DelegationListByAddressReq req) {
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        List<DelegationListByAddressResp> lists = new LinkedList<>();
        /** ???????????????????????????????????? */
        Page<DelegationAddress> delegationAddresses = customDelegationMapper.selectAddressByAddr(req.getAddress());
        /**
         * ?????????????????????id?????????????????????????????????????????????????????????
         */
        List<String> nodes = new ArrayList<>();
        for (DelegationAddress delegationAddress : delegationAddresses.getResult()) {
            nodes.add(delegationAddress.getNodeId());
        }
        /**
         * ???????????????????????????????????????
         */
        List<Reward> rewards = new ArrayList<>();
        try {
            rewards = platonClient.getRewardContract().getDelegateReward(req.getAddress(), nodes).send().getData();
        } catch (Exception e) {
            logger.error("???????????????????????????{}", e.getMessage());
            rewards = new ArrayList<>();
        }
        for (DelegationAddress delegationAddress : delegationAddresses.getResult()) {
            DelegationListByAddressResp byAddressResp = new DelegationListByAddressResp();
            BeanUtils.copyProperties(delegationAddress, byAddressResp);
            byAddressResp.setDelegateHas(delegationAddress.getDelegateHes());
            /** ????????????=???????????????????????????????????? */
            BigDecimal deleValue = delegationAddress.getDelegateHes().add(byAddressResp.getDelegateLocked());
            byAddressResp.setDelegateValue(deleValue);
            byAddressResp.setDelegateUnlock(delegationAddress.getDelegateHes());
            /**
             * ??????????????????
             */
            if (rewards != null) {
                for (Reward reward : rewards) {
                    /**
                     * ??????????????????????????????
                     */
                    if (delegationAddress.getNodeId().equals(HexUtil.prefix(reward.getNodeId()))) {
                        byAddressResp.setDelegateClaim(new BigDecimal(reward.getReward()));
                    }
                }
            }
            lists.add(byAddressResp);
        }
        /** ???????????? */
        Page<?> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setTotal(delegationAddresses.getTotal());
        RespPage<DelegationListByAddressResp> respPage = new RespPage<>();
        respPage.init(page, lists);
        return respPage;
    }

    public RespPage<LockedStakingListResp> lockedStakingList(LockedStakingListReq req) {
        PageHelper.startPage(req.getPageNo(), req.getPageSize());
        RespPage<LockedStakingListResp> respPage = new RespPage<>();
        List<LockedStakingListResp> lists = new LinkedList<>();
        NodeExample nodeExample = new NodeExample();
        nodeExample.setOrderByClause(" leave_time desc");
        NodeExample.Criteria criteria = nodeExample.createCriteria();
        criteria.andStatusEqualTo(StatusEnum.LOCKED.getCode());

        if (StringUtils.isNotBlank(req.getKey())) {
            criteria.andNodeNameLike("%" + req.getKey() + "%");
        }
        Page<Node> stakingPage = customNodeMapper.selectListByExample(nodeExample);

        /** ?????????????????? */
        //NetworkStat networkStatRedis = this.statisticCacheService.getNetworkStatCache();
        int i = (req.getPageNo() - 1) * req.getPageSize();
        for (Node node : stakingPage) {
            LockedStakingListResp lockedStakingListResp = new LockedStakingListResp();
            BeanUtils.copyProperties(node, lockedStakingListResp);
            lockedStakingListResp.setBlockQty(node.getStatBlockQty());
            lockedStakingListResp.setDelegateQty(node.getStatValidAddrs());
            lockedStakingListResp.setExpectedIncome(node.getAnnualizedRate().toString());
            /** ??????????????????=?????????????????????(???????????????)+?????????????????????(???????????????) */
            String sumAmount = node.getStatDelegateValue().toString();
            lockedStakingListResp.setDelegateValue(sumAmount);
            lockedStakingListResp.setIsInit(node.getIsInit() == 1);
            lockedStakingListResp.setStakingIcon(node.getNodeIcon());
            if (node.getIsRecommend() != null) {
                lockedStakingListResp.setIsRecommend(CustomStaking.YesNoEnum.YES.getCode() == node.getIsRecommend());
            }
            /** ???????????? */
            lockedStakingListResp.setRanking(i + 1);
            lockedStakingListResp.setSlashLowQty(node.getStatSlashLowQty());
            lockedStakingListResp.setSlashMultiQty(node.getStatSlashMultiQty());
            lockedStakingListResp.setStatus(StakingStatusEnum.LOCKED.getCode());
            Date leaveTime = node.getLeaveTime();
            lockedStakingListResp.setLeaveTime(leaveTime == null ? null : leaveTime.getTime());
            /** ????????????=???????????????+?????? */
            lockedStakingListResp.setTotalValue(node.getTotalValue().toString());
            lockedStakingListResp.setDeleAnnualizedRate(node.getDeleAnnualizedRate().toString());

            // ?????????????????????????????? = ????????????????????????????????????+????????????????????????x ??????????????????????????????
            int epoches = node.getZeroProduceFreezeEpoch() + node.getZeroProduceFreezeDuration();
            BigInteger unlockBlockNum = blockChainConfig.getSettlePeriodBlockCount().multiply(BigInteger.valueOf(epoches));
            lockedStakingListResp.setUnlockBlockNum(unlockBlockNum.longValue());

            lists.add(lockedStakingListResp);
            i++;
        }
        Page<?> page = new Page<>(req.getPageNo(), req.getPageSize());
        page.setTotal(stakingPage.getTotal());
        respPage.init(page, lists);
        return respPage;
    }

    public RespPage<BlockCountHistoryByNodeResp> getBlockCountHistoryByNode(String nodeid) {
        RespPage<BlockCountHistoryByNodeResp> result = new RespPage<>();
        String aggregationName = "by_day";
        DateHistogramAggregationBuilder byDay =
                AggregationBuilders.dateHistogram(aggregationName).calendarInterval(DateHistogramInterval.DAY).field("time");

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        ESQueryBuilders esQueryBuilders = new ESQueryBuilders().term("nodeId", nodeid)
                .range("time", "now-30d", "now");
        constructor.must(esQueryBuilders);
        constructor.aggregation(byDay);
        try {
            Aggregations aggregations = esBlockRepository.aggregationSearch(constructor).getAggregations();
            result.setData(((ParsedDateHistogram) aggregations.get(aggregationName)).getBuckets().stream()
                    .map(BlockCountHistoryByNodeResp::new)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            logger.error("??????es??????", e);
        }

        return result;
    }

    public RespPage<DeleAnnualizedRateHistoryByNodeResult> getDeleAnnualizedRateHistoryByNode(String nodeid) {
        NodeHistoryDeleAnnualizedRateExample example = new NodeHistoryDeleAnnualizedRateExample();
        NodeHistoryDeleAnnualizedRateExample.Criteria criteria = example.createCriteria();
        criteria.andNodeIdEqualTo(nodeid).andDateGreaterThan(DateUtils.addDays(new Date(), -30));
        List<NodeHistoryDeleAnnualizedRate> list = nodeHistoryDeleAnnualizedRateMapper.selectByExample(example);
        RespPage<DeleAnnualizedRateHistoryByNodeResult> result = new RespPage<>();
        result.setData(list.stream().map(DeleAnnualizedRateHistoryByNodeResult::new).collect(Collectors.toList()));
        return result;
    }

    public RespPage<TotalValueHistoryByNodeResultDetail> getTotalValueHistoryByNode(String nodeid) {
        NodeHistoryTotalAndStatDelegateValueExample example = new NodeHistoryTotalAndStatDelegateValueExample();
        NodeHistoryTotalAndStatDelegateValueExample.Criteria criteria = example.createCriteria();
        criteria.andNodeIdEqualTo(nodeid).andDateGreaterThan(DateUtils.addDays(new Date(), -30));
        List<NodeHistoryTotalAndStatDelegateValue> list = nodeHistoryTotalAndStatDelegateValueMapper.selectByExample(example);
        RespPage<TotalValueHistoryByNodeResultDetail> result = new RespPage<>();
        result.setData(list.stream().map(TotalValueHistoryByNodeResultDetail::new).collect(Collectors.toList()));
        return result;
    }
}
