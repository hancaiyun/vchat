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
 * �����ҷ�������
 * @author Administrator
 */
public class ChatService{
    public static  JTextArea showText;
    /*
     * �����ڷ������˵�ServerSocket���ڴ򿪷���˿ڣ��������ö˿�
     * ����ͨ���ö˿����ӵķ���˽���ͨѶ��
     */
    private ServerSocket server;
    /*
     * �����ϣ����ڱ������пͻ��˵������
     * ���ڽ���Ϣ�㲥�o���пͻ���
     */
    private List<PrintWriter> allOut;

    // ���ڴ洢���ӵ����������û��Ϳͻ����׽��ֶ���
    //private Hashtable<String, Socket> map = new Hashtable<String, Socket>();

    public ChatService() throws Exception{
        try{
			/*
			 * ��ʼ��ServerSocket
			 * ��ʼ��ʱҪ��ָ������˿ڣ��ͻ���
			 * ����ͨ���ö˿����ӵ�����˵�
			 */
            allOut=new ArrayList<PrintWriter>();
            server=new ServerSocket(5000);
        }catch(Exception e){
            System.out.println("����˳�ʼ��ʧ�ܣ�");
            throw e;
        }
    }
    private synchronized void addOut(PrintWriter out){
        allOut.add(out);
    }
    //���������������ӹ�������ɾ��
    private synchronized void removeOut(PrintWriter out){
        allOut.remove(out);
    }
    //���������ϣ�����Ϣ���͸�ÿ���ͻ���
    private synchronized void sendMessage(String message){
        for(PrintWriter out:allOut){
            out.println(message);
        }
    }

    /*
     * ����˵���������
     */
    public void start() throws Exception{
        try{
            System.out.print("������������,�����ͻ�������. ");
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
			 * ServerSocket�ṩ��accept��������һ�������������÷�����
			 * ��������Ķ˿ڣ������ǡ�5000����ֱ��һ���ͻ���ͨ���ö�
			 * �����ӷ�����ʱ��accept�����Ż���������������һ�� Socket
			 * ��ÿͻ��˽���ͨѶ�������ٴμ��������ͻ��˵����ӣ�����Ҫ
			 * �ٴε���accept���������ܸ�֪����
			 */
        }catch(Exception e){
            System.out.println("����������ʧ�ܣ�");
            throw e;
        }
    }
    public static void main(String[] args) {
        try{
            //ͨ�ŷ���
            ChatService server=new ChatService();
            server.start();
        }catch(Exception e){
            System.out.println("���������ʧ�ܣ�");
            e.printStackTrace();
        }
    }
    private class ClientHandler implements Runnable{
        /*��ǰ�߳����Socket��Ӧ�Ŀͻ��˽���
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
                sendMessage(host1+"/������");//�б������ɾ��
                System.out.println(host1+"/������");

                OutputStream out=socket.getOutputStream();
                OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
                pw=new PrintWriter(osw,true);
                //��������ÿͻ��˷�����Ϣ����������빲����
                addOut(pw);

				/*
				 * InputStream getInpuStream()
				 * Socket�ĸ÷���������ȡһ��������������Զ�ˣ�������ǿͻ��ˣ�
				 * ���͹���������
				 */
                InputStream in=socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(in,"UTF-8");
                BufferedReader br=new BufferedReader(isr);
                String message=null;
                while((message=br.readLine())!=null){
					/*
					 * ͨ��br.readLine()��ȡ�ͻ��˷��͹�����ÿһ���ַ�
					 * ���ڿͻ������ڵ�ϵͳ��ͬ�����ͻ�����������Ͽ�
					 * ���Ӻ����˵���������Ľ���ǲ�ͬ�ġ�
					 * ��windows�Ŀͻ��˶Ͽ�����ʱ��readLine������
					 * ֱ���׳��쳣��
					 * ��Linux�Ŀͻ��˶Ͽ����Ӻ�readLine�����᷵��null
					 */
                    int index=message.indexOf(":");
                    host2=message.substring(0,index);
                    System.out.println(host1+"/"+message);
                    addChatLog((new Date()).toString()+"/"+host1+"/"+message+"\n");
                    sendMessage(message);
                }
            }catch(Exception e){
            }finally{
                sendMessage(host2+"������");//�б������ɾ��

                System.out.println(host1+"/"+host2+"������");
                //��������ӹ�������ɾ��
                removeOut(pw);
                try{
                    socket.close();
                }catch(Exception e2){
                }
            }
        }
    }

    //��¼��־
    private void addChatLog(String message) throws IOException {
            FileOutputStream fos=new FileOutputStream("chatLog.txt",true);
            byte[] data=message.getBytes("GBK");
            fos.write(data);
            fos.close();
        }
}

