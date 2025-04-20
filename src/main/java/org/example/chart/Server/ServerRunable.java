package org.example.chart.Server;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;

/**
 * 服务端
 * 对于每一个来连接的客户端都需要一条线程进行处理
 * 服务端要将信息反馈给每一个客户端
 * 服务端的作用就是用来接受客户端发送的信息并进行反馈
 * 服务端只有一个，需要不断的等待客户端来连接，对于每一个客户端都要单独开出一条线程进行处理。
 * 当ServerSocket进行反馈时，我们需要反馈给每一个客户端，因此我们需要一个集合用来存储已经连接的Socket
 */
public class ServerRunable implements Runnable {
    Socket socket; // 连接处理的Socket
    String name;
    String receiver;
    ArrayList<ServerRunable> ServerRunables;
    // 客户端对应的SM4密钥
    SecretKey decryptedKey;

    public ServerRunable(Socket socket, ArrayList<ServerRunable> ServerRunables, SecretKey decryptedKey, String name, String receiver) {
        this.socket = socket;
        this.ServerRunables = ServerRunables;
        this.decryptedKey = decryptedKey;
        this.name = name;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        Security.addProvider(new BouncyCastleProvider());
        // 接收客户端发送的消息，并反馈
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
                byte[] message = Base64.getDecoder().decode(parts[1]);

                String base64iv = Base64.getEncoder().encodeToString(iv);
                String base64message = Base64.getEncoder().encodeToString(message);

                System.out.println("服务端收到的加密iv:" + base64iv);
                System.out.println("服务端收到的加密base64message:" + base64message);

                String encodedKey = Base64.getEncoder().encodeToString(decryptedKey.getEncoded());
                System.out.println("服务端解密使用的SM4:" + encodedKey);

                Cipher cipher = Cipher.getInstance("SM4", "BC");
                cipher.init(Cipher.DECRYPT_MODE, decryptedKey, new IvParameterSpec(iv));
                // 4. 解密数据
                byte[] messageData = cipher.doFinal(message);
                String messagestr = new String(messageData, StandardCharsets.UTF_8);
                System.out.println("服务端收到的消息-解密后：" + messagestr);
                System.out.println("========================================================");

                for (ServerRunable serverRunable : ServerRunables) {
                    // 反馈给每一个客户端
                    if (Objects.equals(serverRunable.name, receiver)
                            ||Objects.equals(serverRunable.name, name)) {
                        // TODO 加密messagestr，使用接收方的密钥
                        // 1. 生成随机IV (Initialization Vector)
                        IvParameterSpec ivSpec = new IvParameterSpec(iv);

                        Cipher cipher1 = Cipher.getInstance("SM4", "BC");
                        cipher1.init(Cipher.ENCRYPT_MODE, serverRunable.decryptedKey, ivSpec);

                        String encodedKey1 = Base64.getEncoder().encodeToString(serverRunable.decryptedKey.getEncoded());
                        System.out.println("加密转发使用的SM4:" + encodedKey1);

                        byte[] encryptedData = cipher1.doFinal(messagestr.getBytes(StandardCharsets.UTF_8));
                        String ivBase64 = Base64.getEncoder().encodeToString(iv);
                        String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
                        System.out.println(name + "：客户端发送的加密iv：" + ivBase64);
                        System.out.println(name + "：客户端发送的加密字符串：" + encryptedDataBase64);

                        String payload1 = ivBase64 + "|" + encryptedDataBase64;

                        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(serverRunable.socket.getOutputStream(), StandardCharsets.UTF_8));
//                        bw.write(payload1);
//                        new OutputStreamWriter(serverRunable.socket.getOutputStream(), StandardCharsets.UTF_8)
                        bw.write(payload1);
                        bw.newLine();
                        bw.flush();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}