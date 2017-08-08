package com.cocky.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class RegistService {
    private ServerSocket server;
    private String name;
    private String password;
    private PrintWriter pw;

    public RegistService() throws Exception{
        try{

            server=new ServerSocket(5002);
        }catch(Exception e){
            System.out.println("注册服务端初始化失败！");
            throw e;
        }
    }

    public void start() throws Exception{
        try{
            System.out.print("注册服务已启动. ");
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
        }catch(Exception e){
            System.out.println("注册服务器运行失败！");
            throw e;
        }
    }
    public static void main(String[] args) {
        try{
            //通信服务
            RegistService server=new RegistService();
            server.start();
        }catch(Exception e){
            System.out.println("注册服务端启动失败！");
            e.printStackTrace();
        }
    }
    private class ClientHandler implements Runnable{
        private String host;
        private Socket socket;

        public ClientHandler(Socket socket){
            this.socket=socket;
            InetAddress address=socket.getInetAddress();
            host= address.getHostAddress();
        }
        public void run() {
            try{
                System.out.println(host+"请求注册");
                OutputStream out=socket.getOutputStream();
                OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
                pw=new PrintWriter(osw,true);

                InputStream in=socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(in,"UTF-8");
                BufferedReader br=new BufferedReader(isr);
                String message=null;
                while((message=br.readLine())!=null){

                    int index=message.indexOf(";");
                    name=message.substring(0,index);
                    password=message.substring(index+1);
                    System.out.println("NAME-"+name+";"+"PASSWORD-"+password);
                    sendMsg(name,password);
                    socket.close();
                }
            }catch(Exception e){
            }finally{
                System.out.println(host+"已完成注册");
                try{
                    socket.close();
                }catch(Exception e2){
                }
            }
        }
    }
    public void sendMsg(String name2, String password2) throws SQLException, IOException {
        if(!check(name)){
            pw.println("ok");
            addData(name,password);
        }else{
            pw.println("wrong");
        }

    }
    private boolean check(String name2) throws IOException {
        FileInputStream fis=new FileInputStream("user.txt");
        InputStreamReader isr=new InputStreamReader(fis);
        BufferedReader br=new BufferedReader(isr);

        String line=null;
        boolean checkName=false;
        while((line=br.readLine())!=null){
            int index=line.indexOf(";");
            if(line.substring(0,index).equals(name)){
                checkName=true;
            }
        }
        br.close();
        return checkName;
    }
    public void addData(String name, String password) throws IOException{
        String msg="\n"+name+";"+password;
        FileOutputStream fos=new FileOutputStream("user.txt",true);
        byte[] data=msg.getBytes("GBK");
        fos.write(data);
        fos.close();
    }
}

