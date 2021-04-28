package com.example.whdemo;

public class Node {
    private int i;//保存为1说明已经放了元素
    private Ship s;//当前这个Node属于哪个船

    public Node()
    {
        i=0;
    }

    public int getI() {
        return i;
    }

    public Ship getShip()
    {
        return s;
    }


    public void setI() {
        this.i=1;
    }

    public void setShip(Ship s)
    {
        this.s=s;
    }

    public boolean attacked() {
        //如果被攻击，值变为0,所在的船生命-1
        if(i==1)
        {
            i=0;
            this.s.live--;
            return true;
        }

        return false;
    }
}
