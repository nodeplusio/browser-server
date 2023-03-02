package com.platon.browser.service.elasticsearch;

import org.springframework.stereotype.Repository;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: 区块操作
 */
@Repository
public class EsLogOriginRepository extends AbstractEsRepository {
    @Override
    public String getIndexName() {
        return config.getLogOriginIndexName();
    }
    @Override
    public String getTemplateFileName() {
        return "log-origin";
    }
}
