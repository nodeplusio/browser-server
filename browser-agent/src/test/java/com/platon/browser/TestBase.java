package com.platon.browser;//package com.platon.browser;



import com.platon.browser.common.complement.dto.epoch.NewBlock;
import com.platon.browser.common.complement.dto.delegate.DelegateCreate;
import com.platon.browser.common.complement.dto.delegate.DelegateExit;
import com.platon.browser.common.complement.dto.epoch.Consensus;
import com.platon.browser.common.complement.dto.epoch.Election;
import com.platon.browser.common.complement.dto.epoch.Settle;
import com.platon.browser.common.complement.dto.proposal.ProposalText;
import com.platon.browser.common.complement.dto.proposal.ProposalUpgradeOrCancel;
import com.platon.browser.common.complement.dto.proposal.ProposalVote;
import com.platon.browser.common.complement.dto.slash.Report;
import com.platon.browser.common.complement.dto.stake.StakeCreate;
import com.platon.browser.common.complement.dto.stake.StakeExit;
import com.platon.browser.common.complement.dto.stake.StakeIncrease;
import com.platon.browser.common.complement.dto.stake.StakeModify;
import com.platon.browser.common.complement.dto.statistic.AddressStatChange;
import com.platon.browser.common.complement.dto.statistic.NetworkStatChange;
import com.platon.browser.common.enums.AppStatus;
import com.platon.browser.dto.ProposalMarkDownDto;
import com.platon.browser.persistence.dao.mapper.StatisticBusinessMapper;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Auther: Chendongming
 * @Date: 2019/9/7 16:35
 * @Description:
 */
public class TestBase {

    static {
        System.setProperty(AppStatus.class.getName(),AppStatus.STOP.name());
    }

    public StakeCreate stakingParam(){
        StakeCreate createStakingParam = StakeCreate.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .stakingHes(new BigDecimal("5000"))
                .nodeName("testNode01")
                .externalId("externalId01")
                .benefitAddr("0xff48d9712d8a55bf603dab28f4645b6985696a61")
                .programVersion("1794")
                .bigVersion("1700")
                .webSite("web_site01")
                .details("details01")
                .isInit(1)
                .stakingBlockNum(new BigInteger("200"))
                .stakingTxIndex(0)
                .stakingAddr("0xb58c7fd25437e2fcf038b948963ffb80276bd44d")
                .joinTime(new Date(System.currentTimeMillis()))
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .build();
        return createStakingParam;
    }

    public StakeModify modifyStakingParam(){
        StakeModify modifyStakingParam = StakeModify.builder()
                .nodeName("testNode02")
                .externalId("externalId02")
                .benefitAddr("0xff48d9712d8a55bf603dab28f4645b6985696a61")
                .webSite("web_site01")
                .details("details01")
                .isInit(2)
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .stakingBlockNum(new BigInteger("200"))
                .bNum(new BigInteger("300"))
                .time(new java.sql.Date(System.currentTimeMillis()))
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .build();
        return modifyStakingParam;
    }

    public StakeIncrease addStakingParam(){
        StakeIncrease addStakingParam = StakeIncrease.builder()
                .amount(new BigDecimal("500000000000000000000000000"))
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .bNum(new BigInteger("300"))
                .time(new Date(System.currentTimeMillis()))
                .stakingBlockNum(new BigInteger("200"))
                .build();
        return addStakingParam;
    }

    public StakeExit withdrewStakingParam(){
        StakeExit withdrewStakingParam = StakeExit.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .bNum(new BigInteger("300"))
                .stakingBlockNum(new BigInteger("200"))
                .time(new Date(System.currentTimeMillis()))
                .stakingReductionEpoch(3)
                .build();
        return withdrewStakingParam;
    }

    public Report reportDuplicateSignParam(){
        Report reportDuplicateSignParam = Report.builder()
                .time(new Date(System.currentTimeMillis()))
                .settingEpoch(3)
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .stakingBlockNum(new BigInteger("200"))
                .bNum(new BigInteger("200"))
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .slashRate(new BigDecimal("0.5"))
                .slashData("json")
                .benefitAddr("0x02fba14f5e72092c8fca6ced087cd4e7be0d8fc5")
                .codeCurStakingLocked(new BigDecimal("50000000"))
                .codeNodeOptDesc("AMOUNT")
                .codeStatus(2)
                .codeRewardValue(new BigDecimal("1000000000"))
                .build();
        return  reportDuplicateSignParam;
    }

    public NewBlock blockParam(){
        NewBlock newBlockParam = NewBlock.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .blockRewardValue(new BigDecimal("100000000000"))
                .feeRewardValue(new BigDecimal("10000000000"))
                .stakingBlockNum(new BigInteger("200"))
                .build();
        return newBlockParam;
    }

    public Consensus consensusParam(){
        List <String> nodeIdList = new ArrayList <>();
        nodeIdList.add("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18");
        Consensus consensus =  Consensus.builder()
                .validatorList(nodeIdList)
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .expectBlockNum(new BigInteger("10"))
                .build();
        return consensus;
    }

    public Settle settleParam(){
        List<String> curVerifierList = new ArrayList <>();
        curVerifierList.add("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18");
        curVerifierList.add("0x0aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e7");
        List<String> preVerifierList = new ArrayList <>();
        preVerifierList.add("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18");
        preVerifierList.add("0x0aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e7");
        Settle settle = Settle.builder()
                .curVerifierList(curVerifierList)
                .preVerifierList(preVerifierList)
                .annualizedRate(Double.valueOf("10"))
                .annualizedRateInfo("json")
                .settingEpoch(3)
                .stakingLockEpoch(3)
                .stakingReward(new BigDecimal("100000000000"))
                .build();
        return settle;
    }

