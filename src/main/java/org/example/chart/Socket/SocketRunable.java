package org.example.chart.Socket;



import org.example.chart.JFrame.SocketJFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * 客户端
 * 客户端的发送数据要与与聊天界面的信息输入框和发送按钮绑定
 * 客户端需要单独开出一条线程用来接收服务端反馈的信息并打印到聊天界面的文本框中
 * 客户端的作用就是发送数据并接收服务端反馈的信息打印到聊天界面的文本框中
 *
 * 首先我们要接收用户登录时的用户名，与线程名字绑定
 * 然后客户端的发送数据需要与聊天界面的信息输入框和发送按钮绑定，当我们点击发送按钮时我们要发送的数据就会被发送到服务端
 * 其次客户端需要单独开出一条线程用来接收服务端反馈的信息并打印到聊天界面的文本框中
 */
class SocketRunable implements Runnable{
    Socket socket;
    public SocketRunable(Socket socket) {
        this.socket=socket;
    }
    @Override
    public void run() {
        String name=Thread.currentThread().getName();
        while (true){
            try {
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str=br.readLine();
                //将反馈的信息打印到文本框中
                SocketJFrame.jTextArea.append(str+"\r\n");
                System.out.println(str);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

