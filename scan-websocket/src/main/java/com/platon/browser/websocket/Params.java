package com.platon.browser.websocket;

import lombok.Data;

@Data
public class Params<T> {
    private T result;
    private String subscription;
}