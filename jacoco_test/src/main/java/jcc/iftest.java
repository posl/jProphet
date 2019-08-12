package jcc;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;


public class iftest {
    public int printNum(int x) {
        if (x == 1){
            System.out.println("echo 1");
        }else if(x == 2){
            System.out.println("echo 2");
        }else{
            System.out.println("echo OTHER");
        }
        return x;
    }
}