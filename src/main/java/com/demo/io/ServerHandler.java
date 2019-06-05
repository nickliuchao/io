package com.demo.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerHandler implements Runnable{
	private Socket socket;
	 
    public ServerHandler(Socket socket) {
        this.socket = socket;
    }
    
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        String msg = null;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(),true);
            while ((msg = in.readLine()) != null && msg.length()!=0) {//当连接成功后在此等待接收消息（挂起，进入阻塞状态）
                System.out.println("server received : " + msg);
                out.print("received~\n");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
