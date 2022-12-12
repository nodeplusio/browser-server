package com.platon.browser.response.address;

import lombok.Data;

import java.util.List;

/**
 * 查询地址的返回的对象
 */
@Data
public class QueryAddressValueAllColsResp {

    private List<AddressValueAllCols> result;
    /**
     * 总数
     */
    private long totalCount;
    /**
     * 当前页
     */
    private long currentPage;
    /**
     * 总页数
     */
    private long totalPages;

}
