package com.platon.browser.common.service.epoch;

import com.platon.browser.client.EpochInfo;
import com.platon.browser.client.PlatOnClient;
import com.platon.browser.client.SpecialApi;
import com.platon.browser.common.complement.cache.NetworkStatCache;
import com.platon.browser.common.exception.CandidateException;
import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.exception.BusinessException;
import com.platon.browser.utils.EpochUtil;
import com.platon.browser.utils.HexTool;
import com.platon.sdk.contracts.ppos.dto.CallResponse;
import com.platon.sdk.contracts.ppos.dto.resp.Node;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

/**
 * 奖励计算服务
 * 1、根据区块号计算周期切换相关值：
 * 名称/含义                                                                   变量名称
 * 当前增发周期开始时的激励池余额 IB                                             inciteBalance
 * 当前增发周期开始时的激励池余额分给区块奖励部分 BR=IB*区块奖励比例               inciteAmount4Block
 * 当前增发周期每个区块奖励值 BR/增发周期区块总数                                 blockReward
 * 当前增发周期开始时的激励池余额分给质押奖励部分 SR=IB*质押奖励比例               inciteAmount4Stake
 * 当前增发周期的每个结算周期质押奖励值 SSR=SR/一个增发周期包含的结算周期数        settleStakeReward
 * 当前结算周期每个节点的质押奖励值 PerNodeSR=SSR/当前结算周期实际验证人数         stakeReward
 * 当前共识周期验证人                                                            curValidators
 * 当前结算周期验证人                                                            curVerifiers
 */
@Slf4j
@Service
public class EpochRetryService {
    public static final Queue<ConfigChange> EPOCH_CHANGES = new LinkedList<>();

    @Autowired
    private BlockChainConfig chainConfig;
    @Autowired
    private PlatOnClient platOnClient;
    @Autowired
    private SpecialApi specialApi;
    @Getter
    private BigDecimal blockReward = BigDecimal.ZERO; // 当前增发周期每个区块奖励值 BR/增发周期区块总数
    @Getter
    private BigDecimal settleStakeReward = BigDecimal.ZERO;  // 当前增发周期的每个结算周期质押奖励值 SSR=SR/一个增发周期包含的结算周期数
    @Getter
    private BigDecimal stakeReward = BigDecimal.ZERO; // 当前结算周期每个节点的质押奖励值 PerNodeSR=SSR/当前结算周期实际验证人数
    @Getter
    private BigDecimal preStakeReward = BigDecimal.ZERO; // 前一结算周期每个节点的质押奖励值 PerNodeSR=SSR/当前结算周期实际验证人数
    @Getter
    private List <Node> preValidators = new ArrayList <>(); // 前一共识周期验证人列表
    @Getter
    private List <Node> curValidators = new ArrayList <>(); // 当前共识周期验证人列表
    @Getter
    private List <Node> preVerifiers = new ArrayList <>(); // 前一结算周期验证人列表
    @Getter
    private List <Node> curVerifiers = new ArrayList <>(); // 当前结算周期验证人列表
    @Getter
    private Long expectBlockCount = 0L; // 当前期望出块数

    @Autowired
    private NetworkStatCache networkStatCache;

