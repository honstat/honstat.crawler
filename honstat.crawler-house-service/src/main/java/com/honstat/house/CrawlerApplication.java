package com.honstat.house;

import com.honstat.crawler.service.utils.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
@tk.mybatis.spring.annotation.MapperScan(basePackages ="com.honstat.house.service.dao.mapper")
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class})
@ImportResource({"classpath:META-INF/spring/applicationContext-datasource.xml",
//        "classpath*:META-INF/spring/applicationContext-utils-base.xml",
		//  "classpath*:META-INF/spring/applicationContext-multidb-base.xml",
		//"classpath*:META-INF/spring/applicationContext-dubbo.xml",
		"classpath*:META-INF/spring/applicationContext-mq.xml",
		"classpath*:META-INF/spring/applicationContext-base.xml",
		"classpath*:META-INF/spring/applicationContext-redis.xml"
})
@EnableAspectJAutoProxy
public class CrawlerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CrawlerApplication.class, args);

	}

}

