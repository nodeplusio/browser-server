package com.platon.browser.service.staking;

import com.alibaba.fastjson.JSON;
import com.platon.browser.dao.entity.*;
import com.platon.browser.dao.mapper.NodeHistoryDeleAnnualizedRateMapper;
import com.platon.browser.dao.mapper.NodeHistoryTotalAndStatDelegateValueMapper;
import com.platon.browser.dao.mapper.TxBakMapper;
import com.platon.browser.dao.mapper.TxDelegationRewardBakMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
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
        example1.createCriteria().andTypeIn(Arrays.asList(1000, 1002, 1003, 1004, 1005, 1006))
                .andTimeGreaterThanOrEqualTo(statsDay).andTimeLessThan(nextDay);
        List<TxBakWithBLOBs> txBakList = txBakMapper.selectByExampleWithBLOBs(example1);

        if (txBakList.isEmpty()) {
            return;
        }
        NodeHistoryTotalAndStatDelegateValueExample preExample = new NodeHistoryTotalAndStatDelegateValueExample();
        preExample.createCriteria().andDateEqualTo(DateUtils.addDays(statsDay, -1));
        List<NodeHistoryTotalAndStatDelegateValue> preList = nodeHistoryTotalAndStatDelegateValueMapper.selectByExample(preExample);
        Map<String, NodeHistoryTotalAndStatDelegateValue> preMap = preList.stream()
                .collect(Collectors.toMap(NodeHistoryTotalAndStatDelegateValue::getNodeId, Function.identity()));
        Map<String, NodeHistoryTotalAndStatDelegateValue> newMap = new HashMap<>();
        for (TxBakWithBLOBs txBak : txBakList) {
            TxInfo txInfo = JSON.parseObject(txBak.getInfo(), TxInfo.class);
            String nodeId = txInfo.getNodeId();
            NodeHistoryTotalAndStatDelegateValue value = newMap.get(nodeId);
            NodeHistoryTotalAndStatDelegateValue pre = preMap.get(nodeId);
            if (value == null) {
                value = new NodeHistoryTotalAndStatDelegateValue();
                if (pre == null) {
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
                } else {
                    BeanUtils.copyProperties(pre, value);
                }
            }
            value.setDate(statsDay);

            BigDecimal amount = txInfo.getAmount();
            if (amount == null) continue;

            if (txBak.getType() == 1000 || txBak.getType() == 1002) {
                value.setStatStakingValueCount(value.getStatStakingValueCount() + 1);
                value.setStatStakingValueTotal(value.getStatStakingValueTotal().add(amount));
                value.setStatStakingValueMax(value.getStatStakingValueMax().max(value.getStatStakingValueTotal()));
                if (value.getStatStakingValueMin().compareTo(BigDecimal.ZERO) == 0
                        || value.getStatStakingValueTotal().compareTo(value.getStatStakingValueMin()) < 0) {
                    value.setStatStakingValueMin(value.getStatStakingValueTotal());
                }
            } else if (txBak.getType() == 1003) {
                value.setStatStakingValueTotal(value.getStatStakingValueTotal().subtract(amount));
                value.setStatStakingValueMax(value.getStatStakingValueMax().max(value.getStatStakingValueTotal()));
                if (value.getStatStakingValueMin().compareTo(BigDecimal.ZERO) == 0
                        || value.getStatStakingValueTotal().compareTo(value.getStatStakingValueMin()) < 0) {
                    value.setStatStakingValueMin(value.getStatStakingValueTotal());
                }
            } else if (txBak.getType() == 1004) {
                value.setStatDelegateValueCount(value.getStatDelegateValueCount() + 1);
                value.setStatDelegateValueTotal(value.getStatDelegateValueTotal().add(amount));
                value.setStatDelegateValueMax(value.getStatDelegateValueMax().max(value.getStatDelegateValueTotal()));
                if (value.getStatDelegateValueMin().compareTo(BigDecimal.ZERO) == 0
                        || value.getStatDelegateValueTotal().compareTo(value.getStatDelegateValueMin()) < 0) {
                    value.setStatDelegateValueMin(value.getStatDelegateValueTotal());
                }
            } else if (txBak.getType() == 1005 || txBak.getType() == 1006) {
                value.setStatDelegateValueTotal(value.getStatDelegateValueTotal().subtract(amount));
                value.setStatDelegateValueMax(value.getStatDelegateValueMax().max(value.getStatDelegateValueTotal()));
                if (value.getStatDelegateValueMin().compareTo(BigDecimal.ZERO) == 0
                        || value.getStatDelegateValueTotal().compareTo(value.getStatDelegateValueMin()) < 0) {
                    value.setStatDelegateValueMin(value.getStatDelegateValueTotal());
                }
            }
        }

        NodeHistoryTotalAndStatDelegateValueExample example = new NodeHistoryTotalAndStatDelegateValueExample();
        example.createCriteria().andDateEqualTo(statsDay);
        List<NodeHistoryTotalAndStatDelegateValue> list = nodeHistoryTotalAndStatDelegateValueMapper.selectByExample(example);
        Map<String, NodeHistoryTotalAndStatDelegateValue> map = list.stream()
                .collect(Collectors.toMap(NodeHistoryTotalAndStatDelegateValue::getNodeId, Function.identity()));
        for (NodeHistoryTotalAndStatDelegateValue value : newMap.values()) {
            NodeHistoryTotalAndStatDelegateValue pre = preMap.get(value.getNodeId());
            if (value.getStatStakingValueCount() > 0) {
                if (pre == null) {
                    value.setStatStakingValueAvg(value.getStatStakingValueTotal().divideToIntegralValue(BigDecimal.valueOf(value.getStatStakingValueCount())));
                } else {
                    BigDecimal avg = value.getStatStakingValueTotal().subtract(pre.getStatStakingValueTotal())
                            .divideToIntegralValue(BigDecimal.valueOf(value.getStatStakingValueCount() - pre.getStatStakingValueCount()));
                    value.setStatStakingValueAvg(pre.getStatStakingValueTotal().add(avg));
                }
            }
            if (value.getStatDelegateValueCount() > 0) {
                if (pre == null) {
                    value.setStatDelegateValueAvg(value.getStatDelegateValueTotal().divideToIntegralValue(BigDecimal.valueOf(value.getStatDelegateValueCount())));
                } else {
                    BigDecimal avg = value.getStatDelegateValueTotal().subtract(pre.getStatDelegateValueTotal())
                            .divideToIntegralValue(BigDecimal.valueOf(value.getStatDelegateValueCount() - pre.getStatDelegateValueCount()));
                    value.setStatStakingValueAvg(pre.getStatDelegateValueTotal().add(avg));
                }
            }
            if (map.containsKey(value.getNodeId())) {
                value.setUpdateTime(new Date());
                nodeHistoryTotalAndStatDelegateValueMapper.updateByPrimaryKey(value);
            } else {
                nodeHistoryTotalAndStatDelegateValueMapper.insert(value);
            }
        }
        for (NodeHistoryTotalAndStatDelegateValue value : preMap.values()) {
            if (!newMap.containsKey(value.getNodeId())) {
                value.setDate(statsDay);
                if (map.containsKey(value.getNodeId())) {
                    value.setUpdateTime(new Date());
                    nodeHistoryTotalAndStatDelegateValueMapper.updateByPrimaryKey(value);
                } else {
                    nodeHistoryTotalAndStatDelegateValueMapper.insert(value);
                }
            }
        }
    }

    public void statsNodeHistoryDeleAnnualizedRate(Date date) {
        Date statsDay = DateUtils.truncate(date, Calendar.DATE);
        Date nextDay = DateUtils.addDays(statsDay, 1);
        TxDelegationRewardBakExample rewardBakExample = new TxDelegationRewardBakExample();
        rewardBakExample.createCriteria()
                .andTimeGreaterThanOrEqualTo(statsDay).andTimeLessThan(nextDay);
        List<TxDelegationRewardBakWithBLOBs> rewardList = txDelegationRewardBakMapper.selectByExampleWithBLOBs(rewardBakExample);

        if (rewardList.isEmpty()) {
            return;
        }

        NodeHistoryTotalAndStatDelegateValueExample example = new NodeHistoryTotalAndStatDelegateValueExample();
        example.createCriteria().andDateEqualTo(statsDay);
        Map<String, NodeHistoryTotalAndStatDelegateValue> map = nodeHistoryTotalAndStatDelegateValueMapper.selectByExample(example)
                .stream().collect(Collectors.toMap(NodeHistoryTotalAndStatDelegateValue::getNodeId, Function.identity()));

        Map<String, NodeHistoryDeleAnnualizedRate> newMap = new HashMap<>();
        for (TxDelegationRewardBakWithBLOBs txBak : rewardList) {
            List<RewardNode> list = JSON.parseArray(txBak.getExtra(), RewardNode.class);
            for (RewardNode rewardNode : list) {
                String nodeId = rewardNode.getNodeId();
                NodeHistoryDeleAnnualizedRate value = newMap.get(nodeId);
                if (value == null) {
                    value = new NodeHistoryDeleAnnualizedRate();
                    value.setDate(statsDay);
                    value.setNodeId(nodeId);
                    value.setDeleReward(BigDecimal.ZERO);
                    value.setDeleAnnualizedRateMin(BigDecimal.ZERO);
                    value.setDeleAnnualizedRateAvg(BigDecimal.ZERO);
                    value.setDeleAnnualizedRateMax(BigDecimal.ZERO);
                    newMap.put(nodeId, value);
                }
                value.setDeleReward(value.getDeleReward().add(rewardNode.getReward()));
            }
        }

        NodeHistoryDeleAnnualizedRateExample example2 = new NodeHistoryDeleAnnualizedRateExample();
        example2.createCriteria().andDateEqualTo(statsDay);
        Map<String, NodeHistoryDeleAnnualizedRate> rateMap = nodeHistoryDeleAnnualizedRateMapper.selectByExample(example2)
                .stream().collect(Collectors.toMap(NodeHistoryDeleAnnualizedRate::getNodeId, Function.identity()));

        for (NodeHistoryDeleAnnualizedRate value : newMap.values()) {
            String nodeId = value.getNodeId();
            NodeHistoryTotalAndStatDelegateValue totalValue = map.get(nodeId);
            if (totalValue == null) continue;
            BigDecimal deleAnnualizedRate = BigDecimal.valueOf(365).multiply(value.getDeleReward())
                    .divide(totalValue.getStatStakingValueAvg().add(totalValue.getStatDelegateValueAvg()), 12, RoundingMode.DOWN);
            value.setDeleAnnualizedRateAvg(deleAnnualizedRate);
            value.setDeleAnnualizedRateMax(deleAnnualizedRate);
            value.setDeleAnnualizedRateMin(deleAnnualizedRate);
            if (rateMap.containsKey(nodeId)) {
                value.setUpdateTime(new Date());
                nodeHistoryDeleAnnualizedRateMapper.updateByPrimaryKey(value);
            } else {
                nodeHistoryDeleAnnualizedRateMapper.insert(value);
            }
        }
    }

}
