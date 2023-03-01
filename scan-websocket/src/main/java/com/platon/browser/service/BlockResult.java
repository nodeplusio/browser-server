package com.platon.browser.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.util.AddressLatToHexSerializer;
import com.platon.browser.util.BigIntegerToHexSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.List;

@Data
@NoArgsConstructor
public class BlockResult {
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger number;
    private String hash;
    private String parentHash;
    private String nonce;
    private String sha3Uncles;
    private String logsBloom;
    private String transactionsRoot;
    private String stateRoot;
    private String receiptsRoot;
    @JsonSerialize(using = AddressLatToHexSerializer.class)
    private String miner;
    private String mixHash;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger difficulty;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger totalDifficulty;
    private String extraData;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger size;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger gasLimit;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger gasUsed;
    @JsonSerialize(using = BigIntegerToHexSerializer.class)
    private BigInteger timestamp;
    private List<String> uncles;

    public BlockResult(BlockOrigin result) {
//        BeanUtils.copyProperties(result, this);
        number = result.getNumber();
    }
}
