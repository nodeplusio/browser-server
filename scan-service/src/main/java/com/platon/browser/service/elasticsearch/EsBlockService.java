package com.platon.browser.service.elasticsearch;

import com.platon.browser.client.PlatOnClient;
import com.platon.browser.elasticsearch.dto.Block;
import com.platon.browser.elasticsearch.dto.BlockOrigin;
import com.platon.browser.elasticsearch.dto.LogOrigin;
import com.platon.browser.exception.BusinessException;
import com.platon.protocol.core.DefaultBlockParameter;
import com.platon.protocol.core.methods.request.PlatonFilter;
import com.platon.protocol.core.methods.response.PlatonBlock;
import com.platon.protocol.core.methods.response.PlatonLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Auther: Chendongming
 * @Date: 2019/10/25 15:12
 * @Description: ES服务
 */
@Slf4j
@Service
public class EsBlockService implements EsService<Block> {
    @Resource
    private EsBlockRepository ESBlockRepository;
    @Resource
    private PlatOnClient platOnClient;
    @Resource
    private EsBlockOriginService esBlockOriginService;
    @Resource
    private EsLogOriginService esLogOriginService;

    @Retryable(value = BusinessException.class, maxAttempts = Integer.MAX_VALUE)
    public void save(Set<Block> blocks) throws IOException {
        if (blocks.isEmpty()) return;
        try {
            Map<String, Block> blockMap = new HashMap<>();
            // 使用区块号作ES的docId
            blocks.forEach(b -> blockMap.put(b.getNum().toString(), b));
            ESBlockRepository.bulkAddOrUpdate(blockMap);
            saveBlockOrigin(blocks);
            saveLogOrigin(blocks);
        } catch (Exception e) {
            log.error("保存块数据出错", e);
            throw new BusinessException(e.getMessage());
        }
    }

    private void saveLogOrigin(Set<Block> blocks) throws IOException {
        try {
            LongSummaryStatistics statistics = blocks.stream().mapToLong(Block::getNum).summaryStatistics();
            PlatonFilter ethFilter = new PlatonFilter(DefaultBlockParameter.valueOf(BigInteger.valueOf(statistics.getMin())),
                    DefaultBlockParameter.valueOf(BigInteger.valueOf(statistics.getMax())), (List<String>) null);
            List<PlatonLog.LogResult> logs = platOnClient.getWeb3jWrapper().getWeb3j()
                    .platonGetLogs(ethFilter)
                    .send().getLogs();
            Set<LogOrigin> result = logs.stream().map(logResult -> (PlatonLog.LogObject) logResult.get())
                    .map(LogOrigin::new).collect(Collectors.toSet());
            esLogOriginService.save(result);
        } catch (Throwable e) {
            log.error("查询log原始数据失败", e);
            throw new RuntimeException(e);
        }
    }

    private void saveBlockOrigin(Set<Block> blocks) throws IOException {
        Set<BlockOrigin> blockOrigins = blocks.stream().map(this::getBlockOrigin).collect(Collectors.toSet());
        esBlockOriginService.save(blockOrigins);
    }

    private BlockOrigin getBlockOrigin(Block block) {
        try {
            PlatonBlock.Block ethBlock = platOnClient.getWeb3jWrapper().getWeb3j()
                    .platonGetBlockByNumber(DefaultBlockParameter.valueOf(BigInteger.valueOf(block.getNum())), false)
                    .send().getBlock();
            return new BlockOrigin(ethBlock);
        } catch (Throwable e) {
            log.error("查询块原始数据失败", e);
            throw new RuntimeException(e);
        }
    }
}
