package com.platon.browser.request.address;

import com.platon.browser.request.PageReq;
import lombok.Data;

/**
 *  查询地址锁仓详情请求对象
 *  @file QueryDetailRequest.java
 *  @description 
 *	@author zhangrj
 *  @data 2019年8月31日
 */
@Data
public class QueryRpPlanByUnlockNumberRequest extends PageReq{

	private Long epoch;

	private Long unlockNumber;
    
}