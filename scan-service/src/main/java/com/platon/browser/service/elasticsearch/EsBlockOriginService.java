package com.platon.browser.service.elasticsearch;

import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: ES服务
 */
@Slf4j
@Service
public class EsBlockOriginService implements EsService<BlockOrigin>{
    @Resource
    private EsBlockOriginRepository esBlockOriginRepository;

    @Retryable(value = BusinessException.class, maxAttempts = Integer.MAX_VALUE)
    public void save(Set<BlockOrigin> blocks) throws IOException {
        if(blocks.isEmpty()) return;
        try {
            Map<String,BlockOrigin> blockMap = new HashMap<>();
            // 使用区块号作ES的docId
            blocks.forEach(b->blockMap.put(b.getNumber().toString(),b));
            esBlockOriginRepository.bulkAddOrUpdate(blockMap);
        }catch (Exception e){
            log.error("保存块原始数据出错",e);
            throw new BusinessException(e.getMessage());
        }
    }
}
