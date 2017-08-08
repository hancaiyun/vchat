package com.cocky.frame;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Login {
	ChatFrame cf;
	private JFrame frame;
	private JButton login ;
	private JButton register;
	private JPasswordField pwd;
	private JLabel passWord;
	private Socket socket;
	private JLabel userName;
	private JTextField  name;
	private JLabel sIp;
	private JTextField serviceIp;
	private int Port;
	private String p1,p2;
	private PrintWriter pw;
	private String mes;
	private Socket socket1;
	private String IP;
	public boolean isConnected;

	public Login(){
		IP="192.168.138.31";//服务器IP
		Port=5001;//服务端口号
		pw=null;
		mes=null;
		isConnected=false;
		frame=new JFrame("VChat-1.6.1");
		userName=new JLabel("用户名");
		passWord=new JLabel(" 密 码   ");
		pwd=new JPasswordField(20);
		pwd.setText("123");//可去
		name=new JTextField(20);
		name.setText("COCKY");//可去
		login=new JButton("登陆");
		register=new JButton("注册");

		sIp=new JLabel("服务器IP");
		serviceIp=new JTextField(20);
		init();
		//密码输入框监听Enter，作用等同于“登陆”按钮
		serviceIp.addKeyListener(new KeyAdapter()  {	//键盘监听ENTER
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){//同时按SHIFT+ENTER发送消息 //
					try {
						login();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, "ERROR");
					}
				}
			}
		});
		addEvent();
	}
	private void login(){
		//TODO 这里控制不了免重复连接，因为登陆成功后就已经跳转对话框了
		if (isConnected) {
			JOptionPane.showMessageDialog(null, "已处于连接上状态，不要重复连接!",
					"错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		p1=new String(name.getText());
		p2=new String(pwd.getPassword());
		//先检查用户名或者密码是否为空
		if((p1.equals(""))||(p2.equals(""))){
			JOptionPane.showMessageDialog(null, "用户名或者密码不能为空","错误", JOptionPane.ERROR_MESSAGE);
		}
//		else if(serviceIp.getText().equals("")){
//			JOptionPane.showMessageDialog(null, "请填写正确的服务器IP","错误", JOptionPane.ERROR_MESSAGE);}
		else{
			//调数据库查看用户名和密码是否正确
			try {
				//IP=new String(serviceIp.getText());
				socket1=new Socket(IP,Port);
				//发送NAME和PASSWORD
				OutputStream out=socket1.getOutputStream();
				OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
				pw=new PrintWriter(osw,true);
				pw.println(p1+";"+p2);
				//接收返回信息
				BufferedReader br=new BufferedReader(new InputStreamReader(socket1.getInputStream(),"UTF-8"));
				while((mes=br.readLine())!="OK"){
					if(mes.equals("wrong name")){
						JOptionPane.showMessageDialog(null, "用户名不存在！","错误", JOptionPane.ERROR_MESSAGE);
						name.setText(null);
						pwd.setText(null);
						return;
					}else if(mes.equals("wrong password")){
						JOptionPane.showMessageDialog(null, "密码错误！","错误", JOptionPane.ERROR_MESSAGE);
						pwd.setText(null);
						return;
					}else{
						try{
							socket=new Socket(IP,5000);
							isConnected=true;
							frame.dispose();
							//建立连接后关闭Login窗口开启Chat窗口开始对话
							cf=new ChatFrame(socket,name.getText(),IP);
							return;//跳出监听事件结束当前方法
						}catch(Exception e1){
							JOptionPane.showMessageDialog(null, "无法连接服务器，登陆失败");
							return;
						}
					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
				System.out.println("连接失败！");
				JOptionPane.showMessageDialog(null, "无法连接服务器!");
			}
		}
	}
	private void addEvent() {
		//登录按钮监听事件
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				login();
			}
		});
		//注册按钮事件
		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(serviceIp.getText().equals("")){
					JOptionPane.showMessageDialog(null, "请填写服务器IP","错误", JOptionPane.ERROR_MESSAGE);
				}else{
					new RegisterFrame(serviceIp.getText());
					frame.dispose();
				}
			}
		});
	}
	private void init() {
		JPanel jp1=new JPanel();
		JPanel jp2 = new JPanel(new FlowLayout());
		jp2.add(userName);
		jp2.add(name);

		JPanel jp3=new JPanel(new FlowLayout());
		jp3.add(passWord);
		jp3.add(pwd);

		JPanel jp5=new JPanel(new FlowLayout());
		jp5.add(sIp);
		jp5.add(serviceIp);

		JPanel jp4=new JPanel(new FlowLayout());
		jp4.add(login);
		jp4.add(register);

		frame.setIconImage(Toolkit.getDefaultToolkit().createImage(Login.class.getResource("5.png")));
		frame.setLayout(new GridLayout(5, 1));//(5,1)
		frame.add(jp1);
		frame.add(jp2);
		frame.add(jp3);
		frame.add(jp5);
		frame.add(jp4);
		frame.setVisible(true);
		frame.setSize(400,300);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public static void main(String[] args) {
		new Login();
	}
}
