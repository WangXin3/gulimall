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



    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main11(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main....start.....");

        // TODO 异步起线程执行业务
//        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, executorService);

        // TODO 异步起线程执行业务
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).whenComplete((res,exc)->{
////                 可以接收到返回值和异常类型，但是无法处理
//            System.out.println("异步任务成功完成了...结果是：" + res + ";异常是：" + exc);
//        }).exceptionally(throwable -> {
////                 返回一个自定义的值，和上文返回值无关。
//            return 10;
//        });


        //方法执行完成后的处理
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).handle((res,thr)->{
////             无论线程是否正确执行，都会执行这里，可以对返回值进行操作。
//            if(res != null){
//                return res * 2;
//            }
//            if(thr != null){
//                return 0;
//            }
//            return 0;
//        });

        /**
         * 线程串并行操作。
         */

        // 两个任务串行执行，第二个线程要使用第一个线程的返回值，并且返回第二个线程的返回值。
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executorService).thenApplyAsync(res -> {
//            System.out.println("任务2启动了" + res);
//            return "Hello" + res;
//        }, executorService);
//        String s = future.get();



        /**
         * 两个都完成的情况下，执行任务3
         */
//        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务1线程：" + Thread.currentThread().getId());
//            int i = 10 / 4;
//            System.out.println("任务1结束：");
//            return i;
//        }, executorService);

//        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务2线程：" + Thread.currentThread().getId());
//            try {
//                Thread.sleep(3000);
//                System.out.println("任务2结束");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "Hello";
//        }, executorService);

        // 两个任务都完成，在不使用两个任务返回值的情况下执行任务3，并且不返回值
//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务3开始");
//        },executorService);

        // 两个任务都完成，任务3需要任务1和任务2的结果，并且不返回值
//        future01.thenAcceptBothAsync(future02, (f1,f2)->{
//            System.out.println("任务3开始，之前的结果" + f1 + "-->" + f2);
//        },executorService);

        // 两个任务都完成，任务3需要任务1和任务2的结果，并且返回值
//        CompletableFuture<String> future = future01.thenCombineAsync(future02, (f1, f2) -> {
//            return f1 + ":" + f2 + "->haha";
//        }, executorService)

        /**
         * 两个任务只要有一个完成，我们就执行任务3
         */
        // 不使用future01或future02线程的结果，执行任务3，并且不返回值
//        future01.runAfterEitherAsync(future02,()->{
//            System.out.println("任务3开始，之前的结果");
//        }, executorService);

        // 使用future01或future02线程的结果，执行任务3，并且不返回值
//        future01.acceptEitherAsync(future02, (res)->{
//            System.out.println("任务3开始，之前的结果" + res);
//        },executorService);

        // 使用future01或future02线程的结果，执行任务3，并且返回值
//        CompletableFuture<String> future = future01.applyToEitherAsync(future02, (res) -> {
//            System.out.println("任务3开始，之前的结果" + res);
//            return res.toString() + "->哈哈";
//        }, executorService);


        CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> "任务1", executorService);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> "任务2", executorService);
        CompletableFuture<String> future03 = CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务3";
        }, executorService);

//             只要异步线程队列有一个任务率先完成就返回，这个特性可以用来获取最快的那个线程结果。
//        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future01, future02, future03);
//        // 获取若干个任务中最快完成的任务结果
//        Object o = anyOf.get();

        CompletableFuture<Void> all = CompletableFuture.allOf(future01, future02, future03);
        // 等待所有结果完成
        all.join();

        System.out.println("main....end.....");
    }

    public void thread() {
        System.out.println("main....start.....");
        /**
         * 1. 继承Thread
         *         Thread01 thread = new Thread01();
         *           thread.start(); //启动线程
         *
         * 2. 实现Runnable接口
         *        Runnable01 runnable01 = new Runnable01();
         *         new Thread(runnable01).start();
         * 3. 实现Callable接口+FutureTask
         *       FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
         *         new Thread(futureTask).start();
         *         //阻塞等待整个线程执行完成，获取返回结果
         *         Integer integer = futureTask.get();
         * 4. 线程池
         *      给线程池直接提交任务。
         *          1、创建：
         *              1)、Executors
         *              2)、new ThreadPoolExecutor
         *
         * 区别：1,2不能得到返回值，3可以获取返回值
         *      1,2,3都不能控制资源
         *      4可以控制资源，性能稳定
         */

        //以后再业务代码里面，以上三种启动线程的方式都不用。应该【将所有的多线程异步任务都交给线程池执行】
//        new Thread(()->{
//            System.out.println("hello");
//        }).start();

        //当前系统中池只有一两个，每个异步任务，提交给线程池让他自己去执行就行。
//        executorService.execute(new Runnable01());
        /**
         * 七大参数
         * corePoolSize:[5]核心线程数[一直存在]；线程池，创建好以后就准备就绪的线程数量，就等待来接受异步任务去执行
         *     5个 Thread thread = new Thread();
         * maximumPoolSize[200]:最大线程数量；控制资源
         * keepAliveTime:存活时间。如果当前的线程数量大于core数量
         *      释放空闲的线程(maximumPoolSize-corePoolSize)。只要线程空闲大于指定的keepAliveTime;
         * unit:时间单位
         * BlockingQueue<Runnable> workQueue:阻塞队列。如果任务有很多，就会将目前多的任务放在队列里面。
         *                                  只要有线程空闲，就会取队列里取出新的任务继续
         * threadFactory:线程的创建工厂。
         * RejectedExecutionHandler handler:如果队列满了，按照我们指定的拒绝策略拒绝执行任务。
         *
         *
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
        System.out.println("main....end.....");
    }
}
