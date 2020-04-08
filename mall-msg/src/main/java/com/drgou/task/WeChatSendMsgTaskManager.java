package com.drgou.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class WeChatSendMsgTaskManager {
    private Logger logger = LoggerFactory.getLogger(WeChatSendMsgTaskManager.class);

    public static final int CAPACITY = 300000;
    /**
     * 队列
     */
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(CAPACITY);
    private Executor taskExecutor = Executors.newFixedThreadPool(5);
    private volatile boolean isStart = false;
    private Thread daemonThread = null;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        daemonThread = new Thread(() -> execute());
        daemonThread.setName("weChatSendMsgTaskManager");
        daemonThread.start();
        isStart = true;
    }

    public void execute() {
        while (isStart) {
            try {
                logger.info("weChatMessage专用任务队列-执行任务队列，当前任务数量:{}", queue.size());
                // 从队列中获取任务
                Runnable task = queue.take();
                if (null == task) {
                    continue;
                }
                // 提交到线程池执行task
                taskExecutor.execute(task);
            } catch (Exception e) {
                logger.error("weChatMessage专用任务队列-执行任务队列出错：{}", e.getMessage());
            }
        }
    }

    /**
     * 添加任务
     *
     * @param task
     */
    public void put(Runnable task) {
        // 将消息体放到队列中
        try {
            queue.put(task);
        } catch (InterruptedException e) {
            logger.error("weChatMessage专用任务队列-队列put任务出错：{}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 删除任务
     *
     * @param task
     * @return
     */
    public boolean removeTask(Runnable task) {
        return queue.remove(task);
    }
}
