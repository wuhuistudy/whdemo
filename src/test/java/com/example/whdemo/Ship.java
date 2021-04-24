package com.example.whdemo;
public class Ship {
    static int number;//该船占的格子数
    int live;//现在存活的数量
    int i;//船头的行
    int j;//船头的列
    char direction;//船头的方向

    public Ship(Filed f,char s,int i,int j,int number)
    {this.i=i;
        this.j=j;
        live=number;//存活数为live
        f.addship();
        if(s=='H'||s=='h')
        {
            this.direction='h';

            for(int k=0;k<number;k++)
            {
                f.get(i,j+k).setI();
                f.get(i,j+k).setShip(this);
            }
        }
        else //在没有异常时等价于if(s=='V'||s=='v')
        {
            this.direction='v';
            for(int k=0;k<number;k++)
            {
                f.get(i+k,j).setI();
                f.get(i+k,j).setShip(this);
            }
        }
    }

    public boolean living()
    {
        if(live==0)   return false;
        return true;
    }

    public static int getnumber()
    {
        return number;
    }
}
