package com.platon.browser.service.staking;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class TxInfo{
	private String nodeName;
	private BigDecimal amount;
	private BigInteger stakingBlockNum;
	private int type;
	private String nodeId;
}