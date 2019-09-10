package com.platon.browser.service;

import com.platon.browser.TestBase;
import com.platon.browser.client.PlatonClient;
import com.platon.browser.client.SpecialContractApi;
import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.engine.BlockChain;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.web3j.platon.contracts.NodeContract;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @Auther: Chendongming
 * @Date: 2019/9/9 20:30
 * @Description:
 */
@RunWith(MockitoJUnitRunner.class)
public class CandidateServiceTest extends TestBase {

    @Mock
    private CandidateService candidateService;
    @Mock
    private BlockChain blockChain;
    @Mock
    private BlockChainConfig chainConfig;
    @Mock
    private PlatonClient client;
    @Mock
    private NodeContract nodeContract;
    @Mock
    private DbService dbService;
    @Mock
    private SpecialContractApi sca;

    @Before
    public void setup() throws Exception {
        ReflectionTestUtils.setField(candidateService, "blockChain", blockChain);
        ReflectionTestUtils.setField(candidateService, "chainConfig", chainConfig);
        ReflectionTestUtils.setField(candidateService, "client", client);
        ReflectionTestUtils.setField(candidateService, "dbService", dbService);
        ReflectionTestUtils.setField(candidateService, "sca", sca);
        when(sca.getHistoryVerifierList(any(),any(BigInteger.class))).thenReturn(verifiers);
        when(sca.getHistoryValidatorList(any(),any(BigInteger.class))).thenReturn(validators);
        when(chainConfig.getExpectBlockCount()).thenReturn(BigInteger.TEN);
        when(chainConfig.getDefaultStakingLockedAmount()).thenReturn(BigDecimal.valueOf(10000000));
        when(candidateService.getCurCandidates()).thenReturn(candidates);
        when(candidateService.getCurVerifiers()).thenReturn(verifiers);
        when(candidateService.getCurValidators()).thenReturn(validators);
        when(candidateService.getInitParam()).thenCallRealMethod();
    }

    @Test
    public void testInitParam() throws Exception {
        CandidateService.InitParam initParam = candidateService.getInitParam();
        assertEquals(verifiers.size(),initParam.getNodes().size());
    }

}
