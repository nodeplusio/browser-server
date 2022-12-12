package com.platon.browser.response.staking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platon.browser.dao.entity.NodeHistoryDeleAnnualizedRate;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 节点历史出块详情
 */
@Data
public class DeleAnnualizedRateHistoryByNodeResult {

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("time__date")
    private Date date;

    private BigDecimal deleAnnualizedRateMax;

    private BigDecimal deleAnnualizedRateMin;

    private BigDecimal deleAnnualizedRateAvg;

    public DeleAnnualizedRateHistoryByNodeResult(NodeHistoryDeleAnnualizedRate entity) {
        BeanUtils.copyProperties(entity, this);
    }
}
