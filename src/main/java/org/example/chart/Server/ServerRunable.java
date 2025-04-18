package org.example.chart.Server;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;

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
    //客户端对应的SM4密钥
    SecretKey decryptedKey;
    public ServerRunable(Socket socket, ArrayList<Socket> sockets, SecretKey decryptedKey) {
        this.socket = socket;
        this.sockets = sockets;
        this.decryptedKey = decryptedKey;
    }
    @Override
    public void run() {
        //接收客户端发送的消息，并反馈
        while (true) {
            try {
                // 1. 接收数据
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                String payload = br.readLine();
                // 2. 分割IV和加密数据
                String[] parts = payload.split("\\|");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("数据错误");
                }
                byte[] iv = Base64.getDecoder().decode(parts[0]);
                byte[] encryptedData = Base64.getDecoder().decode(parts[1]);

                String base64iv = Base64.getEncoder().encodeToString(iv);
                String base64Encoded = Base64.getEncoder().encodeToString(encryptedData);
                System.out.println("服务端收到的加密iv:"+ base64iv);
                System.out.println("服务端收到的加密字符串:"+ base64Encoded);
                // 3. 初始化解密器
                Cipher cipher = Cipher.getInstance("SM4", "BC");
                cipher.init(Cipher.DECRYPT_MODE, decryptedKey, new IvParameterSpec(iv));
                // 4. 解密数据
                byte[] decryptedData = cipher.doFinal(encryptedData);
                String str= new String(decryptedData, StandardCharsets.UTF_8);
                System.out.println("服务端收到的消息-解密后："+str);
                System.out.println("========================================================");
                for (Socket socket1 : sockets) {//反馈给每一个客户端
                    BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(socket1.getOutputStream()));
                    bw.write(str);
                    bw.newLine();
                    bw.flush();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
