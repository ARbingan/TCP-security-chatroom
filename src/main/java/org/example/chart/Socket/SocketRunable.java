package org.example.chart.Socket;

import org.example.chart.JFrame.SocketJFrame;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
class SocketRunable implements Runnable {
    Socket socket;
    SecretKey sm4secretKey;
    String name;
    String receiver;

    public SocketRunable(Socket socket, SecretKey sm4secretKey, String name, String receiver) {
        this.socket = socket;
        this.sm4secretKey = sm4secretKey;
        this.name = name;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        // String name=Thread.currentThread().getName();
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                String payload = br.readLine();

                //分割IV和加密数据
                String[] parts = payload.split("\\|");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("数据错误");
                }
                byte[] iv = Base64.getDecoder().decode(parts[0]);
                byte[] message = Base64.getDecoder().decode(parts[1]);

                String base64iv = Base64.getEncoder().encodeToString(iv);
                String base64message = Base64.getEncoder().encodeToString(message);

                System.out.println("接收方收到的加密iv:" + base64iv);
                System.out.println("接收方收到的加密base64message:" + base64message);
                String encodedKey = Base64.getEncoder().encodeToString(sm4secretKey.getEncoded());
                System.out.println("接收方解密使用的SM4:" + encodedKey);
                //初始化解密器
                Cipher cipher = Cipher.getInstance("SM4", "BC");
                cipher.init(Cipher.DECRYPT_MODE, sm4secretKey, new IvParameterSpec(iv));
                //解密数据
                byte[] messageData = cipher.doFinal(message); // 直接对message字节数组进行解密
                String messagestr = new String(messageData, StandardCharsets.UTF_8);
                System.out.println("接收方收到的消息-解密后：" + messagestr);
                System.out.println("========================================================");

                //将反馈的信息打印到文本框中
                SocketJFrame.jTextArea.append(messagestr + "\r\n");
                // System.out.println(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}