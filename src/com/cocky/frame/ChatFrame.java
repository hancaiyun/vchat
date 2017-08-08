package com.cocky.frame;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatFrame {
	private JFrame frame;
	private static JTextField  message;
	private   Socket socket;
	private static JTextArea showText;	
	private   String name;
	private static JTextArea notice;
	private static JLabel NOTICE;
	private static JLabel table;
	private static JTextArea userTable;
	private PrintWriter pw;
	public ChatFrame(final Socket socket,String name,String IP) {
		this.socket=socket;
		this.name=name;
		frame = new JFrame();
		notice=new JTextArea(6,18);
		NOTICE=new JLabel("           NOTICE           ");
		notice.setEditable(false);//???????????
		notice.setLineWrap(true);
		
		DateFormat df = DateFormat.getDateInstance();// ???DateFormat???
		String dateString = df.format(new Date()); // ??????????
//		df = DateFormat.getTimeInstance(DateFormat.MEDIUM);// ???DateFormat???
		notice.setText("    This is a small chat room that we can talk to everyone who are online,please notice your talk.\n    Thanks!\n\n "
				+ "                                          "+dateString);

		table=new JLabel("ONLINE---                                                ");
		userTable=new JTextArea(12,18);
		userTable.setLineWrap(true);
		userTable.setEditable(false);
		try {
			Socket socket1=new Socket(IP,5003);
			//????NAME
			OutputStream out=socket1.getOutputStream();
			OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
			pw=new PrintWriter(osw,true);
			pw.println("+"+name);

			//??????????
			BufferedReader br=new BufferedReader(new InputStreamReader(socket1.getInputStream(),"UTF-8"));
			String line = br.readLine();
			System.out.println("返回的列表信息"+br.readLine());
			if("wrong".equals(line) || line == null){
					userTable.setText("                 List refresh failure!");
				}else {
				String[] strs = line.split(";");
				for (int i = 0; i < strs.length; i++) {
					userTable.setText(strs[i] + "\n");
				}
			}
		}catch (Exception e){
			System.out.println("列表刷新失败");
			userTable.setText("                 List refresh failure!");
		}finally {
			pw.close();

		}

		showText=new JTextArea(21,17);//(20,35)
		showText.setLineWrap(true);//??????У????????????????????
		showText.setEditable(false);//???????????
		message=new JTextField(17);
		message.addKeyListener(new KeyAdapter()  {	//???????ENTER
			public void keyReleased(KeyEvent e) {
				if("".equals(message.getText())){
					return;
				}else if(e.getKeyCode()==KeyEvent.VK_ENTER){//??SHIFT+ENTER??????? //
					try {
						start(socket);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "ERROR");
					}
					message.setText(null);
				}
			}    
		});
		init();
	}
	public void init() {
		JPanel jp1=new JPanel(new FlowLayout());
		jp1.setBackground(Color.WHITE);
		//???????????????

		JScrollPane jsp=new JScrollPane(showText);
		jp1.add(jsp);
		jp1.add(message);

		JScrollPane jsp2=new JScrollPane(notice);
		JScrollPane jsp3=new JScrollPane(userTable);


		JPanel jp2=new JPanel(new FlowLayout());
		jp2.add(NOTICE);
		jp2.add(jsp2);
		jp2.add(table);
		jp2.add(jsp3);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().createImage(RegisterFrame.class.getResource("5.png")));
		frame.setLayout(new GridLayout(1, 2));
		frame.add(jp1);
		frame.add(jp2);
		frame.setTitle("VChat-1.6.1");
		frame.setSize(400, 450);
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void start(Socket socket2) throws Exception{
		try{
			//?????????????????????????????//
			ServerMessageHandler handler=new ServerMessageHandler();
			Thread t=new Thread(handler);
			t.start();
			// ???Socket?????????????????????????????? 	
			OutputStream out=socket2.getOutputStream();
			OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
			PrintWriter pw=new PrintWriter(osw,true);
			//??????? 
			String regex="(cnm|wqnmlgd|dsd|nmb|cnnnd|rnm|djb|SB|???|??B|FUCK|fuck)";
		     String mes=null;
		     mes=message.getText().replaceAll(regex, "***");
			 pw.println(name+":"+mes);
		}catch(Exception e){
			throw e;
		}
	}
	private  class ServerMessageHandler implements Runnable{
		public void run() {
			try{			
				BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
				String str=null;
				while((str=br.readLine())!=null){
						showText.append(str+"\n");
				}				
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, "????????????????","????", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
}
