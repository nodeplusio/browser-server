package com.platon.browser.service;

import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;
import com.platon.browser.websocket.WebSocketData;

public class ESQueryBuilderConstructorBuilder {

    public static Long getBlockNumber(WebSocketData webSocketData) {
        String lastPushData = webSocketData.getLastPushData();
        if (lastPushData == null) {
            return null;
        }
        return Long.parseLong(lastPushData);
    }
    public static ESQueryBuilderConstructor buildBlockConstructor(WebSocketData webSocketData, String numberFieldName) {
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        String lastPushData = webSocketData.getLastPushData();
        if (lastPushData == null) {
            blockConstructor.setDesc(numberFieldName);
            blockConstructor.setSize(1);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(numberFieldName, getBlockNumber(webSocketData) + 1, null))
                    .setAsc(numberFieldName);
            blockConstructor.setSize(1);
        }
        return blockConstructor;
    }
}
