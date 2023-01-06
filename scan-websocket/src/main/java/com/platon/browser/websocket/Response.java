package com.platon.browser.websocket;

import lombok.Data;

@Data
public class Response<T> {
    private Integer id;
    private String jsonrpc = "2.0";
    private ErrorResult error;
    private T result;
}