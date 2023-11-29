package xyz.linyh.tokenpool.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;


/**
 * @author lin
 */

@ConfigurationProperties("token-pool")
@Data
public class TokenPoolProperties {

    /**
     *tokens
     */
    private List<String> tokens;

    /**
     * 周期
     */
    private Integer cycle;

    /**
     * 一个周期里面最多多少次
     */
    private Integer frequency;

}
