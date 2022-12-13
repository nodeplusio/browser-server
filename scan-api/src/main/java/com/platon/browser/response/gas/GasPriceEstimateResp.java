package com.platon.browser.response.gas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GasPriceEstimateResp {

    private Boolean health;

    @JsonProperty("block_number")
    private Long blockNumber;

    @JsonProperty("block_time")
    private Double blockTime;

    private Double slow;
    private Double standard;
    private Double fast;
    private Double instant;
}
