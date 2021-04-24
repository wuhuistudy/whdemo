package com.example.whdemo;

public class Filed {
    private   Node n[][]=new Node[10][10];
    private int Border_length=10;
    private  int  ship=0;
    public Filed()
    {
        for(int i=0;i<n.length;i++)
            for(int j=0;j<n[i].length;j++)
                n[i][j]=new Node();
    }

    public  Node  get(int i,int j)
    {
        return  n[i][j];
    }

    public  int  GetBorder_length()
    {
        return  Border_length;
    }


    public  void addship()
    {
        ship++;
    }

    public void  reduceship()
    {
        ship--;
    }

    public  int getship()
    {
        return ship;
    }
}
