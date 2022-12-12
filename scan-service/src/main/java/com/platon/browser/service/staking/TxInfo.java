package com.platon.browser.service.staking;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TxInfo{
	private String nodeName;
	private BigDecimal amount;
	private int stakingBlockNum;
	private int type;
	private String nodeId;
}