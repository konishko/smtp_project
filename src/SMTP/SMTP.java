package SMTP;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;

public class SMTP {
    private SSLSocket sock;
    private Scanner sc;
    private BufferedOutputStream bos;
    private Base64.Encoder encoder;

    public SMTP(){
        encoder = Base64.getEncoder();
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
                .getDefault();
        try {
            sock = (SSLSocket) sslsocketfactory.createSocket(
                    "smtp.yandex.ru", 465);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            sc = new Scanner(new
                    InputStreamReader(sock.getInputStream()));
            sc.useDelimiter("\r\n");
            bos = new BufferedOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ReceiveData();
            SendData("EHLO user".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (data.split(" ")[0] != "220")
//            throw new Exception("Can't start tls or server doesn't support the secure connection");
    }

    private String ReceiveData(){
        StringBuilder sb = new StringBuilder();
        String data;
        while((data = sc.nextLine()).charAt(3) == '-') {
            sb.append(data);
            sb.append('\n');
        }
        sb.append(data);
        sb.append('\n');
        data = sb.toString();
        System.out.println(data);
        return data;
    }
    private String SendData(byte[] data) throws IOException {
        bos.write(data);
        bos.write("\r\n".getBytes());
        bos.flush();
        return ReceiveData();
    }
    public boolean Login(String login, String password){
        String data = null;
        byte[] b64Login = encoder.encode(login.getBytes());
        byte[] b64Password = encoder.encode(password.getBytes());
        try {
           SendData("AUTH LOGIN".getBytes());
           SendData(b64Login);
           data = SendData(b64Password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data == null) return false;
        return (data.split(" ")[0].equals("235"));
    }

    public boolean send_email(String sender, String[] receivers, String theme, String text, ArrayList<byte[]> attachments){
        return true;
    }
}
