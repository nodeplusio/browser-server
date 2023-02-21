package com.platon.browser.service;

import com.platon.browser.websocket.WebSocketData;

public interface SubscriptionService {

    void subscribe(WebSocketData webSocketData);

    default void send() {

    }

    default void clean() {
    }
}
