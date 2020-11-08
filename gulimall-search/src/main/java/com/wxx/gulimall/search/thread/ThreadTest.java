package com.wxx.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * @author 她爱微笑
 * @date 2020/11/8
 */
public class ThreadTest {

    public static ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main1(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");
//        Thread01 thread01 = new Thread01();
//        thread01.start();
//
//
//        Runnable01 runnable01 = new Runnable01();
//        new Thread(runnable01).start();


//        FutureTask<Integer> task = new FutureTask<>(new Callable01());
//        new Thread(task).start();
//        // 等待线程执行完（阻塞被调用线程，也就是这里的主线程），获取结果
//        Integer integer = task.get();
//        System.out.println(integer);


//        pool.execute(new Runnable01());
//        pool.shutdown();


        /**
         * 五个必填
         * corePoolSize：        核心线程数，一直存在，就算什么都不干也会存在；
         * maximumPoolSize：     最大线程数（核心线程和非核心线程），线程池允许最大线程数，非核心线程闲置到一定时间会被销毁。
         * keepAliveTime：       ⾮核⼼线程闲置超时时⻓。
         * unit：                闲置超时时间单位。
         * workQueue：           阻塞队列，维护着等待执行的Runnable任务对象。
         *
         * 两个选填
         * threadFactory：       创建线程的工厂用于批量创建线程，统⼀在创建线程时设置⼀些参数，如是否守护线程、线程的优先级等
         * handler：             拒绝处理策略
         *
         */

        System.out.println("main...end...");

    }


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main...start...");

//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getName());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, pool);

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getName());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//
//            return i;
//        }, pool).whenComplete((integer, throwable) -> {
//            System.out.println(integer);
//            System.out.println(throwable);
//        }).exceptionally(throwable -> {
//
//            // 可以感知异常，并返回指定值
//            return 10;
//        });

//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getName());
//            int i = 10 / 3;
//            System.out.println("运行结果：" + i);
//
//            return i;
//        }, pool).handle((integer, throwable) -> {
//            // 方法执行完成之后的处理，无论成功或者失败。
//            System.out.println(integer);
//            System.out.println(throwable);
//            if (integer == null) {
//                integer = 10 * 2;
//            }
//
//            return integer;
//        });


        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 3;
            System.out.println("运行结果：" + i);

            return i;
        }, pool).thenRunAsync(() -> {
            System.out.println();
        }, pool);

        System.out.println(future.get());

        System.out.println("main...end...");
    }

    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runnable01 implements Runnable {

        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Callable01 implements Callable<Integer> {

        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getName());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
}
