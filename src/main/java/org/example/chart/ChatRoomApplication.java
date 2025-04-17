package org.example.chart;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.example.chart.Server.ServerRunable;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Arrays;

@SpringBootApplication

public class ChatRoomApplication {

    //记录连接的客户端
    public static ArrayList<Socket> sockets = new ArrayList<>();

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        //服务端
        ServerSocket ss = new ServerSocket(9090);

        // 注册 Bouncy Castle 提供者
        Security.addProvider(new BouncyCastleProvider());

        KeyPairGenerator kgp = KeyPairGenerator.getInstance("NTRU", "BC");
        kgp.initialize(NTRUParameterSpec.ntruhps2048509);
        KeyPair keyPair = kgp.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        //获取 Base64 编码字节数组
        String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        System.out.println("服务端生成的NTRU私钥：" + privateKeyBase64);
        System.out.println("服务端生成的NTRU公钥：" + publicKeyBase64);
        System.out.println("========================================================");

        //获取 DER 编码字节数组
        System.out.println("服务端生成的NTRU私钥 DER 编码：" + Arrays.toString(privateKey.getEncoded()));
        System.out.println("服务端生成的NTRU公钥 DER 编码：" + Arrays.toString(publicKey.getEncoded()));
        System.out.println("========================================================");


        //来一个客户端就开辟一个线程处理
        while (true) {
            Socket socket = ss.accept();
            // 获取输出流，用于向客户端发送数据
            OutputStream os = socket.getOutputStream();
            // 将公钥转换为字节数组
            byte[] publicKeyBytes = publicKey.getEncoded();
            // 发送字节数组长度
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeInt(publicKeyBytes.length);
            // 发送字节数组
            dos.write(publicKeyBytes);
            sockets.add(socket);
            new Thread(new ServerRunable(socket, sockets)).start();
        }

    }
}
