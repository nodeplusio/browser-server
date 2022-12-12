package com.platon.browser.task;

import cn.hutool.core.convert.Convert;
import com.platon.browser.service.staking.StakingStatsService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

@Component
@Slf4j
public class StakingStatsTask {

    @Resource
    private StakingStatsService stakingStatsService;

    @XxlJob("statsTotalValueHistory")
    public void statsTotalValueHistory() {
        try {
            int day = Convert.toInt(XxlJobHelper.getJobParam(), 1);
            for (int i = day - 1; i >= 0; i--) {
                stakingStatsService.statsTotalValueHistory(DateUtils.addDays(new Date(), -i));
            }
            XxlJobHelper.handleSuccess("统计数据成功");
        } catch (Exception e) {
            log.error("统计数据异常", e);
            throw e;
        }
    }

    @XxlJob("statsNodeHistoryDeleAnnualizedRate")
    public void statsNodeHistoryDeleAnnualizedRate() {
        try {
            int day = Convert.toInt(XxlJobHelper.getJobParam(), 1);
            for (int i = day - 1; i >= 0; i--) {
                stakingStatsService.statsNodeHistoryDeleAnnualizedRate(DateUtils.addDays(new Date(), -i));
            }
            XxlJobHelper.handleSuccess("统计数据成功");
        } catch (Exception e) {
            log.error("统计数据异常", e);
            throw e;
        }
    }

}
