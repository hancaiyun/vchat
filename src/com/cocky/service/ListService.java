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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 列表监听
 * Created by cocky on 17-6-8.
 */
public class ListService {

    private ServerSocket server;
    private PrintWriter pw;

    public ListService() throws Exception{
        try{

            server=new ServerSocket(5003);
        }catch(Exception e){
            System.out.println("列表服务初始化失败！");
            throw e;
        }
    }

    public void start() throws Exception{
        try{
            System.out.print("列表服务已启动. ");
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
            System.out.println("列表服务注册失败！");
            throw e;
        }
    }
    public static void main(String[] args) {
        try{
            //通信服务
            ListService server=new ListService();
            server.start();
        }catch(Exception e){
            System.out.println("列表服务启动失败！");
            e.printStackTrace();
        }
    }
    private class ClientHandler implements Runnable{

        private Socket socket;

        public ClientHandler(Socket socket){
            this.socket=socket;
        }
        public void run() {
            try{
                System.out.println("列表请求");

                OutputStream out=socket.getOutputStream();
                OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
                pw=new PrintWriter(osw,true);

                InputStream in=socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(in,"UTF-8");
                BufferedReader br=new BufferedReader(isr);
                String message=br.readLine();
                //区分列表是增是减
                if(message.substring(0,1).equals("+")){
                    //TODO 查询list，如果已存在此用户，则不addData（重复登陆）
                    addData(message.substring(1));
                    System.out.println("上线通知");//
                }else if(message.substring(0,1).equals("-")){
                    delData(message.substring(1));
                    System.out.println("下线通知");
                }else{
                    pw.println("wrong");
                    System.out.println("错误通知,错误信息："+message);//
                    return;
                }
                //拼字符串返回
                pw.println(appendLine());
                System.out.println("返回的列表串:"+appendLine());
                socket.close();
            }catch(Exception e){
            }finally{
                System.out.println("列表已刷新\n");
                try{
                    socket.close();
                }catch(Exception e2){
                }
            }
        }
    }

    //拼line
    private String appendLine() {
        String line = null;
        String lines = "";
        try {
            FileInputStream fis = new FileInputStream("list.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                lines=lines+line+";";
            }
        } catch (Exception e) {
            System.out.print("读取列表失败");
            pw.print("wrong");
        }
        return lines;
    }

    //删除行
    public void delData(String name) throws IOException{

        FileInputStream fis=new FileInputStream("list.txt");
        InputStreamReader isr=new InputStreamReader(fis);
        BufferedReader br=new BufferedReader(isr);
        String line=null;
        List<String> list = new ArrayList<>();
        while((line=br.readLine())!=null){
            if(line.equals(name)) {
                continue;
            }
            list.add(line);
        }

        FileOutputStream fos=new FileOutputStream("list.txt",false);
        for(String user:list) {
            user = user+"\r\n";
            byte[] data = user.getBytes("UTF-8");
            fos.write(data);
        }
        br.close();
        fos.close();
    }

    //新增行
    public void addData(String name) throws IOException{
        String line="\n"+name;
        FileOutputStream fos=new FileOutputStream("list.txt",true);
        byte[] data=line.getBytes("UTF-8");
        fos.write(data);
        fos.close();
    }
}
