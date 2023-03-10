package com.platon.browser.analyzer.epoch;

import com.platon.browser.bean.CommonConstant;
import com.platon.browser.service.statistic.StatisticService;
import com.platon.browser.utils.ChainVersionUtil;
import com.platon.browser.v0160.service.DelegateBalanceAdjustmentService;
import com.platon.contracts.ppos.dto.resp.GovernParam;
import com.platon.contracts.ppos.dto.resp.TallyResult;
import com.platon.browser.bean.CollectionEvent;
import com.platon.browser.bean.CustomProposal;
import com.platon.browser.cache.NetworkStatCache;
import com.platon.browser.cache.NodeCache;
import com.platon.browser.cache.ProposalCache;
import com.platon.browser.client.PlatOnClient;
import com.platon.browser.client.SpecialApi;
import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.v0150.V0150Config;
import com.platon.browser.dao.entity.Config;
import com.platon.browser.dao.entity.Proposal;
import com.platon.browser.dao.entity.ProposalExample;
import com.platon.browser.dao.custommapper.NewBlockMapper;
import com.platon.browser.dao.mapper.ProposalMapper;
import com.platon.browser.dao.param.epoch.NewBlock;
import com.platon.browser.elasticsearch.dto.Block;
import com.platon.browser.exception.BusinessException;
import com.platon.browser.exception.NoSuchBeanException;
import com.platon.browser.service.govern.ParameterService;
import com.platon.browser.service.proposal.ProposalService;
import com.platon.browser.v0150.bean.AdjustParam;
import com.platon.browser.v0150.service.StakingDelegateBalanceAdjustmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Slf4j
@Service
public class OnNewBlockAnalyzer {

    @Resource
    private NodeCache nodeCache;

    @Resource
    private NewBlockMapper newBlockMapper;

    @Resource
    private NetworkStatCache networkStatCache;

    @Resource
    private ProposalCache proposalCache;

    @Resource
    private ProposalService proposalService;

    @Resource
    private ProposalMapper proposalMapper;

    @Resource
    private ParameterService parameterService;

    @Resource
    private PlatOnClient platOnClient;

    @Resource
    private V0150Config v0150Config;

    @Resource
    private StakingDelegateBalanceAdjustmentService stakingDelegateBalanceAdjustmentService;

    @Resource
    private SpecialApi specialApi;

    @Resource
    private BlockChainConfig chainConfig;

    @Resource
    private StatisticService statisticService;

    @Resource
    private DelegateBalanceAdjustmentService delegateBalanceAdjustmentService;

