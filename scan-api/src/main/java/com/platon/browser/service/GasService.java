package com.platon.browser.service;

import com.alibaba.fastjson.JSON;
import com.platon.browser.client.PlatOnClient;
import com.platon.browser.elasticsearch.dto.Transaction;
import com.platon.browser.response.RespPage;
import com.platon.browser.response.gas.*;
import com.platon.browser.service.elasticsearch.EsTransactionRepository;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.protocol.admin.methods.response.TxPoolContent;
import com.platon.protocol.core.Request;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.pipeline.ParsedPercentilesBucket;
import org.elasticsearch.search.aggregations.pipeline.PercentilesBucketPipelineAggregationBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GasService {

    @Resource
    private EsTransactionRepository eSTransactionRepository;
    @Resource
    private PlatOnClient platOnClient;

    public GasPriceEstimateResp gasPriceEstimate() {
        String aggregationName = "nums";
        AggregationBuilder aggregation =
                AggregationBuilders.terms(aggregationName).field("num")
                        .subAggregation(AggregationBuilders.min("gasPriceMin")
                                .script(new Script("new BigInteger(params._source.gasPrice)")));
        double[] percents = {10, 50, 99, 100};
        String gasPriceEstimate = "gasPriceEstimate";
        PercentilesBucketPipelineAggregationBuilder percentilesBucketPipelineAggregationBuilder =
                new PercentilesBucketPipelineAggregationBuilder(gasPriceEstimate, "nums>gasPriceMin")
                        .setPercents(percents);

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.must(new ESQueryBuilders()
                .range("time", "now-2M", "now"));
        constructor.setDesc("time");
        constructor.aggregation(aggregation);
        constructor.aggregation(percentilesBucketPipelineAggregationBuilder);
        GasPriceEstimateResp gasPriceEstimateResp = new GasPriceEstimateResp();
        try {
            SearchResponse searchResponse = eSTransactionRepository.aggregationSearch(constructor);
            Aggregations aggregations = searchResponse.getAggregations();
            ParsedPercentilesBucket percentiles = aggregations.get(gasPriceEstimate);
            gasPriceEstimateResp.setSlow(percentiles.percentile(10));
            gasPriceEstimateResp.setStandard(percentiles.percentile(50));
            gasPriceEstimateResp.setFast(percentiles.percentile(99));
            gasPriceEstimateResp.setInstant(percentiles.percentile(100));
            SearchHit hit = searchResponse.getHits().getAt(0);
            Transaction transaction = JSON.parseObject(hit.getSourceAsString(), Transaction.class);
            gasPriceEstimateResp.setBlockNumber(transaction.getNum());
            gasPriceEstimateResp.setBlockTime(transaction.getTime().getTime());
            gasPriceEstimateResp.setHealth(DateUtils.addMinutes(new Date(), 2).before(transaction.getTime()));
        } catch (Exception e) {
            log.error("查询es出错", e);
        }
        return gasPriceEstimateResp;
    }

    public RespPage<GasPriceHistoryGroupByBlockResp> getGasPriceHistoryGroupByBlock() {
        RespPage<GasPriceHistoryGroupByBlockResp> result = new RespPage<>();
        String aggregationName = "nums";
        AggregationBuilder aggregation =
                AggregationBuilders.terms(aggregationName).field("num")
                        .subAggregation(AggregationBuilders.sum("costSum")
                                .script(new Script("new BigInteger(params._source.cost)")))
                        .subAggregation(AggregationBuilders.min("costMin")
                                .script(new Script("new BigInteger(params._source.cost)")))
                        .subAggregation(AggregationBuilders.avg("costAvg")
                                .script(new Script("new BigInteger(params._source.cost)")))
                        .subAggregation(AggregationBuilders.max("costMax")
                                .script(new Script("new BigInteger(params._source.cost)")))
                        .subAggregation(AggregationBuilders.min("gasPriceMin")
                                .script(new Script("new BigInteger(params._source.gasPrice)")))
                        .subAggregation(AggregationBuilders.avg("gasPriceAvg")
                                .script(new Script("new BigInteger(params._source.gasPrice)")))
                        .subAggregation(AggregationBuilders.max("gasPriceMax")
                                .script(new Script("new BigInteger(params._source.gasPrice)")));

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.must(new ESQueryBuilders()
                .range("time", "now-2M", "now"));
        constructor.aggregation(aggregation);
        try {
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor).getAggregations();
            result.setData(((MultiBucketsAggregation) aggregations.get(aggregationName)).getBuckets().stream()
                    .map(GasPriceHistoryGroupByBlockResp::new)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("查询es出错", e);
        }

        return result;
    }

    public RespPage<CountGroupByGasPriceResp> getCountGroupByGasPrice() {
        RespPage<CountGroupByGasPriceResp> result = new RespPage<>();
        String aggregationName = "gasPrices";
        AggregationBuilder aggregation = AggregationBuilders.terms(aggregationName)
                .script(new Script("new BigInteger(params._source.gasPrice)"));

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.must(new ESQueryBuilders()
                .range("time", "now-2M", "now"));
        constructor.aggregation(aggregation);
        try {
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor).getAggregations();
            result.setData(((MultiBucketsAggregation) aggregations.get(aggregationName)).getBuckets().stream()
                    .map(CountGroupByGasPriceResp::new)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("查询es出错", e);
        }

        return result;
    }

    public RespPage<CostGroupByTxTypeResp> getCostGroupByTxType() {
        RespPage<CostGroupByTxTypeResp> result = new RespPage<>();
        String aggregationName = "types";
        AggregationBuilder aggregation =
                AggregationBuilders.terms(aggregationName).field("type")
                        .subAggregation(AggregationBuilders.min("costMin")
                                .script(new Script("new BigInteger(params._source.cost)")))
                        .subAggregation(AggregationBuilders.avg("costAvg")
                                .script(new Script("new BigInteger(params._source.cost)")))
                        .subAggregation(AggregationBuilders.max("costMax")
                                .script(new Script("new BigInteger(params._source.cost)")));

        ESQueryBuilderConstructor constructor = new ESQueryBuilderConstructor();
        constructor.must(new ESQueryBuilders()
                .range("time", "now-2M", "now"));
        constructor.aggregation(aggregation);
        try {
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor).getAggregations();
            result.setData(((MultiBucketsAggregation) aggregations.get(aggregationName)).getBuckets().stream()
                    .map(CostGroupByTxTypeResp::new)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("查询es出错", e);
        }

        return result;
    }

    public RespPage<PendingTxInfoResp> getPendingTxInfo() {
        RespPage<PendingTxInfoResp> respPage = new RespPage<>();
        try {
            Request<?, TxPoolContent> txPoolContent = platOnClient.getWeb3jWrapper().getAdmin().txPoolContent();
            TxPoolContent.TxPoolContentResult result = txPoolContent.send().getResult();
            Map<String, Map<BigInteger, com.platon.protocol.core.methods.response.Transaction>> resultPending = result.getPending();
            respPage.setData(resultPending.values()
                    .stream()
                    .map(Map::values)
                    .flatMap(Collection::stream)
                    .map(PendingTxInfoResp::new)
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            log.error("查询txPoolContent出错", e);
        }
        return respPage;
    }
}
