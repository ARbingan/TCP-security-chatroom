package org.example.chart.JFrame;



import org.example.chart.Socket.MySocket;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 聊天界面
 * 登录成功后就创建客户端对象
 * 发送信息按钮与客户端的发送信息绑定
 */
public class MainJFrame extends JFrame implements ActionListener {
    public Container container=null;
    JTextField news=new JTextField();//信息输入框
    JButton sendsJButton=new JButton("发送");
    String name;//用户名
    JTextArea jTextArea;//文本显示框
    MySocket s;//当登录成功后创建客户端对象
    public MainJFrame(String name,JTextArea jTextArea) {
        this.name=name;
        this.jTextArea=jTextArea;
        s=new MySocket(name);
        initJFrame();
        initView();
        this.setVisible(true);
    }
    public void initView(){
        //信息输入框
        news.setBounds(40,400,200,40);
        container.add(news);
        //发送按钮
        sendsJButton.setBounds(380,400,70,50);
        sendsJButton.addActionListener(this);
        container.add(sendsJButton);
    }
    public void initJFrame() {
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("TalkWirhMyFriend\\src\\image\\坤坤.jpg"));
        this.setSize(600, 500);
        this.setTitle("聊天室");
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo(null);
        this.setAlwaysOnTop(true);
        container=this.getContentPane();
        container.setLayout(null);
        container.setBackground(Color.BLACK);
//        initBackground();//添加背景图片
    }
    public void initBackground(){//添加背景图片
        ImageIcon background=new ImageIcon("JFrame_Game\\Jframe_knowledge\\src\\image\\坤坤.jpg");
        JLabel jLabel=new JLabel(background);
        jLabel.setBounds(-80,0,800,800);
        container.add(jLabel);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        //点击发送，将输入框中的内容发送给服务器,然后服务器反馈
        String str=name+":"+news.getText();
        s.talk(s.socket,str);
        news.setText("");
        System.out.println(str);
    }
}