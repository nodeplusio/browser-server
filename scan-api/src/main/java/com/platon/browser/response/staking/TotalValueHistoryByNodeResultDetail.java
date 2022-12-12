package com.platon.browser.response.staking;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.platon.browser.dao.entity.NodeHistoryTotalAndStatDelegateValue;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 节点历史出块详情
 */
@Data
public class TotalValueHistoryByNodeResultDetail {

	@JsonFormat(pattern = "yyyy-MM-dd")
	@JsonProperty("time__date")
	private Date date;

	private BigDecimal totalValueMax;

	private BigDecimal totalValueMin;

	private BigDecimal totalValueAvg;

	private BigDecimal statStakingValueMax;

	private BigDecimal statStakingValueMin;

	private BigDecimal statStakingValueAvg;


	public TotalValueHistoryByNodeResultDetail(NodeHistoryTotalAndStatDelegateValue entity) {
		BeanUtils.copyProperties(entity, this);
		totalValueAvg = statStakingValueAvg.add(entity.getStatDelegateValueAvg());
		totalValueMax = statStakingValueMax.add(entity.getStatDelegateValueMax());
		totalValueMin = statStakingValueMin.add(entity.getStatDelegateValueMin());
	}

}
