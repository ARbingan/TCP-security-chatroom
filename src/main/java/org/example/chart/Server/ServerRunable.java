package org.example.chart.Server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 服务端
 * 对于每一个来连接的客户端都需要一条线程进行处理
 * 服务端要将信息反馈给每一个客户端
 * 服务端的作用就是用来接受客户端发送的信息并进行反馈
 * 服务端只有一个，需要不断的等待客户端来连接，对于每一个客户端都要单独开出一条线程进行处理。
 * 当ServerSocket进行反馈时，我们需要反馈给每一个客户端，因此我们需要一个集合用来存储已经连接的Socket
 */
public class ServerRunable implements Runnable {
    Socket socket;//连接处理的Socket
    ArrayList<Socket> sockets;
    public ServerRunable(Socket socket, ArrayList<Socket> sockets) {
        this.socket = socket;
        this.sockets = sockets;
    }
    @Override
    public void run() {
        //接收客户端发送的消息，并反馈
        while (true) {
            try {
                BufferedReader br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String str=br.readLine();
                System.out.println(str);
                for (Socket socket1 : sockets) {//反馈给每一个客户端
                    BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
                    bw.write(str);
                    bw.newLine();
                    bw.flush();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
