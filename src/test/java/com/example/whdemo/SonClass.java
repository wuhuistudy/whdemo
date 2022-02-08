package com.example.whdemo;

public class SonClass extends ParentClass {
    // 静态变量
    public static String p_StaticField = "子类--静态变量";

    // 变量
    public static String Field = "子类--变量";

    protected int j = 0;

    protected int i = 0;

    // 静态初始化代码块
    static {
        System.out.println(p_StaticField);
        System.out.println("子类--静态初始化代码块");
    }

    // 构造方法
    public SonClass(){
        System.out.println("子类--构造方法");
    }

    // 普通初始化代码块
    {
        System.out.println("子类--普通初始化代码块");
    }

    public static void main(String[] args) {
        new SonClass();
    }
}
