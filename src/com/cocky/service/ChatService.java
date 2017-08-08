package com.cocky.service;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JTextArea;

/**
 * 聊天室服务器端
 * @author Administrator
 */
public class ChatService{
    public static  JTextArea showText;
    /*
     * 运行在服务器端得ServerSocket用于打开服务端口，并监听该端口
     * ，与通过该端口连接的服务端进行通讯。
     */
    private ServerSocket server;
    /*
     * 共享集合，用于保存所有客户端的输出流
     * 便于将消息广播給所有客户端
     */
    private List<PrintWriter> allOut;

    // 用于存储连接到服务器的用户和客户端套接字对象
    //private Hashtable<String, Socket> map = new Hashtable<String, Socket>();

    public ChatService() throws Exception{
        try{
			/*
			 * 初始化ServerSocket
			 * 初始化时要有指定服务端口，客户端
			 * 就是通过该端口连接到服务端的
			 */
            allOut=new ArrayList<PrintWriter>();
            server=new ServerSocket(5000);
        }catch(Exception e){
            System.out.println("服务端初始化失败！");
            throw e;
        }
    }
    private synchronized void addOut(PrintWriter out){
        allOut.add(out);
    }
    //将给定的输入流从共享集合中删除
    private synchronized void removeOut(PrintWriter out){
        allOut.remove(out);
    }
    //遍历共享集合，将消息发送给每个客户端
    private synchronized void sendMessage(String message){
        for(PrintWriter out:allOut){
            out.println(message);
        }
    }

    /*
     * 服务端的启动方法
     */
    public void start() throws Exception{
        try{
            System.out.print("服务器已启动,正待客户端连接. ");
            for(int i=0;i*1000<5000;i++){
                System.out.print(". ");
                Thread.sleep(i*500)	;
            }
            System.out.println();
            while(true){
                Socket socket=server.accept();
                ClientHandler handler=new ClientHandler(socket);
                Thread t=new Thread(handler);
                t.start();
            }
			/*
			 * Socket accept()
			 * ServerSocket提供的accept方法就是一个阻塞方法，该方法会
			 * 监听申请的端口，这里是”5000“。直到一个客户端通过该端
			 * 口连接服务器时，accept方法才会解除阻塞，并创建一个 Socket
			 * 与该客户端进行通讯。若想再次监听其他客户端的链接，还需要
			 * 再次调用accept方法，才能感知到。
			 */
        }catch(Exception e){
            System.out.println("服务器运行失败！");
            throw e;
        }
    }
    public static void main(String[] args) {
        try{
            //通信服务
            ChatService server=new ChatService();
            server.start();
        }catch(Exception e){
            System.out.println("服务端启动失败！");
            e.printStackTrace();
        }
    }
    private class ClientHandler implements Runnable{
        /*当前线程与该Socket对应的客户端交互
         * @see java.lang.Runnable#run()
         */
        private String host1;
        private String host2;
        private Socket socket;
        public ClientHandler(Socket socket){
            this.socket=socket;
            InetAddress address=socket.getInetAddress();
            host1 = address.getHostAddress();
        }
        public void run() {
            PrintWriter pw=null;
            try{
                sendMessage(host1+"/上线了");//列表出来后删掉
                System.out.println(host1+"/上线了");

                OutputStream out=socket.getOutputStream();
                OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
                pw=new PrintWriter(osw,true);
                //将用于向该客户端发送消息的输出流存入共享集合
                addOut(pw);

				/*
				 * InputStream getInpuStream()
				 * Socket的该方法用来获取一个输入流，来读远端（这里就是客户端）
				 * 发送过来的数据
				 */
                InputStream in=socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(in,"UTF-8");
                BufferedReader br=new BufferedReader(isr);
                String message=null;
                while((message=br.readLine())!=null){
					/*
					 * 通过br.readLine()读取客户端发送过来的每一行字符
					 * 由于客户端坐在的系统不同，当客户端与服务器断开
					 * 连接后服务端的这个方法的结果是不同的。
					 * 当windows的客户端断开连接时，readLine方法会
					 * 直接抛出异常。
					 * 当Linux的客户端断开连接后，readLine方法会返回null
					 */
                    int index=message.indexOf(":");
                    host2=message.substring(0,index);
                    System.out.println(host1+"/"+message);
                    addChatLog((new Date()).toString()+"/"+host1+"/"+message+"\n");
                    sendMessage(message);
                }
            }catch(Exception e){
            }finally{
                sendMessage(host2+"下线了");//列表出来后删掉

                System.out.println(host1+"/"+host2+"下线了");
                //将输出流从共享集合中删除
                removeOut(pw);
                try{
                    socket.close();
                }catch(Exception e2){
                }
            }
        }
    }

    //记录日志
    private void addChatLog(String message) throws IOException {
        FileOutputStream fos=new FileOutputStream("chatLog.txt",true);
        byte[] data=message.getBytes("UTF-8");
        fos.write(data);
        fos.close();
    }
}

