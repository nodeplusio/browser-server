package com.platon.browser.websocket;

import lombok.Data;

@Data
public class SubscriptionResponse<T> {
    private String method = "eth_subscription";
    private String jsonrpc = "2.0";
    private Params<T> params;
}