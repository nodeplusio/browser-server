package com.platon.browser.response.gas;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;

@Data
public class GasPriceHistoryGroupByBlockResp {
    private Object key;
    @JsonProperty("doc_count")
    private Long docCount;

    private Long costSum;
    private Long costMin;
    private Long costAvg;
    private Long costMax;
    private Long gasPriceMin;
    private Long gasPriceAvg;
    private Long gasPriceMax;

    public GasPriceHistoryGroupByBlockResp(MultiBucketsAggregation.Bucket bucket) {
        key = bucket.getKey();
        docCount = bucket.getDocCount();
        costSum = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("costSum")).value();
        costMin = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("costMin")).value();
        costAvg = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("costAvg")).value();
        costMax = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("costMax")).value();
        gasPriceMin = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("gasPriceMin")).value();
        gasPriceAvg = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("gasPriceAvg")).value();
        gasPriceMax = (long)((ParsedSingleValueNumericMetricsAggregation)bucket.getAggregations().get("gasPriceMax")).value();
    }
}
