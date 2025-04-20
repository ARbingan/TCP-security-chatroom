package org.example.chart.JFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录界面
 * 登录成功后启动的聊天界面将共享一个文本显示框
 * 我们需要将聊天界面的文本显示框在这里初始化，为什么？
 * 因为服务端发送的数据是共享的，因此我们要在登录成功后将共享的文本显示框一同传输给客户端的聊天界面
 */
public class SocketJFrame extends JFrame implements ActionListener {
    public Container container = null;
    JTextField jTextField = new JTextField();// 文本输入框（用户名）
    JTextField receiverTextField = new JTextField();// 新增的文本输入框（密码）
    JButton login = new JButton("登录");
    public static JTextArea jTextArea = new JTextArea();// 聊天界面文本显示框

    public SocketJFrame() {
        // 设置图标
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("TalkWirhMyFriend\\src\\image\\坤坤.jpg"));
        // 初始化界面
        initJFrame();
        // 初始化组件
        initView();
        this.setVisible(true);
    }

    public void initJButton() {
        login.setBounds(100, 140, 80, 30);
        login.addActionListener(e -> {
            try {
                loginAction(e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });// 添加监听事件
        container.add(login);
    }

    public void initView() {
        // 设置文字
        Font codeFont = new Font(null, 1, 14);
        JLabel code = new JLabel("用户名");
        code.setBounds(90, 20, 100, 40);
        code.setFont(codeFont);
        code.setBackground(Color.BLACK);
        container.add(code);

        // 设置用户名输入框
        jTextField.setBounds(90, 50, 150, 30);
        container.add(jTextField);

        // 设置密码标签
        JLabel receiverLabel = new JLabel("接收人");
        receiverLabel.setBounds(90, 80, 100, 40);
        receiverLabel.setFont(codeFont);
        receiverLabel.setBackground(Color.BLACK);
        container.add(receiverLabel);

        // 设置密码输入框
        receiverTextField.setBounds(90, 110, 150, 30);
        container.add(receiverTextField);

        // 按钮
        initJButton();
    }

    public void initJFrame() {
        this.setSize(300, 250);// 设置大小
        this.setTitle("聊天室");// 设置标题
        this.setDefaultCloseOperation(3);// 设置关闭模式
        this.setLocationRelativeTo(null);// 设置位置居中
        this.setAlwaysOnTop(true);// 设置置顶
        container = this.getContentPane();// 获取界面隐藏容器
        container.setLayout(null);// 取消内部默认的坐标
        container.setBackground(Color.WHITE);// 设置背景颜色
    }

    public JScrollPane initJTextArea() {
        // 文本显示器
        jTextArea.setEnabled(false);// 设置为不可编辑
        jTextArea.setLineWrap(true);// 自动换行
        jTextArea.setWrapStyleWord(true);// 单词边界换行
        jTextArea.setFont(new Font(null, 1, 16));
        jTextArea.setBackground(Color.gray);
        // 添加滚动器
        JScrollPane jsp = new JScrollPane(jTextArea);
        jsp.setBounds(30, 30, 500, 300);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return jsp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void loginAction(ActionEvent e) throws Exception {// 登录
        this.setVisible(false);
        // 启动聊天界面
        MainJFrame mj = new MainJFrame(jTextField.getText(), receiverTextField.getText(),jTextArea);
        JScrollPane jsp = initJTextArea();
        mj.container.add(jsp);
    }
}