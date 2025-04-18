package org.example.chart;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;
import org.example.chart.Server.ServerRunable;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
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
    public static void main(String[] args) throws Exception {
        //服务端
        ServerSocket ss = new ServerSocket(9090);
        // 注册 Bouncy Castle 提供者
        Security.addProvider(new BouncyCastleProvider());
        //生成ntru密钥对
        KeyPairGenerator kgp = KeyPairGenerator.getInstance("NTRU", "BC");
        kgp.initialize(NTRUParameterSpec.ntruhps2048509);
        KeyPair ntrukeyPair = kgp.generateKeyPair();
        PrivateKey ntruprivateKey = ntrukeyPair.getPrivate();
        PublicKey ntrupublicKey = ntrukeyPair.getPublic();
        //获取 Base64 编码字节数组
        String privateKeyBase64 = Base64.getEncoder().encodeToString(ntruprivateKey.getEncoded());
        String publicKeyBase64 = Base64.getEncoder().encodeToString(ntrupublicKey.getEncoded());
        System.out.println("服务端生成的NTRU私钥：" + privateKeyBase64);
        System.out.println("服务端生成的NTRU公钥：" + publicKeyBase64);
        System.out.println("========================================================");
        //来一个客户端就开辟一个线程处理
        while (true) {
            Socket socket = ss.accept();
            //向客户端发送公钥
            //获取输出流，用于向客户端发送数据
            OutputStream os = socket.getOutputStream();
            //将公钥转换为字节数组
            byte[] ntrupublicKeyBytes = ntrupublicKey.getEncoded();
            //发送字节数组长度
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeInt(ntrupublicKeyBytes.length);
            // 发送字节数组
            dos.write(ntrupublicKeyBytes);

            //接收客户端的SM4密钥
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            // 1. 接收客户端发送的加密数据长度
            int encryptedDataLength = dis.readInt();
            byte[] encryptedData = new byte[encryptedDataLength];
            // 2. 接收加密数据
            dis.readFully(encryptedData);
            // 3. 使用NTRU私钥解密数据
            Cipher cipher = Cipher.getInstance("NTRU", "BC");
            cipher.init(Cipher.UNWRAP_MODE, ntruprivateKey);
            // 解包密钥
            SecretKey decryptedKey = (SecretKey) cipher.unwrap(encryptedData, "SM4", Cipher.SECRET_KEY);

            String encoded = Base64.getEncoder().encodeToString(encryptedData);
            System.out.println("收到客户端的SM4密钥-未解密: " + encoded);
            // 将解包后的密钥转换为字节数组
            byte[] decryptedKeyBytes = decryptedKey.getEncoded();
            String sm4KeyBase64 = Base64.getEncoder().encodeToString(decryptedKeyBytes);
            System.out.println("收到客户端的SM4密钥-解密后: " + sm4KeyBase64);
            System.out.println("========================================================");
            sockets.add(socket);
            new Thread(new ServerRunable(socket, sockets,decryptedKey)).start();
        }
    }
}
