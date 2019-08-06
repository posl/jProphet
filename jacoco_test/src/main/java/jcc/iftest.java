package jcc;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;


public class iftest implements Runnable {
    public void run(){

        System.out.println("RRUUUN");

    }
    public int suuji(int x) {
        if (x == 1){
            System.out.println("111111111111");
        }else if(x == 2){
            System.out.println("222222222222");
        }else{
            System.out.println("OTHERRRRRRRRRRRRRRR");
        }

        return x;
    }

}