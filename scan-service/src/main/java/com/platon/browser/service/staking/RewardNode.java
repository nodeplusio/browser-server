package com.platon.browser.service.staking;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RewardNode {
	private String nodeName;
	private BigDecimal reward;
	private String nodeId;
}