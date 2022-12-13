package com.platon.browser.request.staking;

import com.platon.browser.request.PageReq;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 大额交易的列表的请求对象
 */
@Data
public class TransactionListByValueSelectiveReq extends PageReq {

	@NotNull
	private BigDecimal value;

}
