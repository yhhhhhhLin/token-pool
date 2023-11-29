package xyz.linyh.tokenpool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.linyh.tokenpool.client.TokenPoolClient;
import xyz.linyh.tokenpool.properties.TokenPoolProperties;

/**
 * @author lin
 */
@Configuration
@EnableConfigurationProperties({TokenPoolProperties.class})
@Slf4j
public class TokenPoolConfig {

    /**
     * 将token池操作对象注入到容器中
     * @param properties 配置信息
     * @return 操作对象
     */
    @Bean
    public TokenPoolClient tokenPoolClient(TokenPoolProperties properties) {
        TokenPoolClient tokenPoolClient = new TokenPoolClient(properties.getTokens(), properties.getCycle(), properties.getFrequency());
        log.info("tokenPoolClient 初始化成功");
        return tokenPoolClient;
    }


}
