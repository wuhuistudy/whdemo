package com.example.whdemo;

import java.util.HashMap;

public class TestDemo
{
    public static void main(String[] args)
    {
        HashMap hashMap = new HashMap(3);
        hashMap.put(1,"111");
        hashMap.put(2,"222");
        hashMap.put(3,"333");
        String str = (String)hashMap.get(2);

        ClassLoader loader = Panda.class.getClassLoader();
        while (loader != null) {
            System.out.println(loader.toString());
            loader = loader.getParent();
        }
    }
}
