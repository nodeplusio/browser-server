package com.platon.browser.param;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

public class ProposalCancelParamTest {

	@Test
	public void testInit() {
		ProposalCancelParam param = ProposalCancelParam.builder()
                .canceledProposalID("333")
                .endVotingRound(BigDecimal.ONE)
                .nodeName("dfdfsf")
                .pIDID("3r343")
                .verifier("sfsdfdf")
                .build();
		assertTrue("sfsdfdf".equals(param.getVerifier()));
	}

}
