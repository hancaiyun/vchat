package com.cocky.frame;

import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class RegisterFrame {
	private Socket socket;
	private String IP;
	private int Port;
	private JFrame jf;
	private JLabel userName;
	private JTextField  name;
	private JLabel passWord;
	private JLabel passWordConfig;
	private JLabel gender;
	private Checkbox male;
	private Checkbox female;
	private JLabel place;
	private JComboBox loc;
	
	private JLabel title;
	private JPasswordField pwd;
	private JPasswordField pwdcg;
	private String un,pd;
	private JButton hand;
	private JButton back;
	private PrintWriter pw;
	public  RegisterFrame(String ip){
		this.IP=ip;
//    	IP="10.65.2.168";//������IP
//    	IP="119.97.6.248";
    	Port=5002;//����˿ں�
    	title=new JLabel("��ӭ����ע�����");
    	userName=new JLabel(" �����û���        *");
    	name=new JTextField(10);
    	passWord=new JLabel("��������������*");
    	pwd=new JPasswordField(10);
    	passWordConfig=new JLabel("  ��ȷ����������*");
    	gender=new JLabel("�Ա�*");
    	male=new Checkbox(" �� ");
    	female=new Checkbox(" Ů ");
    	
    	place=new JLabel("���� *");
    	String []str={"����","����","�Ϻ�","����","�人","�㽭","��ݸ","����","����","����"};
    	loc=new JComboBox(str);
    	pwdcg=new JPasswordField(10);
    	
    	new JLabel("��֤��*");
    	new JTextField(4);
    	hand=new JButton("�ύ");
    	back=new JButton("����");
    	init();
    	addEvent();
     }
	private void addEvent() {
		hand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String p1=new String(pwd.getPassword());
				String p2=new String(pwdcg.getPassword());
				un=new String(name.getText());
				pd= new String(pwd.getPassword());
				if(un.equals("")){
					JOptionPane.showMessageDialog(jf, "�û�������Ϊ��!");
				}else if(p1.equals("")||p2.equals("")){
					JOptionPane.showMessageDialog(jf, "���벻��Ϊ��!");
				}else if(!p1.equals(p2)){
					JOptionPane.showMessageDialog(jf, "�������벻һ��!");
					pwd.setText(null);
					pwdcg.setText(null);
				}
				else{
	                 //���û����ݴ������ݿ�ķ���	
	                try {
	                	socket=new Socket(IP,Port);
						//����NAME��PASSWORD
						OutputStream out=socket.getOutputStream();
						OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
						pw=new PrintWriter(osw,true);
						pw.println(un+";"+pd);
						//����ע������Ϣ
						BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
						String mes=null;
						while((mes=br.readLine())!=null){
							if(mes.equals("ok")){
							   	 JOptionPane.showMessageDialog(jf, "ע��ɹ�!���ϵ�½��");
							     jf.dispose();
				                 new Login();
							}else{
								 JOptionPane.showMessageDialog(null, "ע��ʧ�ܣ��û����ѱ�ʹ��","����", JOptionPane.ERROR_MESSAGE);
									pwd.setText(null);
									pwdcg.setText(null);
							}
						}
					} catch (UnknownHostException e1) {
						JOptionPane.showMessageDialog(null,"�޷����ӷ�����,�뷵�ص�½ҳ����д��ȷ��IP");
						e1.printStackTrace();
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(null, "ע��ʧ��","����", JOptionPane.ERROR_MESSAGE);
						e2.printStackTrace();
					}
				}
			}
		});
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Login();
				jf.dispose();
					}
			});
	}
	private void init() {
		 jf=new JFrame();
		 
		JPanel jp1=new JPanel(new FlowLayout());
		jp1.add(userName);
		jp1.add(name);

		JPanel jp2=new JPanel(new FlowLayout());
		jp2.add(passWord);
		jp2.add(pwd);

		
		JPanel jp3=new JPanel(new FlowLayout());
		jp3.add(passWordConfig);
		jp3.add(pwdcg);
		
		JPanel jp6=new JPanel(new FlowLayout());
		jp6.add(gender);//
		jp6.add(male);
		jp6.add(female);
		
		JPanel jp7=new JPanel(new FlowLayout());
		jp7.add(place);//
		jp7.add(loc);
				
		JPanel jp4=new JPanel(new FlowLayout());
		jp4.add(hand);
		jp4.add(back);
		JPanel jp5=new JPanel(new FlowLayout());
		jp5.add(title);
		
		jf.setIconImage(Toolkit.getDefaultToolkit().createImage(RegisterFrame.class.getResource("5.png")));
		jf.setLayout(new GridLayout(7, 1));//�Ű�
		jf.add(jp5);
		jf.add(jp1);
		jf.add(jp2);
		jf.add(jp3);
		jf.add(jp6);
		jf.add(jp7);
		jf.add(jp4);
		
	
		jf.setTitle("VChat-1.6.1ע��ҳ��");
		jf.setVisible(true);
		jf.setSize(350,500);
		jf.setResizable(false);
		jf.setLocationRelativeTo(null);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
	}
	
	//����
}
