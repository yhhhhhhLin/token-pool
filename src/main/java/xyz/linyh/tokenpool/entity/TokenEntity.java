package xyz.linyh.tokenpool.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lin
 */
@Data
public class TokenEntity {

    private String token;


    private List<Long> times;

    /**
     * 一天内的次数
     */
    private Integer dayCount;

    /**
     * 周期(分钟)
     */
    private Integer cycle;

    /**
     * 0就是可用 1就是不可用
     */
    private Integer status = 0;

    /**
     * 一个周期里面最多多少次
     */
    private Integer frequency;

    public TokenEntity(String token, Integer dayCount, Integer cycle, Integer frequency, Integer status) {
        this.times = new ArrayList<>(frequency);
        for (int i = 0; i < frequency; i++) {
            times.add(0L);
        }
        this.frequency = frequency;
        this.token = token;
        this.cycle = cycle;
        this.dayCount = dayCount;
        this.status = status;
    }

    public TokenEntity() {
    }

    public String getThisToken() {
//        用完了，需要判断之间
        for (int i = 0; i < frequency; i++) {
            if (System.currentTimeMillis() - times.get(i) > cycle * 60 * 1000) {
                dayCount++;
                times.set(i, System.currentTimeMillis());
                return token;
            }
        }
        return null;
    }

}
