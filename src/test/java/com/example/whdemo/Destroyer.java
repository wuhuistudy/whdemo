package com.example.whdemo;

public class Destroyer extends Ship{//驱逐舰 2
    //放的时候船头总是保持在左或上
    //horizon是水平（横着）     vertical是竖着
    //用H或h            用V或v
    static 	int number=2;

    public Destroyer(Filed f, char s, int i, int j) {
        super(f, s, i, j,2);
        // TODO Auto-generated constructor stub
    }
}
