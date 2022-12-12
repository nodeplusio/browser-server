package com.platon.browser.service.delegation;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.platon.browser.dao.entity.DelegationLog;
import com.platon.browser.dao.entity.DelegationLogExample;
import com.platon.browser.dao.entity.TxBakExample;
import com.platon.browser.dao.entity.TxBakWithBLOBs;
import com.platon.browser.dao.mapper.DelegationLogMapper;
import com.platon.browser.dao.mapper.TxBakMapper;
import com.platon.browser.service.staking.TxInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DelegationLogGenerateService {

    @Resource
    private TxBakMapper txBakMapper;
    @Resource
    private DelegationLogMapper delegationLogMapper;

    public void generateDelegationLog() {
        DelegationLogExample example = new DelegationLogExample();
        example.setOrderByClause("id desc");
        PageHelper.startPage(1, 1);
        List<DelegationLog> delegationLogs = delegationLogMapper.selectByExample(example);
        long maxId;
        if (delegationLogs.isEmpty()) {
            maxId = 0L;
        } else {
            maxId = delegationLogs.get(0).getId();
        }

        TxBakExample example1 = new TxBakExample();
        example1.createCriteria().andTypeIn(Arrays.asList(1004, 1005))
                .andIdGreaterThan(maxId);
        PageHelper.startPage(1, 100);
        List<TxBakWithBLOBs> txBakList = txBakMapper.selectByExampleWithBLOBs(example1);
        if (txBakList.isEmpty()) {
            return;
        }

        List<Long> ids = txBakList.stream().map(TxBakWithBLOBs::getId).collect(Collectors.toList());
        DelegationLogExample deleteExample = new DelegationLogExample();
        deleteExample.createCriteria().andIdIn(ids);
        delegationLogMapper.deleteByExample(deleteExample);
        delegationLogMapper.batchInsert(txBakList.stream().map(this::covert).collect(Collectors.toList()));
    }

    private DelegationLog covert(TxBakWithBLOBs txBakWithBLOBs) {
        DelegationLog delegationLog = new DelegationLog();
        delegationLog.setId(txBakWithBLOBs.getId());
        delegationLog.setTime(txBakWithBLOBs.getTime());
        delegationLog.setHash(txBakWithBLOBs.getHash());
        delegationLog.setType(txBakWithBLOBs.getType());

        TxInfo txInfo = JSON.parseObject(txBakWithBLOBs.getInfo(), TxInfo.class);
        delegationLog.setNodeId(txInfo.getNodeId());
        return delegationLog;
    }
}
