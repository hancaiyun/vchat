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
		IP="192.168.138.31";//������IP
		Port=5001;//����˿ں�
		pw=null;
		mes=null; 
		isConnected=false;
		frame=new JFrame("VChat-1.6.1");
		userName=new JLabel("�û���");
		passWord=new JLabel(" �� ��   ");
		pwd=new JPasswordField(20);
		pwd.setText("123");//��ȥ
		name=new JTextField(20);
		name.setText("COCKY");//��ȥ
		login=new JButton("��½");
		register=new JButton("ע��");
		
		sIp=new JLabel("������IP");
		serviceIp=new JTextField(20);
		init();
		//������������Enter�����õ�ͬ�ڡ���½����ť
		serviceIp.addKeyListener(new KeyAdapter()  {	//���̼���ENTER
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER){//ͬʱ��SHIFT+ENTER������Ϣ //
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
		//TODO ������Ʋ������ظ����ӣ���Ϊ��½�ɹ�����Ѿ���ת�Ի�����
		if (isConnected) {  
            JOptionPane.showMessageDialog(null, "�Ѵ���������״̬����Ҫ�ظ�����!",  
                    "����", JOptionPane.ERROR_MESSAGE);  
            return;
        } 
		p1=new String(name.getText());
		p2=new String(pwd.getPassword());
		//�ȼ���û������������Ƿ�Ϊ��
		if((p1.equals(""))||(p2.equals(""))){
			JOptionPane.showMessageDialog(null, "�û����������벻��Ϊ��","����", JOptionPane.ERROR_MESSAGE);
		}
//		else if(serviceIp.getText().equals("")){
//			JOptionPane.showMessageDialog(null, "����д��ȷ�ķ�����IP","����", JOptionPane.ERROR_MESSAGE);}
        else{
		//�����ݿ�鿴�û����������Ƿ���ȷ
			try {
				//IP=new String(serviceIp.getText());
				socket1=new Socket(IP,Port);
				//����NAME��PASSWORD
				OutputStream out=socket1.getOutputStream();
				OutputStreamWriter osw=new OutputStreamWriter(out,"UTF-8");
				pw=new PrintWriter(osw,true);
				pw.println(p1+";"+p2);
				//���շ�����Ϣ
				BufferedReader br=new BufferedReader(new InputStreamReader(socket1.getInputStream(),"UTF-8"));
				while((mes=br.readLine())!="OK"){
					if(mes.equals("wrong name")){
						JOptionPane.showMessageDialog(null, "�û��������ڣ�","����", JOptionPane.ERROR_MESSAGE);
						name.setText(null);
						pwd.setText(null);
						return;
					}else if(mes.equals("wrong password")){
						JOptionPane.showMessageDialog(null, "�������","����", JOptionPane.ERROR_MESSAGE);
						pwd.setText(null);
						return;
					}else{		
						try{	
							socket=new Socket(IP,5000);
							isConnected=true;
							frame.dispose();
							//�������Ӻ�ر�Login���ڿ���Chat���ڿ�ʼ�Ի�
							cf=new ChatFrame(socket,name.getText(),IP);
							return;//���������¼�������ǰ����
						}catch(Exception e1){
							JOptionPane.showMessageDialog(null, "�޷����ӷ���������½ʧ��");
							return;
						}	
					}
				 }
			 } catch (Exception e2) {
				e2.printStackTrace();
				System.out.println("����ʧ�ܣ�");
				JOptionPane.showMessageDialog(null, "�޷����ӷ�����!");
			}
	}
	}
	private void addEvent() {
		//��¼��ť�����¼�
		login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    login();
			}           
		});
		//ע�ᰴť�¼�
		register.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(serviceIp.getText().equals("")){
					JOptionPane.showMessageDialog(null, "����д������IP","����", JOptionPane.ERROR_MESSAGE);
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