    /**
     * 增发周期变更:
     * 必然伴随着结算周期和共识周期的变更
     *
     * @param currentBlockNumber 增发周期内的任意块
     */
    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public void issueChange ( BigInteger currentBlockNumber ) {
        log.debug("增发周期变更:{}({})", Thread.currentThread().getStackTrace()[1].getMethodName(), currentBlockNumber);
        try {

        } catch (Exception e) {
            log.error("", e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 共识周期变更
     *
     * @param currentBlockNumber 共识周期内的任意块
     */
    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public void consensusChange ( BigInteger currentBlockNumber ) {
        log.debug("共识周期变更:{}({})", Thread.currentThread().getStackTrace()[1].getMethodName(), currentBlockNumber);
        try {
            // 当前块所处的共识周期
            BigInteger currentEpoch = EpochUtil.getEpoch(currentBlockNumber, chainConfig.getConsensusPeriodBlockCount());
            // 链上最新块所处的共识周期
            Web3j web3j = platOnClient.getWeb3jWrapper().getWeb3j();
            BigInteger latestBlockNumber = platOnClient.getLatestBlockNumber();
            BigInteger latestEpoch = EpochUtil.getEpoch(latestBlockNumber, chainConfig.getConsensusPeriodBlockCount());
            // 上一个周期的最后一个块号
            BigInteger preEpochLastBlockNumber = EpochUtil.getPreEpochLastBlockNumber(currentBlockNumber, chainConfig.getConsensusPeriodBlockCount());

            // 前一周期的验证人
            List <Node> preNodes = specialApi.getHistoryValidatorList(web3j, preEpochLastBlockNumber);
            preNodes.forEach(n -> n.setNodeId(HexTool.prefix(n.getNodeId())));
            preValidators.clear();
            preValidators.addAll(preNodes);

            // 当前周期的验证人
            List <Node> curNodes = Collections.emptyList();
            if (latestEpoch.compareTo(currentEpoch) > 0) {
                // >>>>如果链上最新块所在周期>当前块所处周期, 则查询特殊节点历史接口
                // 如果前一个周期的最后一个块是0，则查第0块时的验证人作为当前验证人
                BigInteger targetBlockNumber = preEpochLastBlockNumber.compareTo(BigInteger.ZERO) == 0 ? BigInteger.ZERO : preEpochLastBlockNumber.add(BigInteger.ONE);
                curNodes = specialApi.getHistoryValidatorList(web3j, targetBlockNumber);
            }
            if (latestEpoch.compareTo(currentEpoch) == 0) {
                // >>>>如果链上最新块所在周期==当前块所处周期, 则查询实时接口
                curNodes = platOnClient.getLatestValidators();
            }
            curNodes.forEach(n -> n.setNodeId(HexTool.prefix(n.getNodeId())));
            curValidators.clear();
            curValidators.addAll(curNodes);

            // 更新期望出块数：期望出块数=共识周期块数/实际参与共识节点数
            expectBlockCount = chainConfig.getConsensusPeriodBlockCount().divide(BigInteger.valueOf(curValidators.size())).longValue();
        } catch (Exception e) {
            platOnClient.updateCurrentWeb3jWrapper();
            log.error("", e);
            throw new BusinessException(e.getMessage());
        }
    }

    /**
     * 结算周期变更
     *
     * @param currentBlockNumber 结算周期内的任意块
     */
    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public void settlementChange ( BigInteger currentBlockNumber ) {
        log.debug("结算周期变更:{}({})", Thread.currentThread().getStackTrace()[1].getMethodName(), currentBlockNumber);
        try {
            // 当前块所处周期
            BigInteger currentEpoch = EpochUtil.getEpoch(currentBlockNumber, chainConfig.getSettlePeriodBlockCount());
            // 链上最新块所处周期
            Web3j web3j = platOnClient.getWeb3jWrapper().getWeb3j();
            BigInteger latestBlockNumber = platOnClient.getLatestBlockNumber();
            BigInteger latestEpoch = EpochUtil.getEpoch(latestBlockNumber, chainConfig.getSettlePeriodBlockCount());
            // 上一个周期的最后一个块号
            BigInteger preEpochLastBlockNumber = EpochUtil.getPreEpochLastBlockNumber(currentBlockNumber, chainConfig.getSettlePeriodBlockCount());

            // 前一周期的验证人
            List <Node> preNodes = specialApi.getHistoryVerifierList(web3j, preEpochLastBlockNumber);
            preNodes.forEach(n -> n.setNodeId(HexTool.prefix(n.getNodeId())));
            preVerifiers.clear();
            preVerifiers.addAll(preNodes);

            // 当前周期的验证人
            List <Node> curNodes = Collections.emptyList();
            if (latestEpoch.compareTo(currentEpoch) > 0) {
                // >>>>如果链上最新块所在周期>当前块所处周期, 则查询特殊节点历史接口
                // 如果前一个周期的最后一个块是0，则查第0块时的验证人作为当前验证人
                BigInteger targetBlockNumber = preEpochLastBlockNumber.compareTo(BigInteger.ZERO) == 0 ? BigInteger.ZERO : preEpochLastBlockNumber.add(BigInteger.ONE);
                curNodes = specialApi.getHistoryVerifierList(web3j, targetBlockNumber);
            }
            if (latestEpoch.compareTo(currentEpoch) == 0) {
                // >>>>如果链上最新块所在周期==当前块所处周期, 则查询实时接口
                curNodes = platOnClient.getLatestVerifiers();
            }
            curNodes.forEach(n -> n.setNodeId(HexTool.prefix(n.getNodeId())));
            curVerifiers.clear();
            curVerifiers.addAll(curNodes);



            // 上一结算周期最后一个块号
            BigInteger preSettleEpochLastBlockNumber = EpochUtil.getPreEpochLastBlockNumber(currentBlockNumber, chainConfig.getSettlePeriodBlockCount());
            // 从特殊接口获取
            EpochInfo epochInfo = specialApi.getEpochInfo(platOnClient.getWeb3jWrapper().getWeb3j(),preSettleEpochLastBlockNumber);
            // 区块奖励
            blockReward=epochInfo.getPackageReward();
            // 当前增发周期内每个结算周期的质押奖励
            settleStakeReward = epochInfo.getStakingReward();

            ConfigChange configChange = new ConfigChange();
            configChange.setAvgPackTime(epochInfo.getAvgPackTime());
            configChange.setBlockReward(epochInfo.getPackageReward());
            configChange.setIssueEpoch(epochInfo.getYearNum());
            configChange.setYearStartNum(epochInfo.getYearStartNum());
            configChange.setYearEndNum(epochInfo.getYearEndNum());
            configChange.setRemainEpoch(epochInfo.getRemainEpoch());
            configChange.setSettleStakeReward(epochInfo.getStakingReward());


            // 前一结算周期质押奖励轮换
            preStakeReward = stakeReward;
            // 计算当前结算周期内每个验证人的质押奖励
            stakeReward = settleStakeReward.divide(BigDecimal.valueOf(curVerifiers.size()), 10, RoundingMode.FLOOR);

            configChange.setStakeReward(stakeReward);
            EPOCH_CHANGES.offer(configChange);

            applyConfigChange();
        } catch (Exception e) {
            platOnClient.updateCurrentWeb3jWrapper();
            log.error("", e);
            throw new BusinessException(e.getMessage());
        }
    }


    /**
     * 获取实时候选人列表
     *
     * @return
     * @throws Exception
     */
    @Retryable(value = Exception.class, maxAttempts = Integer.MAX_VALUE)
    public List <Node> getCandidates () throws CandidateException {
        log.debug("获取实时候选人列表:{}()", Thread.currentThread().getStackTrace()[1].getMethodName());
        try {
            CallResponse<List <Node>> br = platOnClient.getNodeContract().getCandidateList().send();
            if (!br.isStatusOk()) throw new CandidateException(br.getErrMsg());
            List <Node> candidates = br.getData();
            if (candidates == null) throw new CandidateException("实时候选节点列表为空!");
            candidates.forEach(v -> v.setNodeId(HexTool.prefix(v.getNodeId())));
            return candidates;
        } catch (Exception e) {
            platOnClient.updateCurrentWeb3jWrapper();
            log.error("", e);
            throw new CandidateException(e.getMessage());
        }
    }

    /**
     * 应用配置变更
     * 1、更新BlockChainConfig涉及的相关配置项
     * 2、更新网络统计缓存涉及的相关数据项
     */
    public void applyConfigChange(){
        ConfigChange summary = new ConfigChange();
        while (EPOCH_CHANGES.peek()!=null){
            ConfigChange configChange = EPOCH_CHANGES.poll();

            if(configChange.getIssueEpoch()!=null){
                // 更新增发周期轮数
                chainConfig.setIssueEpochRound(configChange.getIssueEpoch());
                summary.setIssueEpoch(configChange.getIssueEpoch());
            }
            if(configChange.getYearStartNum()!=null){
                // 更新增发周期起始块号
                chainConfig.setIssueEpochStartBlockNumber(configChange.getYearStartNum());
                summary.setYearStartNum(configChange.getYearStartNum());
            }
            if(configChange.getYearEndNum()!=null){
                // 更新增发周期结束块号
                chainConfig.setIssueEpochEndBlockNumber(configChange.getYearEndNum());
                summary.setYearEndNum(configChange.getYearEndNum());
            }

            if(configChange.getYearStartNum()!=null&&configChange.getYearEndNum()!=null){
                // 更新增发周期区块数
                chainConfig.setAddIssuePeriodBlockCount(configChange.getYearEndNum().subtract(configChange.getYearStartNum()).toBigInteger());
                // 更新每个增发周期的结算周期数
                chainConfig.setSettlePeriodCountPerIssue(chainConfig.getAddIssuePeriodBlockCount().divide(chainConfig.getSettlePeriodBlockCount()));
            }

            if(configChange.getSettleStakeReward()!=null) summary.setSettleStakeReward(configChange.getSettleStakeReward());
            if(configChange.getBlockReward()!=null) summary.setBlockReward(configChange.getBlockReward());
            if(configChange.getStakeReward()!=null) summary.setStakeReward(configChange.getStakeReward());
            if(configChange.getAvgPackTime()!=null) summary.setAvgPackTime(configChange.getAvgPackTime());
        }
        networkStatCache.updateByEpochChange(summary);
    }
}
