package xyz.linyh.tokenpool.client;


import lombok.extern.slf4j.Slf4j;
import xyz.linyh.tokenpool.entity.GptTask;

import java.util.List;
import java.util.Queue;
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

    private BlockingQueue<GptTask> taskQueue = new LinkedBlockingQueue<>();



    private BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<String> tokenQueue = new LinkedBlockingQueue<>();

    /**
     * 定时任务执行器
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private AtomicInteger num = new AtomicInteger(0);

    Boolean active = true;

    public TokenPoolClient() {

        refreshTokenQueue();
        this.start();

    }

    public TokenPoolClient(int maxPoolSize, List<String> tokens, Integer cycle, Integer frequency) {

        this.executorService = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                5,
                TimeUnit.SECONDS,
                blockingQueue,
                new ThreadPoolExecutor.AbortPolicy());



        this.cycle = cycle;
        this.frequency = frequency;
        this.tokens = tokens;
        refreshTokenQueue();
        this.start();

//        初始化token队列
    }

    public TokenPoolClient(List<String> tokens, Integer cycle, Integer frequency) {
        int maxPoolSize = tokens.size();
        if (tokens.size() >= 10) {
            maxPoolSize = 10;
        }

        this.executorService = new ThreadPoolExecutor(maxPoolSize,
                maxPoolSize,
                5,
                TimeUnit.SECONDS,
                blockingQueue,
                new ThreadPoolExecutor.AbortPolicy());

        this.cycle = cycle;
        this.frequency = frequency;
        this.tokens = tokens;
        refreshTokenQueue();
        this.start();
    }

    private void refreshTokenQueue() {
        for (int i = 0; i < frequency; i++) {
            tokenQueue.addAll(this.tokens);
        }
    }


    public void addTask(GptTask task) throws Exception {
//        添加到要执行的队列中
        taskQueue.add(task);
    }

    @Override
    public void run() {

//        设置一个定时刷新的任务去刷新token池
        scheduler.scheduleAtFixedRate(() -> {
            tokenQueue.clear();
            refreshTokenQueue();
        }, 0, cycle, TimeUnit.MINUTES);

        while (active) {
            try {
//                需要上锁，不然可能会出现重复拿的情况
                String token = tokenQueue.take();
                log.info("当前token值为:{}", token);
                GptTask task = taskQueue.take();

                log.info("当前token值2为:{}", token);
//                利用线程池去执行对应的任务
                executorService.execute(() -> {

                    try {
                        task.execute(token);
                    } catch (Exception e) {
//                        log.info("捕获抛出的异常:{},对应的token值为{}",e.getMessage(),token);
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
