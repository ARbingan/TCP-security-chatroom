package org.example.chart.Socket;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class MySocket {

    String name;//用户名
    public Socket socket;//客户端对象

    public MySocket(String name) throws Exception {
        //客户端
        this.name = name;
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 9090);
            //接收公钥
            // 注册 Bouncy Castle 提供者
            Security.addProvider(new BouncyCastleProvider());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            // 读取字节数组长度
            int length = dis.readInt();
            byte[] publicKeyBytes = new byte[length];
            // 读取字节数组
            dis.readFully(publicKeyBytes);
            // 还原公钥对象
            // 创建 X509EncodedKeySpec 对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            // 获取 KeyFactory 实例，使用 NTRU 算法
            KeyFactory keyFactory = KeyFactory.getInstance("NTRU", "BC");
            // 生成公钥对象
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            // 将公钥对象转换为 Base64 编码的字符串
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            System.out.println(name + "客户端接收到的公钥: " + publicKeyBase64);
            System.out.println("========================================================");
            this.socket = socket;
            //单独开出一条线程用来打印
            Thread t = new Thread(new SocketRunable(socket));
            t.setName(name);
            t.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void talk(Socket socket, String str) {//与聊天界面的信息输入框和发送按钮绑定
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bw.write(str);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
