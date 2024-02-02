package xyz.linyh.tokenpool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class GptTasker<T>{


    private BlockingQueue<T> resultBlockingQueue;


    private GptTask<T> task;

    public GptTasker(GptTask task){
        resultBlockingQueue = new LinkedBlockingQueue<>();
        this.task = task;
    }



    public T getResult() throws InterruptedException {
            return resultBlockingQueue.take();
//        通过take从阻塞队列里面获取消息，然后返回
    }

    public void executeTask(String token) {

        T result = null;
        try {
            result = task.execute(token);
        } catch (Exception e) {
            log.error("执行任务失败", e);
        }
        resultBlockingQueue.add(result);
    }


}
