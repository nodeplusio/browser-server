package com.platon.browser.service;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MinedTransactionsParam {

    private Boolean removed = false;
    private Boolean hashesOnly = false;
    private List<MinedTransactionsAddress> addresses = new ArrayList<>();

}
