package org.example.chart.Socket;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class MySocket {
    String name;//用户名
    public Socket socket;//客户端对象
    public MySocket(String name){
        //客户端
        this.name=name;
        Socket socket=null;
        try {
            socket = new Socket("127.0.0.1",9090);
            this.socket=socket;
            //单独开出一条线程用来打印
            Thread t=new Thread(new SocketRunable(socket));
            t.setName(name);
            t.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void talk(Socket socket,String str) {//与聊天界面的信息输入框和发送按钮绑定
        try {
            BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(str);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
