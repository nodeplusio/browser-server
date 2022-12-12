package com.platon.browser.response.staking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;

/**
 * 节点历史出块详情
 */
@Data
public class BlockCountHistoryByNodeResp {
    private Object key;
    @JsonProperty("key_as_string")
    private String keyAsString;
    @JsonProperty("doc_count")
    private Long docCount;

    public BlockCountHistoryByNodeResp(Histogram.Bucket bucket) {
        this.key = bucket.getKey();
        this.keyAsString = bucket.getKeyAsString();
        this.docCount = bucket.getDocCount();
    }
}
