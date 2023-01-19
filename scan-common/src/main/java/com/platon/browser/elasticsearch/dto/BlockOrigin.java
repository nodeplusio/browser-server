package com.platon.browser.elasticsearch.dto;

import com.platon.protocol.core.methods.response.PlatonBlock;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
public class BlockOrigin {
    private BigInteger number;
    private String hash;
    private String parentHash;
    private String nonce;
    private String sha3Uncles;
    private String logsBloom;
    private String transactionsRoot;
    private String stateRoot;
    private String receiptsRoot;
    private String author;
    private String miner;
    private String mixHash;
    private BigInteger difficulty;
    private BigInteger totalDifficulty;
    private String extraData;
    private BigInteger size;
    private BigInteger gasLimit;
    private BigInteger gasUsed;
    private BigInteger timestamp;
    private List<String> uncles;
    private List<String> sealFields;

    public BlockOrigin(PlatonBlock.Block block) {
        number = block.getNumber();
        hash = block.getHash();
        parentHash = block.getParentHash();
        sha3Uncles = block.getSha3Uncles();
        logsBloom = block.getLogsBloom();
        transactionsRoot = block.getTransactionsRoot();
        stateRoot = block.getStateRoot();
        receiptsRoot = block.getReceiptsRoot();
        author = block.getAuthor();
        miner = block.getMiner();
        mixHash = block.getMixHash();
        nonce = block.getNonceRaw();
        if (block.getDifficultyRaw() != null) {
            difficulty = block.getDifficulty();
        }
        if (block.getTotalDifficultyRaw() != null) {
            totalDifficulty = block.getTotalDifficulty();
        }
        if (block.getTotalDifficultyRaw() != null) {
            size = block.getSize();
        }
        if (block.getGasLimitRaw() != null) {
            gasLimit = block.getGasLimit();
        }
        if (block.getGasLimitRaw() != null) {
            gasUsed = block.getGasUsed();
        }
        if (block.getTimestampRaw() != null) {
            timestamp = block.getTimestamp();
        }
        extraData = block.getExtraData();
        uncles = block.getUncles();
        sealFields = block.getSealFields();
    }
}
