package com.example.whdemo;

import java.util.Scanner;

public class Main {
    //放的时候船头总是保持在左或上
    //horizon是水平（横着）     vertical是竖着
    //用H或h            用V或v
    //假定输入的都是正确的格式
    public static boolean set(Filed f,char s,int i,int j,int number)
    {//判断是否可以放

        if(s=='H'||s=='h')
        {for(int k=0;k<number;k++)//判断是否已经放了
            if(f.get(i,j+k).getI()==1)  return false;

            return true;
        }
        else if(s=='V'||s=='v')
        {for(int k=0;k<number;k++)//判断是否已经放了
            if(f.get(i+k,j).getI()==1)  return false;

            return true;
        }
        else  return false;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Scanner in = new Scanner(System.in);
        Filed  f1=new Filed();
        //像下面的if一样，如果能放置，才去放置
        //后面四个也要加上，为省劲不再写了
        new Aircraft_carrier(f1,'h',0,0);
        new Battleship(f1,'h',1,0);
        new Cruiser(f1,'h',2,0);
        new Destroyer(f1,'h',3,0);
        new Submarine(f1,'h',4,0);

        //实际游戏中，不需要输出当前放置情况
        System.out.println("玩家1矩阵:");
        for(int i=0;i<f1.GetBorder_length();i++)
        {for(int j=0;j<f1.GetBorder_length();j++)
        {int aa=f1.get(i, j).getI();
            System.out.print(aa);}
            System.out.println();}
        System.out.println();
        ///
        Filed  f2=new Filed();
        new Aircraft_carrier(f2,'v',0,0);
        new Battleship(f2,'v',0,1);
        new Cruiser(f2,'v',0,2);
        new Destroyer(f2,'v',0,3);
        new Submarine(f2,'v',0,4);


        System.out.println("玩家2矩阵:");
        for(int i=0;i<f2.GetBorder_length();i++)
        {for(int j=0;j<f2.GetBorder_length();j++)
        {int aa=f2.get(i, j).getI();
            System.out.print(aa);}
            System.out.println();}
        System.out.println();
        //首先	1攻2         然后     2攻1
        //我们让2为电脑，随机的
        //所以2的每一步都要停一下，看一看
        int row;
        int col;
        boolean bb;
        while(true)
        {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();}
            bb=true;
            System.out.println("现在是玩家1在进攻");

            while(bb&&f2.getship()>0)
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();}
                System.out.println("输入行和列(用空格分隔)");
                row=in.nextInt();
                col=in.nextInt();
                bb=f2.get(row, col).attacked();
                //打中了
                if(bb)
                {
                    System.out.println("打中了");
                    /*判断船是否活着*/
                    if(!f2.get(row, col).getShip().living())
                    {f2.reduceship();
                    }
                }
            }

            //下面的try catch 是处理Thread.sleep
            //Thread.sleep是程序中断  毫秒数
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();}

            if(bb==false)
            {System.out.println("未打中");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();}
                System.out.println("玩家2还剩余"+
                        f2.getship()+"只船");
                System.out.println();
            }
            else
            {System.out.println("玩家1打沉了所有船！");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();}
                System.out.println("玩家1赢了！");
                break;}
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();}
            bb=true;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();}
            System.out.println("现在是玩家2在进攻");
            while(bb&&f1.getship()>0)
            {try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();}
                //System.out.println("输入行和列(用空格分隔)");
                //下面由输入改成随机生成
                row=(int)(Math.random()*10);
                col=(int)(Math.random()*10);
                System.out.println("玩家二选择行和列为"+row+" "+col);
                //row=in.nextInt();
                //col=in.nextInt();
                bb=f1.get(row, col).attacked();
                //打中了
                if(bb)
                {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();}
                    System.out.println("打中了");

                    /*判断船是否活着*/
                    if(!f1.get(row, col).getShip().living())
                    {f1.reduceship();
                        System.out.println("船被击毁");}
                }
            }
            //下面的try catch 是处理Thread.sleep
            //Thread.sleep是程序中断  毫秒数
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();}

            if(bb==false)
            {System.out.println("未打中");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();}
                System.out.println("玩家1还剩余"+
                        f1.getship()+"只船");
                System.out.println();
            }
            else
            {System.out.println("玩家2打沉了所有船！");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();}
                System.out.println("玩家2赢了！");
                break;}
        }
        in.close();
    }
}
