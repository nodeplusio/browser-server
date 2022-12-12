package com.platon.browser.service.staking;

import com.alibaba.fastjson.JSON;
import com.platon.browser.dao.entity.*;
import com.platon.browser.dao.mapper.NodeHistoryDeleAnnualizedRateMapper;
import com.platon.browser.dao.mapper.NodeHistoryTotalAndStatDelegateValueMapper;
import com.platon.browser.dao.mapper.TxBakMapper;
import com.platon.browser.dao.mapper.TxDelegationRewardBakMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StakingStatsService {

    @Resource
    private TxBakMapper txBakMapper;
    @Resource
    private NodeHistoryTotalAndStatDelegateValueMapper nodeHistoryTotalAndStatDelegateValueMapper;
    @Resource
    private TxDelegationRewardBakMapper txDelegationRewardBakMapper;
    @Resource
    private NodeHistoryDeleAnnualizedRateMapper nodeHistoryDeleAnnualizedRateMapper;

    public void statsTotalValueHistory(Date date) {
        Date statsDay = DateUtils.truncate(date, Calendar.DATE);
        Date nextDay = DateUtils.addDays(statsDay, 1);
        TxBakExample example1 = new TxBakExample();
        example1.createCriteria().andTypeIn(Arrays.asList(1000, 1002, 1004))
                .andTimeGreaterThanOrEqualTo(statsDay).andTimeLessThan(nextDay);
        List<TxBakWithBLOBs> txBakList = txBakMapper.selectByExampleWithBLOBs(example1);

        Map<String, NodeHistoryTotalAndStatDelegateValue> newMap = new HashMap<>();
        for (TxBakWithBLOBs txBak : txBakList) {
            TxInfo txInfo = JSON.parseObject(txBak.getInfo(), TxInfo.class);
            String nodeId = txInfo.getNodeId();
            NodeHistoryTotalAndStatDelegateValue value = newMap.get(nodeId);
            if (value == null) {
                value = new NodeHistoryTotalAndStatDelegateValue();
                value.setDate(statsDay);
                value.setNodeId(nodeId);
                value.setStatDelegateValueMax(BigDecimal.ZERO);
                value.setStatDelegateValueMin(BigDecimal.ZERO);
                value.setStatDelegateValueAvg(BigDecimal.ZERO);
                value.setStatDelegateValueTotal(BigDecimal.ZERO);
                value.setStatDelegateValueCount(0);
                value.setStatStakingValueMax(BigDecimal.ZERO);
                value.setStatStakingValueMin(BigDecimal.ZERO);
                value.setStatStakingValueAvg(BigDecimal.ZERO);
                value.setStatStakingValueTotal(BigDecimal.ZERO);
                value.setStatStakingValueCount(0);
                newMap.put(nodeId, value);
            }

            BigDecimal amount = txInfo.getAmount();
            if (txBak.getType() == 1004) {
                value.setStatStakingValueCount(value.getStatStakingValueCount() + 1);
                value.setStatStakingValueTotal(value.getStatStakingValueTotal().add(amount));
                value.setStatStakingValueMax(value.getStatStakingValueMax().max(amount));
                if (value.getStatStakingValueMin().compareTo(BigDecimal.ZERO) == 0
                        || amount.compareTo(value.getStatStakingValueMin()) < 0) {
                    value.setStatStakingValueMin(amount);
                }
            } else {
                value.setStatDelegateValueCount(value.getStatDelegateValueCount() + 1);
                value.setStatDelegateValueTotal(value.getStatDelegateValueTotal().add(amount));
                value.setStatDelegateValueMax(value.getStatDelegateValueMax().max(amount));
                if (value.getStatDelegateValueMin().compareTo(BigDecimal.ZERO) == 0
                        || amount.compareTo(value.getStatDelegateValueMin()) < 0) {
                    value.setStatDelegateValueMin(amount);
                }
            }
        }

        NodeHistoryTotalAndStatDelegateValueExample example = new NodeHistoryTotalAndStatDelegateValueExample();
        example.createCriteria().andDateEqualTo(statsDay);
        List<NodeHistoryTotalAndStatDelegateValue> list = nodeHistoryTotalAndStatDelegateValueMapper.selectByExample(example);
        Map<String, NodeHistoryTotalAndStatDelegateValue> map = list.stream().collect(Collectors.toMap(NodeHistoryTotalAndStatDelegateValue::getNodeId, Function.identity()));
        for (NodeHistoryTotalAndStatDelegateValue value : newMap.values()) {
            if (value.getStatStakingValueCount() > 0) {
                value.setStatStakingValueAvg(value.getStatStakingValueTotal().divideToIntegralValue(BigDecimal.valueOf(value.getStatStakingValueCount())));
            }
            if (value.getStatDelegateValueCount() > 0) {
                value.setStatDelegateValueAvg(value.getStatDelegateValueTotal().divideToIntegralValue(BigDecimal.valueOf(value.getStatDelegateValueCount())));
            }
            if (map.containsKey(value.getNodeId())) {
                nodeHistoryTotalAndStatDelegateValueMapper.updateByPrimaryKey(value);
            } else {
                nodeHistoryTotalAndStatDelegateValueMapper.insert(value);
            }
        }
    }

    public void statsNodeHistoryDeleAnnualizedRate(Date date) {
        Date statsDay = DateUtils.truncate(date, Calendar.DATE);
        Date nextDay = DateUtils.addDays(statsDay, 1);
        TxDelegationRewardBakExample example1 = new TxDelegationRewardBakExample();
        example1.createCriteria()
                .andTimeGreaterThanOrEqualTo(statsDay).andTimeLessThan(nextDay);
        List<TxDelegationRewardBakWithBLOBs> txBakList = txDelegationRewardBakMapper.selectByExampleWithBLOBs(example1);

        NodeHistoryTotalAndStatDelegateValueExample example = new NodeHistoryTotalAndStatDelegateValueExample();
        example.createCriteria().andDateEqualTo(statsDay);
        Map<String, NodeHistoryTotalAndStatDelegateValue> map = nodeHistoryTotalAndStatDelegateValueMapper.selectByExample(example)
                .stream().collect(Collectors.toMap(NodeHistoryTotalAndStatDelegateValue::getNodeId, Function.identity()));

        Map<String, NodeHistoryDeleAnnualizedRate> newMap = new HashMap<>();
        for (TxDelegationRewardBakWithBLOBs txBak : txBakList) {
            List<RewardNode> list = JSON.parseArray(txBak.getExtra(), RewardNode.class);
            for (RewardNode rewardNode : list) {
                String nodeId = rewardNode.getNodeId();
                NodeHistoryDeleAnnualizedRate value = newMap.get(nodeId);
                if (value == null) {
                    value = new NodeHistoryDeleAnnualizedRate();
                    value.setDate(statsDay);
                    value.setNodeId(nodeId);
                    value.setDeleAnnualizedRateMin(BigDecimal.ZERO);
                    value.setDeleAnnualizedRateAvg(BigDecimal.ZERO);
                    value.setDeleAnnualizedRateMax(BigDecimal.ZERO);
                    newMap.put(nodeId, value);
                }
                NodeHistoryTotalAndStatDelegateValue totalValue = map.get(nodeId);
                if (totalValue == null) continue;
                BigDecimal multiply = BigDecimal.valueOf(365).multiply(rewardNode.getReward());
                value.setDeleAnnualizedRateAvg(multiply
                        .divide(totalValue.getStatStakingValueAvg().add(totalValue.getStatDelegateValueAvg()), 18, RoundingMode.DOWN));
                value.setDeleAnnualizedRateMax(multiply
                        .divide(totalValue.getStatStakingValueMax().add(totalValue.getStatDelegateValueMax()), 18, RoundingMode.DOWN));
                value.setDeleAnnualizedRateMin(multiply
                        .divide(totalValue.getStatStakingValueMin().add(totalValue.getStatDelegateValueMin()), 18, RoundingMode.DOWN));
            }
        }

        NodeHistoryDeleAnnualizedRateExample example2 = new NodeHistoryDeleAnnualizedRateExample();
        example2.createCriteria().andDateEqualTo(statsDay);
        Map<String, NodeHistoryDeleAnnualizedRate> rateMap = nodeHistoryDeleAnnualizedRateMapper.selectByExample(example2)
                .stream().collect(Collectors.toMap(NodeHistoryDeleAnnualizedRate::getNodeId, Function.identity()));

        for (NodeHistoryDeleAnnualizedRate value : newMap.values()) {
            if (rateMap.containsKey(value.getNodeId())) {
                nodeHistoryDeleAnnualizedRateMapper.updateByPrimaryKey(value);
            } else {
                nodeHistoryDeleAnnualizedRateMapper.insert(value);
            }
        }
    }

}
