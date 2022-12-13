package com.platon.browser.service;

import com.platon.browser.response.RespPage;
import com.platon.browser.response.gas.*;
import com.platon.browser.service.elasticsearch.EsTransactionRepository;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.pipeline.ParsedPercentilesBucket;
import org.elasticsearch.search.aggregations.pipeline.PercentilesBucketPipelineAggregationBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GasService {

    @Resource
    private EsTransactionRepository eSTransactionRepository;

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
        constructor.aggregation(aggregation);
        constructor.aggregation(percentilesBucketPipelineAggregationBuilder);
        GasPriceEstimateResp gasPriceEstimateResp = new GasPriceEstimateResp();
        try {
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor);
            ParsedPercentilesBucket percentiles = aggregations.get(gasPriceEstimate);
            gasPriceEstimateResp.setSlow(percentiles.percentile(10));
            gasPriceEstimateResp.setStandard(percentiles.percentile(50));
            gasPriceEstimateResp.setFast(percentiles.percentile(99));
            gasPriceEstimateResp.setInstant(percentiles.percentile(100));
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
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor);
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
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor);
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
            Aggregations aggregations = eSTransactionRepository.aggregationSearch(constructor);
            result.setData(((MultiBucketsAggregation) aggregations.get(aggregationName)).getBuckets().stream()
                    .map(CostGroupByTxTypeResp::new)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("查询es出错", e);
        }

        return result;
    }

    public RespPage<PendingTxInfoResp> getPendingTxInfo() {
        return null;
    }
}