    public void analyze(CollectionEvent event, Block block) throws NoSuchBeanException {

        long startTime = System.currentTimeMillis();

        networkStatCache.getNetworkStat().setCurNumber(event.getBlock().getNum());
        networkStatCache.getNetworkStat().setCurBlockHash(event.getBlock().getHash());
        NewBlock newBlock = NewBlock.builder()
                                    .nodeId(block.getNodeId())
                                    .stakingBlockNum(nodeCache.getNode(block.getNodeId()).getStakingBlockNum())
                                    .blockRewardValue(event.getEpochMessage().getBlockReward())
                                    .feeRewardValue(new BigDecimal(block.getTxFee()))
                                    .predictStakingReward(event.getEpochMessage().getStakeReward())
                                    .build();

        newBlockMapper.newBlock(newBlock);
        log.info("??????[{}]??????[{}]???????????????[{}]???????????????[{}]",
                 event.getBlock().getNum(),
                 newBlock.getNodeId(),
                 newBlock.getFeeRewardValue(),
                 newBlock.getBlockRewardValue());

        // ?????????????????????????????????????????????
        Set<String> proposalTxHashSet = proposalCache.get(block.getNum());
        if (proposalTxHashSet != null) {
            ProposalExample proposalExample = new ProposalExample();
            proposalExample.createCriteria().andHashIn(new ArrayList<>(proposalTxHashSet));
            List<Proposal> proposalList = proposalMapper.selectByExample(proposalExample);
            Map<String, Proposal> proposalMap = new HashMap<>();
            proposalList.forEach(p -> proposalMap.put(p.getHash(), p));
            List<Config> configList = new ArrayList<>();
            for (String hash : proposalTxHashSet) {
                try {
                    TallyResult tr = proposalService.getTallyResult(hash);
                    if (tr == null) {
                        continue;
                    }
                    if (tr.getStatus() == CustomProposal.StatusEnum.PASS.getCode() || tr.getStatus() == CustomProposal.StatusEnum.FINISH.getCode()) {
                        // ??????????????????????????????status=2???||???????????????????????????,status=5??????
                        // ?????????????????????????????????Config?????????????????????
                        Proposal proposal = proposalMap.get(hash);
                        if (proposal.getType() == CustomProposal.TypeEnum.PARAMETER.getCode()) {
                            // ?????????????????????
                            // ?????????????????????????????????Config?????????????????????
                            Config config = new Config();
                            config.setModule(proposal.getModule());
                            config.setName(proposal.getName());
                            config.setStaleValue(proposal.getStaleValue());
                            config.setValue(proposal.getNewValue());
                            configList.add(config);
                        }
                        if (proposal.getType() == CustomProposal.TypeEnum.UPGRADE.getCode()) {
                            // ?????????????????????
                            // ?????????????????????????????????????????????????????????Config?????????????????????
                            List<GovernParam> governParamList = platOnClient.getGovernParamValue("");
                            governParamList.forEach(gp -> {
                                Config config = new Config();
                                config.setModule(gp.getParamItem().getModule());
                                config.setName(gp.getParamItem().getName());
                                config.setStaleValue(gp.getParamValue().getStaleValue());
                                config.setValue(gp.getParamValue().getValue());
                                configList.add(config);
                            });
                            BigInteger proposalVersion = new BigInteger(proposal.getNewVersion());
                            String proposalPipid = proposal.getPipId();
                            BigInteger configVersion = v0150Config.getAdjustmentActiveVersion();
                            String configPipid = v0150Config.getAdjustmentPipId();
                            if (proposalVersion.compareTo(configVersion) >= 0 && proposalPipid.equals(configPipid)) {
                                // ??????????????????????????????ID?????????????????????????????????????????????????????????
                                List<AdjustParam> adjustParams = specialApi.getStakingDelegateAdjustDataList(platOnClient.getWeb3jWrapper()
                                                                                                                         .getWeb3j(),
                                                                                                             BigInteger.valueOf(block.getNum()));
                                adjustParams.forEach(param -> {
                                    param.setBlockTime(block.getTime());
                                    param.setSettleBlockCount(chainConfig.getSettlePeriodBlockCount());
                                });
                                stakingDelegateBalanceAdjustmentService.adjust(adjustParams);
                            }
                            // alaya???????????????????????????0.16.0??????????????????????????????issue1583
                            BigInteger v0160Version = ChainVersionUtil.toBigIntegerVersion(CommonConstant.V0160_VERSION);
                            if (proposalVersion.compareTo(v0160Version) == 0 && CommonConstant.ALAYA_CHAIN_ID == chainConfig.getChainId()) {
                                delegateBalanceAdjustmentService.adjust();
                            }
                            // ????????????1.3.0+?????????unDelegateFreezeDuration????????????
                            parameterService.configUnDelegateFreezeDuration(proposalVersion);
                        }
                    }
                } catch (Exception e) {
                    log.error("get error", e);
                    throw new BusinessException(e.getMessage());
                }
            }
            if (!configList.isEmpty()) {
                // ???????????????config???????????????blockChainConfig
                parameterService.rotateConfig(configList);
            }
        }

        statisticService.nodeSettleStatisBlockNum(event);

        log.debug("????????????:{} ms", System.currentTimeMillis() - startTime);
    }

}
