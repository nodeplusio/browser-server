package com.platon.browser.task;

import cn.hutool.core.convert.Convert;
import com.platon.browser.service.delegation.DelegationLogGenerateService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class DelegationLogGenerateTask {

    @Resource
    private DelegationLogGenerateService delegationLogGenerateService;

    @XxlJob("generateDelegationLog")
    public void generateDelegationLog() {
        try {
            int count = Convert.toInt(XxlJobHelper.getJobParam(), 1);
            for (int i = 0; i < count; i++) {
                delegationLogGenerateService.generateDelegationLog();
            }
            XxlJobHelper.handleSuccess("历史委托列表数据生成成功");
        } catch (Exception e) {
            log.error("历史委托列表数据生成异常", e);
            throw e;
        }
    }

}
