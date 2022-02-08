package com.example.whdemo;

public class ParentClass {

    // 静态变量
    public static String p_StaticField = "父类--静态变量";

    // 变量
    public static String Field = "父类--变量";

    protected int j = 0;

    protected int i = 0;

    // 静态初始化代码块
    static {
        System.out.println(p_StaticField);
        System.out.println("父类--静态初始化代码块");
    }

    // 构造方法
    public ParentClass(){
        System.out.println("父类--构造方法");
    }

    // 普通初始化代码块
    {
        System.out.println("父类--普通初始化代码块");
    }

}
