package com.platon.browser.service.elasticsearch;

import org.springframework.stereotype.Repository;

/**
 */
@Repository
public class EsTransactionOriginRepository extends AbstractEsRepository {
    @Override
    public String getIndexName() {
        return config.getTransactionOriginIndexName();
    }
    @Override
    public String getTemplateFileName() {
        return "transaction-origin";
    }
}
