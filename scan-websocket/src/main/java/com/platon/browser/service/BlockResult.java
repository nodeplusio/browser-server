package com.platon.browser.service;

import com.platon.protocol.core.methods.response.PlatonBlock;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BlockResult {
    private String number;
    private String hash;
    private String parentHash;
    private String nonce;
    private String sha3Uncles;
    private String logsBloom;
    private String transactionsRoot;
    private String stateRoot;
    private String receiptsRoot;
    private String miner;
    private String mixHash;
    private String difficulty;
    private String totalDifficulty;
    private String extraData;
    private String size;
    private String gasLimit;
    private String gasUsed;
    private String timestamp;
    private List<String> uncles;

    public BlockResult(PlatonBlock.Block result) {
        this.number = result.getNumberRaw();
        this.hash = result.getHash();
        this.parentHash = result.getParentHash();
        this.nonce = result.getNonceRaw();
        this.sha3Uncles = result.getSha3Uncles();
        this.logsBloom = result.getLogsBloom();
        this.transactionsRoot = result.getTransactionsRoot();
        this.stateRoot = result.getStateRoot();
        this.receiptsRoot = result.getReceiptsRoot();
        this.miner = result.getMiner();
        this.mixHash = result.getMixHash();
        this.difficulty = result.getDifficultyRaw();
        this.totalDifficulty = result.getTotalDifficultyRaw();
        this.extraData = result.getExtraData();
        this.size = result.getSizeRaw();
        this.gasLimit = result.getGasLimitRaw();
        this.gasUsed = result.getGasUsedRaw();
        this.timestamp = result.getTimestampRaw();
        this.uncles = result.getUncles();
    }
}
