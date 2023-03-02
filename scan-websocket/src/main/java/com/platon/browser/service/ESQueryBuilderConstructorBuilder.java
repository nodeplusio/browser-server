package com.platon.browser.service;

import com.platon.browser.service.elasticsearch.query.ESQueryBuilderConstructor;
import com.platon.browser.service.elasticsearch.query.ESQueryBuilders;

public class ESQueryBuilderConstructorBuilder {

    public static ESQueryBuilderConstructor buildBlockConstructor(String numberFieldName, Long blockNumber) {
        ESQueryBuilderConstructor blockConstructor = new ESQueryBuilderConstructor();
        if (blockNumber == null) {
            blockConstructor.setDesc(numberFieldName);
            blockConstructor.setSize(1);
        } else {
            blockConstructor.must(new ESQueryBuilders().range(numberFieldName, blockNumber + 1, null))
                    .setAsc(numberFieldName);
            blockConstructor.setSize(10);
        }
        return blockConstructor;
    }
}
