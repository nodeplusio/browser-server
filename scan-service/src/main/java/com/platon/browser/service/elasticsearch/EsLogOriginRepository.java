package com.platon.browser.service.elasticsearch;

import org.springframework.stereotype.Repository;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: εΊεζδ½
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
