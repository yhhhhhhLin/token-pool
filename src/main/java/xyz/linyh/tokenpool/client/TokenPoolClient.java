package xyz.linyh.tokenpool.client;


import lombok.extern.slf4j.Slf4j;
import xyz.linyh.tokenpool.entity.GptTask;
import xyz.linyh.tokenpool.entity.GptTasker;
import xyz.linyh.tokenpool.entity.TokenEntity;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lin
 */
@Slf4j
public class TokenPoolClient extends Thread {

    private ExecutorService executorService;


    private List<String> tokens;

    /**
     * 周期(分钟)
     */
    private Integer cycle;

    /**
     * 一个周期里面最多多少次
     */
    private Integer frequency;

    private BlockingQueue<GptTasker> taskQueue = new LinkedBlockingQueue<>();

    /**
     * 用来获取结果的线程池
     */
    private ExecutorService resultPool;

    private BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<TokenEntity> tokenQueue = new LinkedBlockingQueue<>();

    /**
     * 定时任务执行器
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private AtomicInteger num = new AtomicInteger(0);

    Boolean active = true;

    public TokenPoolClient() {
//        refreshTokenQueue();
        this.start();
    }

    public TokenPoolClient(int maxPoolSize, List<String> tokens, Integer cycle, Integer frequency) {
        for (int i = 0; i < tokens.size(); i++) {
            TokenEntity tokenEntity = new TokenEntity(tokens.get(i), 0, cycle, frequency, 0);
            tokenQueue.add(tokenEntity);
        }
        this.executorService = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                5,
                TimeUnit.SECONDS,
                blockingQueue,
                new ThreadPoolExecutor.AbortPolicy());

        this.resultPool = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                5,
                TimeUnit.SECONDS,
                blockingQueue,
                new ThreadPoolExecutor.AbortPolicy());

        this.cycle = cycle;
        this.frequency = frequency;
        this.tokens = tokens;
//        refreshTokenQueue();
        this.start();

//        初始化token队列
    }

    public TokenPoolClient(List<String> tokens, Integer cycle, Integer frequency) {

        int maxPoolSize = tokens.size();
        if (tokens.size() >= 10) {
            maxPoolSize = 10;
        }

        for (int i = 0; i < tokens.size(); i++) {
            TokenEntity tokenEntity = new TokenEntity(tokens.get(i), 0, cycle, frequency, 0);
            tokenQueue.add(tokenEntity);
        }

        this.executorService = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                5,
                TimeUnit.SECONDS,
                blockingQueue,
                new ThreadPoolExecutor.AbortPolicy());

        this.resultPool = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                5,
                TimeUnit.SECONDS,
                blockingQueue,
                new ThreadPoolExecutor.AbortPolicy());

        this.cycle = cycle;
        this.frequency = frequency;
        this.tokens = tokens;
        this.start();
    }


    //    TODO 如果token异常日志
    public <T> T addTask(GptTask<T> task) throws Exception {
//        加强task

        GptTasker<T> gptTasker = new GptTasker<>(task);

        taskQueue.add(gptTasker);
        return gptTasker.getResult();
    }

    @Override
    public void run() {

        while (active) {
            try {

                TokenEntity tokenEntity = tokenQueue.take();
                if (tokenEntity.getThisToken() == null) {
                    tokenQueue.add(tokenEntity);
                    continue;
                }
                log.info("获取到token值，当前token值为:{}", tokenEntity.getToken());
                GptTasker task = taskQueue.take();
                log.info("获取到task，当前的task值为:{}", task);

                executorService.execute(() -> {

                    try {
                        task.executeTask(tokenEntity.getToken());
                        log.info("添加回token队列{}", tokenEntity);
                        tokenQueue.add(tokenEntity);
                    } catch (Exception e) {
                        log.info("出现异常");
                        tokenQueue.add(tokenEntity);
                    }
                });

            } catch (InterruptedException e) {
                log.info("tokenPoolClient被关闭了.{}", e.getMessage());
            }

        }

    }

    /**
     * 关闭这个定时任务就是把tokenPoolClient这个程序给关闭
     */
    public void shutdown() {
        this.active = false;
    }
}
