package com.example.whdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CountDownLatchTester {
    public static void main(String[] args) throws InterruptedException {
        //CountDownLatch latch = new CountDownLatch(3);
        CountDownLatch latch = new CountDownLatch(3);

        List<Thread> threads = new ArrayList<>(3);
        threads.add(new Thread(new Customer(latch, "张三")));
        threads.add(new Thread(new Customer(latch, "李四")));
        threads.add(new Thread(new Customer(latch, "王五")));
        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(100);
        new Thread(new Waitress(latch, "♥小芳♥")).start();

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
