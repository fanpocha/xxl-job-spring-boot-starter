package com.xxl.job.plus.executor.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import com.xxl.job.plus.executor.service.XxlJobProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author : Hydra
 * @date: 2022/9/20 15:59
 * @version: 1.0
 */
@Configuration
@ComponentScan(basePackages = "com.xxl.job.plus.executor")
public class XxlJobPlusConfig {


    @Bean
    public XxlJobSpringExecutor xxlJobExecutor( @Autowired XxlJobProperties xxlJobProperties) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlJobProperties.getAdminAddresses());
        xxlJobSpringExecutor.setAppname(xxlJobProperties.getAppname());
        if (StringUtils.hasLength(xxlJobProperties.getIp()))
        xxlJobSpringExecutor.setIp(xxlJobProperties.getIp());
        xxlJobSpringExecutor.setPort(xxlJobProperties.getPort());
        if (StringUtils.hasLength(xxlJobProperties.getAccessToken()))
        xxlJobSpringExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        if (StringUtils.hasLength(xxlJobProperties.getAddress()))
        xxlJobSpringExecutor.setAddress(xxlJobProperties.getAddress());
        xxlJobSpringExecutor.setLogPath(xxlJobProperties.getLogPath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlJobProperties.getLogRetentionDays());
        return xxlJobSpringExecutor;
    }
}
