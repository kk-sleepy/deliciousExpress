package com.sky.config;

import com.sky.properties.AliOssProperties;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于创建 OSSClient
 */
@Slf4j
@Configuration
public class OssConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssProperties aliOssProperties) {
        log.info("阿里云上传组件: {}", aliOssProperties);
        return new AliOssUtil(aliOssProperties.getEndpoint()
                ,aliOssProperties.getAccessKeyId()
                ,aliOssProperties.getAccessKeySecret()
                ,aliOssProperties.getBucketName());
    }
}