    public Election electionParam(){
        List<String> preVerifierList = new ArrayList <>();
        preVerifierList.add("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18");
        preVerifierList.add("0x0aa9805681d8f77c05f317efc141c97d5adb511ffb51f5a251d2d7a4a3a96d9a12adf39f06b702f0ccdff9eddc1790eb272dca31b0c47751d49b5931c58701e7");
        Election election = Election.builder()
                .bNum(new BigInteger("200"))
                .settingEpoch(3)
                .time(new Date(System.currentTimeMillis()))
                .preVerifierList(preVerifierList).build();
        return election;
    }

    public DelegateCreate delegateCreateParam(){
        DelegateCreate delegateCreate = DelegateCreate.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .blockNumber(new BigInteger("300"))
                .amount(new BigDecimal("100000000000000"))
                .sequence(new BigInteger("10000000"))
                .stakingBlockNumber(new BigInteger("200"))
                .txFrom("0xff48d9712d8a55bf603dab28f4645b6985696a61")
                .build();
        return  delegateCreate;
    }

    public DelegateExit delegateExitParam(){
        DelegateExit delegateExit = DelegateExit.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .amount(new BigDecimal("100000000000000"))
                .stakingBlockNumber(new BigInteger("200"))
                .minimumThreshold(new BigDecimal("500000"))
                .txFrom("0xff48d9712d8a55bf603dab28f4645b6985696a61")
                .codeDelegateHes(new BigDecimal("100000000000000"))
                .codeRmdelegateHes(new BigDecimal("50000000000000"))
                .codeDelegateLocked(new BigDecimal("100000000000000"))
                .codeRmDelegateLocked(new BigDecimal("50000000000000"))
                .codeDelegateReleased(new BigDecimal("50000000000000"))
                .codeRmDelegateReleased(new BigDecimal("200000000"))
                .codeIsHistory(2)
                .codeRealAmount(new BigDecimal("200000000"))
                .codeNodeIsLeave(false)
                .build();
        return delegateExit;
    }

    public ProposalText proposalTextParam(){
        ProposalText proposalText = ProposalText.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .pIDID("100")
                .url("https://github.com/danielgogo/PIPs/PIP-100.md")
                .pipNum("PIP-100")
                .endVotingBlock(new BigInteger("2000"))
                .topic("inquiry")
                .description("inquiry")
                .optDesc("ID|TITLE|TYPE|VERSION")
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .blockNumber(new BigInteger("300"))
                .timestamp(new Date(System.currentTimeMillis()))
                .stakingName("test01")
                .build();
        return proposalText;
    }



    public ProposalVote proposalVoteParam(){
        ProposalVote proposalVote = ProposalVote.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .bNum(new BigInteger("300"))
                .timestamp(new Date(System.currentTimeMillis()))
                .optDesc("ID|TITLE|OPTION|TYPE|VERSION")
                .proposalHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be7")
                .voteOption(1)
                .stakingName("test01")
                .build();
        return proposalVote;
    }


    public ProposalUpgradeOrCancel proposalUpgradeOrCancelParam(){
        ProposalUpgradeOrCancel proposalUpgradeOrCancel = ProposalUpgradeOrCancel.builder()
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .pIDID("100")
                .url("https://github.com/danielgogo/PIPs/PIP-100.md")
                .pipNum("PIP-100")
                .endVotingBlock(new BigInteger("2000"))
                .activeBlock(new BigInteger("3000"))
                .topic("inquiry")
                .description("inquiry")
                .optDesc("ID|TITLE|TYPE|VERSION")
                .txHash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be1")
                .blockNumber(new BigInteger("300"))
                .timestamp(new Date(System.currentTimeMillis()))
                .stakingName("test01")
                .newVersion("1000")
                .build();
        return proposalUpgradeOrCancel;
    }


    public AddressStatChange addressStatChangeParam(){
        AddressStatChange addressStatChange = AddressStatChange.builder()
                .address("0xff48d9712d8a55bf603dab28f4645b6985696a61")
                .contractCreate("0xff48d9712d8a55bf603dab28f4645b6985696a61")
                .contractCreatehash("0xaa85c7e85542ac8e8d2428c618130d02723138437d105d06d405f9e735469be9")
                .contractName("test")
                .delegateQty(1)
                .proposalQty(1)
                .stakingQty(1)
                .transferQty(1)
                .txQty(4)
                .type(1)
                .build();
        return addressStatChange;
    }

    public NetworkStatChange networkStatChangeParam(){
        NetworkStatChange networkStatChange = NetworkStatChange.builder()
                .id(1)
                .addIssueBegin(1L)
                .addIssueEnd(2L)
                .blockReward(new BigDecimal("100000000000000"))
                .curNumber(100L)
                .curTps(100)
                .issueValue(new BigDecimal("100000000000000"))
                .nextSettle(10L)
                .nodeId("0x20a090d94bc5015c9339a46e9ca5d80057a5ef25cc14e71cef67b502ec32949253f046821e80dfb6ff666ef0e0badf58fdb719368c38393f7c40ebcf18d8ed18")
                .nodeName("testNode01")
                .proposalQty(1)
                .stakingReward(new BigDecimal("100000000000000"))
                .turnValue(new BigDecimal("100000000000000"))
                .txQty(1)
                .maxTps(1)
                .build();
        return networkStatChange;
    }
}
