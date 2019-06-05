package com.demo.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	
	private static int DEFAULT_PORT = 12345;
	private static ServerSocket server;

	//线程池 
    private static ExecutorService executorService = Executors.newFixedThreadPool(60);  


	public static void start() throws IOException {
		start(DEFAULT_PORT);
	}

	public synchronized static void start(int port) throws IOException {
		if (server != null) {
			return;
		}
		
		try {
			//启动服务
			server = new ServerSocket(port);
			System.out.println("服务器已启动，端口号：" + port);

			// 通过无线循环监听客户端连接
			while (true) {
				Socket socket = server.accept();
				// 当有新的客户端接入时，会执行下面的代码
				 executorService.execute(new ServerHandler(socket));
			}
		} finally {
			if (server != null) {
				System.out.println("服务器已关闭。");
				server.close();
			}

		}

	}
	
	public static void main(String[] args) throws InterruptedException {

		// 运行服务端
		new Thread(new Runnable() {
			public void run() {
				try {
					Server.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
}
