package org.example.chart.Socket;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;

import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class MySocket {

    String name;//用户名
    public Socket socket;//客户端对象
    public SecretKey sm4secretKey;

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
            byte[] ntrupublicKeyBytes = new byte[length];
            // 读取字节数组
            dis.readFully(ntrupublicKeyBytes);
            // 还原公钥对象
            // 创建 X509EncodedKeySpec 对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(ntrupublicKeyBytes);
            // 获取 KeyFactory 实例，使用 NTRU 算法
            KeyFactory keyFactory = KeyFactory.getInstance("NTRU", "BC");
            // 生成公钥对象
            PublicKey ntrupublicKey = keyFactory.generatePublic(keySpec);
            // 将公钥对象转换为 Base64 编码的字符串
            String ntrupublicKeyBase64 = Base64.getEncoder().encodeToString(ntrupublicKey.getEncoded());
            System.out.println(name + "客户端接收到的公钥: " + ntrupublicKeyBase64);
            System.out.println("========================================================");
            // 创建SM4密钥生成器
            KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", "BC");
            // 初始化密钥生成器（SM4密钥长度固定为128位）
            keyGenerator.init(128);
            // 生成密钥
             sm4secretKey = keyGenerator.generateKey();
            // 获取密钥字节数组
            byte[] SM4keyBytes = sm4secretKey.getEncoded();
            String sm4KeyBase64 = Base64.getEncoder().encodeToString(sm4secretKey.getEncoded());

            // NTRU 加密 SM4 密钥
            Cipher cipher = Cipher.getInstance("NTRU", "BC");
            cipher.init(Cipher.WRAP_MODE, ntrupublicKey);
            byte[] encryptSM4 = cipher.wrap(sm4secretKey);

            String sm4KeyenBase64 = Base64.getEncoder().encodeToString(encryptSM4);
            System.out.println(name + "-客户端的SM4密钥-未加密: " + sm4KeyBase64);
            System.out.println(name + "-客户端的加密后的SM4密钥-加密: " + sm4KeyenBase64);
            System.out.println("========================================================");

            // 5. 发送加密数据到服务端
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            // 先发送数据长度
            dos.writeInt(encryptSM4.length);
            // 再发送加密数据
            dos.write(encryptSM4);
            dos.flush();

            this.socket = socket;
            //单独开出一条线程用来打印
            Thread t = new Thread(new SocketRunable(socket));
            t.setName(name);
            t.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void talk(Socket socket, String str) throws Exception {//与聊天界面的信息输入框和发送按钮绑定
        try {
            // 1. 生成随机IV (Initialization Vector)
            byte[] iv = new byte[16]; // SM4块大小是16字节
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            // 2. 初始化SM4加密器
            Cipher cipher = Cipher.getInstance("SM4", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, sm4secretKey, ivSpec);
            // 3. 加密数据
            byte[] encryptedData = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            // 4. 将IV和加密数据转换为Base64便于文本传输
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            String encryptedDataBase64 = Base64.getEncoder().encodeToString(encryptedData);
            System.out.println(name+"：客户端发送的iv："+ivBase64);
            System.out.println(name+"：客户端发送的加密字符串："+encryptedDataBase64);
            System.out.println(name+"：客户端字符串-未加密："+str);
            System.out.println("========================================================");
            // 5. 构建发送格式: IV + "|" + 加密数据
            String payload = ivBase64 + "|" + encryptedDataBase64;
            // 6. 通过BufferedWriter发送
            BufferedWriter bw = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            bw.write(payload);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
