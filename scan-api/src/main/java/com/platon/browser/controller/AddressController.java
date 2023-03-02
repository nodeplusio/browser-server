package com.platon.browser.controller;

import com.platon.browser.enums.I18nEnum;
import com.platon.browser.enums.RetEnum;
import com.platon.browser.request.PageReq;
import com.platon.browser.request.address.QueryDetailRequest;
import com.platon.browser.request.address.QueryRPPlanDetailRequest;
import com.platon.browser.request.address.QueryRpPlanByUnlockNumberRequest;
import com.platon.browser.response.BaseResp;
import com.platon.browser.response.RespPage;
import com.platon.browser.response.address.*;
import com.platon.browser.service.AddressService;
import com.platon.browser.utils.I18nUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 地址具体实现Controller 提供地址详情页面使用
 *
 * @author zhangrj
 * @file AppDocAddressController.java
 * @description
 * @data 2019年8月31日
 */
@Slf4j
@RestController
public class AddressController {

    @Resource
    private AddressService addressService;

    @Resource
    private I18nUtil i18n;

    /**
     * 查询地址详情
     *
     * @param req
     * @return reactor.core.publisher.Mono<com.platon.browser.response.BaseResp < com.platon.browser.response.address.QueryDetailResp>>
     * @date 2021/1/29
     */
    @PostMapping("address/details")
    public Mono<BaseResp<QueryDetailResp>> details(@Valid @RequestBody QueryDetailRequest req) {
        return Mono.create(sink -> {
            QueryDetailResp resp = addressService.getDetails(req);
            sink.success(BaseResp.build(RetEnum.RET_SUCCESS.getCode(), i18n.i(I18nEnum.SUCCESS), resp));
        });
    }

    /**
     * 地址锁仓详情
     *
     * @param req
     * @return reactor.core.publisher.Mono<com.platon.browser.response.BaseResp < com.platon.browser.response.address.QueryRPPlanDetailResp>>
     * @date 2021/5/25
     */
    @PostMapping("address/rpplanDetail")
    public Mono<BaseResp<QueryRPPlanDetailResp>> rpplanDetail(@Valid @RequestBody QueryRPPlanDetailRequest req) {
        return Mono.create(sink -> {
            QueryRPPlanDetailResp resp = addressService.rpplanDetail(req);
            sink.success(BaseResp.build(RetEnum.RET_SUCCESS.getCode(), i18n.i(I18nEnum.SUCCESS), resp));
        });
    }

    /**
     * 1.1 地址列表按余额大小排序
     *
     * @param req
     * @return reactor.core.publisher.Mono<com.platon.browser.response.BaseResp < com.platon.browser.response.address.QueryAddressValueResp>>
     */
    @PostMapping("address/addressvalue")
    public Mono<RespPage<QueryAddressValueResp>> addressvalue(@Valid @RequestBody PageReq req) {
        return Mono.just(addressService.queryAddressValue(req));
    }

    @PostMapping("address/addressValueAllCols")
    public Mono<BaseResp<QueryAddressValueAllColsResp>> addressValueAllCols(@Valid @RequestBody PageReq req) {
        return Mono.create(sink -> {
            QueryAddressValueAllColsResp resp = addressService.queryAddressValueAllCols(req);
            sink.success(BaseResp.build(RetEnum.RET_SUCCESS.getCode(), i18n.i(I18nEnum.SUCCESS), resp));
        });
    }

    @PostMapping("address/getAllEpoch")
    public Mono<RespPage<QueryAllEpochResp>> getAllEpoch(@Valid @RequestBody PageReq req) {
        return Mono.just(addressService.getAllEpoch(req));
    }

    @PostMapping("address/getRpPlanByUnlockNumber")
    public Mono<RespPage<QueryRpPlanByUnlockNumberResp>> getRpPlanByUnlockNumber(@Valid @RequestBody QueryRpPlanByUnlockNumberRequest req) {
        return Mono.just(addressService.getRpPlanByUnlockNumber(req));
    }

}
