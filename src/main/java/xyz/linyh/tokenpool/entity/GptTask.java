package xyz.linyh.tokenpool.entity;

import cn.hutool.http.HttpRequest;

/**
 * @author lin
 */
public interface GptTask<T>{

    /**
     * 根据发送请求到gpt，携带对应token
     * @param token 要携带的token
     */
    public T execute(String token);
}
