package com.platon.browser.response.gas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;

@Data
public class CountGroupByGasPriceResp {

    private Object key;
    @JsonProperty("doc_count")
    private Long docCount;

    public CountGroupByGasPriceResp(MultiBucketsAggregation.Bucket bucket) {
        key = bucket.getKey();
        docCount = bucket.getDocCount();
    }

}
