package com.jeesite.modules.kube.work;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadPool
 * @Desctiption : TODO
 * @Author ssg
 * @Date 2020/1/6 16:52
 */
public class ThreadPool  extends  Thread{
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Override
    public void run() {
        super.run();
    }
}
