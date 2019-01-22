package com.platon.browser.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.platon.browser.client.Web3jClient;
import com.platon.browser.common.dto.AnalysisResult;
import com.platon.browser.common.util.TransactionAnalysis;
import com.platon.browser.dao.entity.Block;
import com.platon.browser.dao.entity.NodeRanking;
import com.platon.browser.dao.entity.NodeRankingExample;
import com.platon.browser.dao.mapper.BlockMapper;
import com.platon.browser.dao.mapper.NodeRankingMapper;
import com.platon.browser.dto.EventRes;
import com.platon.browser.job.DataCollectorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.web3j.platon.contracts.TicketContract;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * User: dongqile
 * Date: 2019/1/7
 * Time: 14:27
 */
@Component
public class BlockFilter {

    private static Logger log = LoggerFactory.getLogger(BlockFilter.class);


    @Autowired
    private Web3jClient web3jClient;

    @Value("${chain.id}")
    private String chainId;

    @Value("${platon.redis.key.block}")
    private String blockCacheKeyTemplate;

    @Value("${platon.redis.key.max-item}")
    private long maxItemNum;

    @Autowired
    private BlockMapper blockMapper;

    @Autowired
    private NodeRankingMapper nodeRankingMapper;

    @Transactional
    public Block analysis ( DataCollectorJob.AnalysisParam param ) {

        EthBlock ethBlock = param.ethBlock;
        List <Transaction> transactionsList = param.transactionList;
        List <TransactionReceipt> transactionReceiptList = param.transactionReceiptList;
        BigInteger publicKey = param.publicKey;
        Map <String, Object> transactionReceiptMap = param.transactionReceiptMap;

        Block block = new Block();
        log.debug("[EthBlock info :]" + JSON.toJSONString(ethBlock));
        log.debug("[List <TransactionReceipt> info :]" + JSONArray.toJSONString(transactionReceiptList));
        log.debug("[ List <Transaction> info :]" + JSONArray.toJSONString(transactionsList));
        if (!StringUtils.isEmpty(ethBlock)) {
            block.setNumber(ethBlock.getBlock().getNumber().longValue());
            if (String.valueOf(ethBlock.getBlock().getTimestamp().longValue()).length() == 10) {
                block.setTimestamp(new Date(ethBlock.getBlock().getTimestamp().longValue() * 1000L));
            } else {
                block.setTimestamp(new Date(ethBlock.getBlock().getTimestamp().longValue()));
            }
            block.setSize(ethBlock.getBlock().getSize().intValue());
            block.setChainId(chainId);
            if (ethBlock.getBlock().getTransactions().size() <= 0) {
                block.setEnergonAverage(BigInteger.ZERO.toString());
            } else {
                block.setEnergonAverage(ethBlock.getBlock().getGasUsed().divide(new BigInteger(String.valueOf(ethBlock.getBlock().getTransactions().size()))).toString());
            }
            block.setHash(ethBlock.getBlock().getHash());
            block.setEnergonLimit(ethBlock.getBlock().getGasLimit().toString());
            block.setEnergonUsed(ethBlock.getBlock().getGasUsed().toString());
            block.setTransactionNumber(ethBlock.getBlock().getTransactions().size() > 0 ? ethBlock.getBlock().getTransactions().size() : new Integer(0));
            block.setCreateTime(new Date());
            block.setUpdateTime(new Date());
            block.setMiner(ethBlock.getBlock().getMiner());
            block.setExtraData(ethBlock.getBlock().getExtraData());
            block.setParentHash(ethBlock.getBlock().getParentHash());
            block.setNonce(ethBlock.getBlock().getNonce().toString());
            block.setNodeId(publicKey.toString(16));
            NodeRankingExample nodeRankingExample = new NodeRankingExample();
            nodeRankingExample.createCriteria().andChainIdEqualTo(chainId).andNodeIdEqualTo(publicKey.toString(16)).andIsValidEqualTo(1);
            List <NodeRanking> dbList = nodeRankingMapper.selectByExample(nodeRankingExample);
            if (null != dbList && dbList.size() > 0) {
                block.setNodeName(dbList.get(0).getName());
            }

            String rewardWei = FilterTool.getBlockReward(ethBlock.getBlock().getNumber().toString());
            block.setBlockReward(rewardWei);
            //actuakTxCostSum
            BigInteger sum = new BigInteger("0");
            //blockVoteAmount
            BigInteger voteAmount = new BigInteger("0");
            //blockCampaignAmount
            BigInteger campaignAmount = new BigInteger("0");
            if (transactionsList.size() <= 0 && transactionReceiptList.size() <= 0) {
                block.setActualTxCostSum("0");
                block.setBlockVoteAmount(0L);
                block.setBlockCampaignAmount(0L);
                block.setBlockVoteNumber(0L);
                log.debug("[Block info :]" + JSON.toJSONString(block));
                log.debug("[this block is An empty block , transaction null !!!...]");
                log.debug("[exit BlockFilter !!!...]");
                blockMapper.insert(block);
                return block;
            }
            for (Transaction transaction : transactionsList) {
                if (null != transactionReceiptMap.get(transaction.getHash())) {
                    TransactionReceipt transactionReceipt = (TransactionReceipt) transactionReceiptMap.get(transaction.getHash());
                    sum = sum.add(transactionReceipt.getGasUsed().multiply(transaction.getGasPrice()));
                    AnalysisResult analysisResult = TransactionAnalysis.analysis(transaction.getInput(), true);
                    String type = TransactionAnalysis.getTypeName(analysisResult.getType());
                    if ("voteTicket".equals(type)) {
                        voteAmount.add(BigInteger.ONE);
                        //get tickVoteContract vote event
                        List <TicketContract.VoteTicketEventEventResponse> eventEventResponses =
                                web3jClient.getTicketContract().getVoteTicketEventEvents(transactionReceipt);
                        String event = eventEventResponses.get(0).param1;
                        EventRes eventRes = JSON.parseObject(event, EventRes.class);
                        //event objcet is jsonString , transform jsonObject <EventRes>
                        //EventRes get Data
                        block.setBlockVoteNumber(Long.valueOf(eventRes.getData()));
                    } else if ("candidateDeposit".equals(type)) {
                        campaignAmount.add(BigInteger.ONE);
                    }
                    block.setBlockVoteAmount(voteAmount.longValue());
                    block.setBlockCampaignAmount(campaignAmount.longValue());
                    block.setActualTxCostSum(sum.toString());
                }
            }
            //insert struct<block> into database
            blockMapper.insert(block);
        }
        return block;
    }


}