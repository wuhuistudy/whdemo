package com.example.whdemo;

public class BootstrapTest {
    public static void main(String[] args) {
        //com.example.whdemo.Panda
        ClassLoader loader = Panda.class.getClassLoader();
        while (loader != null) {
            System.out.println(loader.toString());
            loader = loader.getParent();
        }
    }
}
