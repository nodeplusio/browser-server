package com.platon.browser.service;

import lombok.Data;

@Data
public class MinedTransactionResult<T> {

    private Boolean removed = false;
    private T transaction;

}
