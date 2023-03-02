package com.platon.browser.response.gas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;

@Data
public class CostGroupByTxTypeResp {
    private Object key;
    @JsonProperty("doc_count")
    private Long docCount;

    private Long costMin;
    private Long costAvg;
    private Long costMax;

    public CostGroupByTxTypeResp(MultiBucketsAggregation.Bucket bucket) {
        key = bucket.getKey();
        docCount = bucket.getDocCount();
        costMin = (long) ((ParsedSingleValueNumericMetricsAggregation) bucket.getAggregations().get("costMin")).value();
        costAvg = (long) ((ParsedSingleValueNumericMetricsAggregation) bucket.getAggregations().get("costAvg")).value();
        costMax = (long) ((ParsedSingleValueNumericMetricsAggregation) bucket.getAggregations().get("costMax")).value();
    }
}
