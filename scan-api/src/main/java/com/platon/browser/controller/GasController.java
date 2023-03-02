package com.platon.browser.controller;

import com.platon.browser.config.CommonMethod;
import com.platon.browser.config.DownFileCommon;
import com.platon.browser.enums.I18nEnum;
import com.platon.browser.enums.RetEnum;
import com.platon.browser.response.BaseResp;
import com.platon.browser.response.RespPage;
import com.platon.browser.response.gas.*;
import com.platon.browser.service.GasService;
import com.platon.browser.service.TransactionService;
import com.platon.browser.utils.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * gas接口
 */
@Slf4j
@RestController
public class GasController {

    @Resource
    private I18nUtil i18n;

    @Resource
    private TransactionService transactionService;
    @Resource
    private GasService gasService;

    @Resource
    private DownFileCommon downFileCommon;

    @Resource
    private CommonMethod commonMethod;

    /**
     * 3.1 gas费用估计
     * 功能：提供四种确认速度的gas手续费预估，每笔交易从收到到区块确认的间隔和手续费价格的关系
     * 实现逻辑：
     * 统计最新的14400个区块（4h），获取每个区块最低的gasprice进行排序，取
     * slow: 10% (20s内确认)、
     * standard：50% (4s内确认)、
     * fast：99% (1s内确认)、
     * instant：100% (立即确认)
     */
    @PostMapping("v2/gas/gasPriceEstimate")
    public Mono<BaseResp<GasPriceEstimateResp>> gasPriceEstimate() {
        return Mono.create(sink -> {
            GasPriceEstimateResp resp = gasService.gasPriceEstimate();
            sink.success(BaseResp.build(RetEnum.RET_SUCCESS.getCode(), i18n.i(I18nEnum.SUCCESS), resp));
        });
    }

    /**
     * 3.2 每个区块的费用统计
     */
    @PostMapping("/v2/gas/getGasPriceHistoryGroupByBlock")
    public Mono<RespPage<GasPriceHistoryGroupByBlockResp>> getGasPriceHistoryGroupByBlock() {
        return Mono.just(gasService.getGasPriceHistoryGroupByBlock());
    }

    /**
     * 3.3 gasPrice统计
     *
     * 功能：统计不同gasPrice的交易数量
     * 实现逻辑：
     * ES统计最新的区块（24h）
     */
    @PostMapping("/v2/gas/getCountGroupByGasPrice")
    public Mono<RespPage<CountGroupByGasPriceResp>> getCountGroupByGasPrice() {
        return Mono.just(gasService.getCountGroupByGasPrice());
    }

    /**
     * 3.4 不同交易gas消耗估计
     *
     * 功能：各种类型交易的gas费用估计，比如转账、委托、prc20交易等 (不同交易类型cost统计)
     * 实现逻辑：
     * ES统计最近1周的所有交易
     */
    @PostMapping("/v2/gas/getCostGroupByTxType")
    public Mono<RespPage<CostGroupByTxTypeResp>> getCostGroupByTxType() {
        return Mono.just(gasService.getCostGroupByTxType());
    }

    /**
     * 3.5 查看pending状态尚未打包的交易
     *
     * 功能：查看pending状态尚未打包的交易
     * 实现逻辑：
     * 修改客户端，从交易池中获取交易信息
     */
    @PostMapping("/v2/gas/getPendingTxInfo")
    public Mono<RespPage<PendingTxInfoResp>> getPendingTxInfo() {
        return Mono.just(gasService.getPendingTxInfo());
    }

}
