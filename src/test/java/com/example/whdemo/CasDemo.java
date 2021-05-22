package com.example.whdemo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * cas : compareAndSet 比较并替换
 */
public class CasDemo {
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        boolean b = atomicInteger.compareAndSet(5, 2021);
        System.out.println("b = " + b);
        System.out.println(atomicInteger.get());
        boolean b1 = atomicInteger.compareAndSet(5, 2021);
        System.out.println("b1 = " + b1);
        System.out.println(atomicInteger.get());
    }
}
