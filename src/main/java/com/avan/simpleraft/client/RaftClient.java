package com.avan.simpleraft.client;

import java.util.Scanner;

public class RaftClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true){
            String cmd = sc.nextLine();
            if(cmd.equals("exit")){
                return;
            }
            String[] tmp = cmd.split(" ");
            int n = tmp.length;

            if(n == 2){
                if(tmp[0].equals("del")){
                    
                }else if(tmp[0].equals("get")){

                }
            }else if(n == 3){
                if(tmp[0].equals("put")){
                    
                }
            }else{
                System.out.println("illegal, input again");
            }


            

        }
    }
}
