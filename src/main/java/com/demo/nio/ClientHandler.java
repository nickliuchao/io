package com.demo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ClientHandler implements Runnable {

	private SocketChannel socketChannel;
	private int port;
	private Selector selector;
	private String host;
	private boolean stop;

	public ClientHandler(String host, int port) {

		this.host = host;
		this.port = port;

		try {
			selector = Selector.open();
			socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	public void run() {
		try {
			doConnect();// 连接
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (!stop) {
			try {
				selector.select(1000);
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> it = selectionKeys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					handleInput(key);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if (key.isValid()) {
			SocketChannel sc = (SocketChannel) key.channel();
			if (key.isConnectable()) {
				if (sc.finishConnect()) {
				} else {
					System.exit(1);
				}
			}
			if (key.isReadable()) {// 读取消息
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				int read = sc.read(readBuffer);
				if (read > 0) {
					readBuffer.flip();
					byte[] bytes = new byte[readBuffer.remaining()];
					readBuffer.get(bytes);
					String body = new String(bytes, "utf-8");
					System.out.println("现在时间为：" + body);
					this.stop = true;
				} else if (read < 0) {
					key.cancel();
					sc.close();
				} else {
				}
			}

		}
	}

	private void doConnect() throws IOException {
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			socketChannel.register(selector, SelectionKey.OP_READ);
		} else {
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	private void doWrite(SocketChannel socketChannel,String request)throws IOException {
		byte[] bytes = request.getBytes();
		ByteBuffer writeBuff = ByteBuffer.allocate(bytes.length);
		writeBuff.put(bytes);
		writeBuff.flip();
		socketChannel.write(writeBuff);
		if (!writeBuff.hasRemaining()) {
			System.out.println("客户端发送命令成功");
		}
	}
	

	public void sendMsg(String msg) throws Exception{  

        doWrite(socketChannel, msg);  
	
    }  


}