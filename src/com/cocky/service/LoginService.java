package com.cocky.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
public class LoginService{
    private ServerSocket server;
    private String name;
    private String password;
    private PrintWriter pw=null;
    private String host=null;

    public LoginService() throws Exception{
        try{
            server=new ServerSocket(5001);
        }catch(Exception e){
            System.out.println("服务端初始化失败！");
            throw e;
        }
    }
    public void start() throws Exception{
        try{
            System.out.print("登录验证服务已启动. ");
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
            System.out.println("服务器运行失败！");
            throw e;
        }
    }
    public static void main(String[] args) {
        try{
            //通信服务
            LoginService server=new LoginService();
            server.start();
        }catch(Exception e){
            System.out.println("服务端启动失败！");
            e.printStackTrace();
        }
    }
    private class ClientHandler implements Runnable{
        private Socket socket;

        public ClientHandler(Socket socket){
            this.socket=socket;
            InetAddress address=socket.getInetAddress();
            host= address.getHostAddress();
        }
        public void run() {
            try{
                System.out.println(host+"请求验证");

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
                    sendMessage(name,password);
                }
            }catch(Exception e){
            }finally{
                System.out.println(host+"已完成验证");
                try{
                    socket.close();
                }catch(Exception e2){
                }
            }
        }
    }
    public void sendMessage(String name, String password) throws SQLException, IOException {
        if(!check(name)){
            pw.println("wrong name");
        }else{
            if(!check(name,password)){
                pw.println("wrong password");
            }else{
                pw.println("ok");//登陆成功返回"ok"
                //将name添加到在线用户数据中，数据库处理数据更好
//                    addData(name);
            }
        }
    }
//	private void addData(String name) {

    //	}
    //访问文件
    public boolean check(String name) throws IOException {
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
    public  boolean check(String name, String password) throws SQLException, IOException {
        FileInputStream fis=new FileInputStream("user.txt");
        InputStreamReader isr=new InputStreamReader(fis);
        BufferedReader br=new BufferedReader(isr);

        String line=null;
        boolean checkPassword=false;
        while((line=br.readLine())!=null){
            int index=line.indexOf(";");
            if(line.substring(0,index).equals(name)){
                if(line.substring(index+1).equals(password)){
                    checkPassword=true;
                }
            }
        }
        br.close();
        return checkPassword;
    }
}

