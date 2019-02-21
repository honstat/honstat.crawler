package com.honstat.house.manager;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author chuanhong.jing
 * @version V1.0
 * @Project: houseParent
 * @Package com.honstat.house.service.manager
 * @Description: TODO
 * @date 2019/1/11 15:40
 */
@Component
public class WebHtmlClientBean {
    @Bean
    public WebClient getClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false); // 取消 CSS 支持 ✔
        webClient.getOptions().setJavaScriptEnabled(false); // 取消 JavaScript支持 ✔
        return webClient;
    }
}
