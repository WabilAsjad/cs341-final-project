package clientserver;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.*;


public class Measurements{
    private static String command;
    private static ExecutorService pool;

    public static void main(String[] args){
        ArrayList newList = ListofRequests.getList();
        command = newList.get(0).toString();

        Runnable run = new Runnable(){
            @Override
            public void run(){
                try{
                    Client.runClient(command);
                }catch(IOException ex){
                    System.out.println("Error: " + ex.getMessage());
                    pool.shutdown();
                }
            }
        };
        pool = Executors.newFixedThreadPool(10);
        int count = 1;
        while(count != 10){
            pool.execute(run);
            count++;
        }
        pool.shutdown(); 
    }
}