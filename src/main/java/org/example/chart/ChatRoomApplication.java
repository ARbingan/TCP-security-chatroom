package org.example.chart;


import org.example.chart.Server.ServerRunable;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

@SpringBootApplication

public class ChatRoomApplication {
    //记录连接的客户端
    public static ArrayList<Socket> sockets = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //服务端
        ServerSocket ss = new ServerSocket(9090);
        //来一个客户端就开辟一个线程处理
        while (true) {
            Socket socket = ss.accept();
            sockets.add(socket);
            new Thread(new ServerRunable(socket, sockets)).start();
        }

    }
}
