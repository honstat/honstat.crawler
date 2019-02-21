package com.honstat.crawler.service.beans;

import com.honstat.crawler.service.manager.ProssorSaveMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.crawler.service.beans
 * @Description: TODO
 * @date 2019/1/22 14:17
 */
@Configuration
public class MonitorSaveBeanUtil {

    @Bean
    public ProssorSaveMonitor getBean(){
      return   new ProssorSaveMonitor();
    }
}
