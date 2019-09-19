package com.platon.browser.task;

import com.platon.browser.config.BlockChainConfig;
import com.platon.browser.dao.entity.Staking;
import com.platon.browser.dao.mapper.CustomStakingMapper;
import com.platon.browser.dto.CustomStaking;
import com.platon.browser.engine.bean.keybase.Completion;
import com.platon.browser.engine.bean.keybase.Components;
import com.platon.browser.engine.bean.keybase.KeyBaseUser;
import com.platon.browser.engine.cache.CacheHolder;
import com.platon.browser.engine.stage.StakingStage;
import com.platon.browser.exception.HttpRequestException;
import com.platon.browser.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @Auther: dongqile
 * @Date: 2019/8/17 20:09
 * @Description: 质押信息更新任务
 */
@Component
public class StakingUpdateTask {
    private static Logger logger = LoggerFactory.getLogger(StakingUpdateTask.class);
    @Autowired
    private BlockChainConfig chainConfig;
    private static final String URI_PATH = "_/api/1.0/user/autocomplete.json?q=";
    @Autowired
    private CacheHolder cacheHolder;
    @Autowired
    private CustomStakingMapper customStakingMapper;

    private StakingStage stakingStage = new StakingStage();

    @Scheduled(cron = "0/3  * * * * ?")
    private void cron(){start();}

    public void start () {
        String keyStoreUrl = chainConfig.getKeyBase();
        try {
            Set <CustomStaking> customStakingSet = getAllStaking();
            if (customStakingSet.isEmpty()) return;
            customStakingSet.forEach(customStaking -> {
                if(
                    (StringUtils.isBlank(customStaking.getExternalName()) || StringUtils.isBlank(customStaking.getStakingIcon()))
                    &&
                    StringUtils.isNotBlank(customStaking.getExternalId())
                ){
                    String queryUrl = keyStoreUrl.concat(URI_PATH.concat(customStaking.getExternalId()));
                    try {
                        KeyBaseUser keyBaseUser = HttpUtil.get(queryUrl,KeyBaseUser.class);
                        List <Completion> completions = keyBaseUser.getCompletions();
                        if (completions == null || completions.isEmpty()) return;
                        // 取最新一条
                        Completion completion = completions.get(0);
                        // 取缩略图
                        String icon = completion.getThumbnail();
                        customStaking.setStakingIcon(icon);

                        Components components = completion.getComponents();
                        String username = components.getUsername().getVal();
                        customStaking.setExternalName(username);
                        // 把改动后的内容暂存至待更新列表
                        stakingStage.updateStaking(customStaking);
                    } catch (HttpRequestException e) {
                        logger.error("更新质押(nodeId = {}, blockNumber = {})keybase信息出错:{}",customStaking.getNodeId(),customStaking.getStakingBlockNum(), e.getMessage());
                    }
                }
            });
            if(!stakingStage.exportStaking().isEmpty()){
                customStakingMapper.batchInsertOrUpdateSelective(stakingStage.exportStaking(), Staking.Column.values());
                stakingStage.clear();
            }
        } catch (Exception e) {
            logger.error("[StakingUpdateTask] Exception {}", e.getMessage());
        }
    }

    /**
     * 取所有质押
     * @return
     */
    public Set<CustomStaking> getAllStaking() {
        return cacheHolder.getNodeCache().getAllStaking();
    }
}
