package com.example.tutorial;

import java.util.concurrent.*;

public class concurrentToolMain {

    public static void main(String[] args) throws InterruptedException {
        //1.CountDownLatch
        //countDownLatchExam();
        //2.CyclicBarrier
        //CyclicBarrierExam();
        //3.Semaphore
        //semaphoreExam();
        //4.exchanger

        //提供一个同步点，在这个同步点两个线程交互，否则都先等等，
        // 可用于校对工作，A的校对结果和B的校对结果应该一致才行
        exchangerExam();
    }

    private static void exchangerExam() {
        final Exchanger<String> exchanger=new Exchanger<String>();//线程间协作，同时 调用exchange方法的时候才能走下去
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String recv= exchanger.exchange("1111111");//可设置超时时间
                    System.out.println(Thread.currentThread().getName()+"接收到"+recv);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    String recv =exchanger.exchange("222222");
                    System.out.println(Thread.currentThread().getName()+"接收到"+recv);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
//      应该两两配对的线程才行，否则单数的线程会一直等待另一个线程
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                    String recv =exchanger.exchange("333333");
//                    System.out.println(Thread.currentThread().getName()+"接收到"+recv);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    private static void semaphoreExam() {
        final Semaphore semaphore=new Semaphore(10);//访问特定资源的线程数量
        for (int i=0;i<30;i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        semaphore.acquire();
                        System.out.println(Thread.currentThread().getName() +"当前线程获取了资源semaphore ， 睡10秒，体现一次性只能 有10个进程可以执行");
                        Thread.sleep(10000);
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //比如银行计算所有的sheet之后 汇总的场景
    private static void CyclicBarrierExam() {
        final CyclicBarrier cyclicBarrier=new CyclicBarrier(2, new Runnable() {
            @Override
            public void run() {
                System.out.println("partie(这里是2)个线程到达屏障时，优先先执行我");
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName()+"屏障之前");
                    //这里就阻塞在屏障面前，等待第2个线程也来时，屏障才放行继续下去。
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName()+"屏障放行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        },"thread1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName()+"屏障之前");
                    //这里就阻塞在屏障面前，等待第2个线程也来时，屏障才放行继续下去。
                    cyclicBarrier.await();
                    System.out.println(Thread.currentThread().getName()+"屏障放行");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        },"thread2").start();
    }
//区别join方法：join方法要等待另一个线程完成之后才能执行当前线程，而CountDownLatch只需要对应方法块执行完成(调用了countDownLatch.countDown())即可
    private static void countDownLatchExam() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(2);//计数2个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName()+"当前线程执行完毕");
            }
        },"thread1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName()+"当前线程执行完毕");

            }
        },"thread2").start();
        //一直等待，直到 构造函数中的2 个线程 完成(调用countDown) 才继续执行下去
        countDownLatch.await();
        System.out.println("等待完成");
    }
}
