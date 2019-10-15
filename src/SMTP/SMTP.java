package SMTP;


import com.sun.xml.internal.org.jvnet.staxex.Base64EncoderStream;
import sun.misc.BASE64Encoder;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class SMTP {
    private SSLSocket sock;
    private BufferedReader br;
    private BufferedOutputStream bos;
    private Base64.Encoder encoder;

    private SMTP() throws Exception {
        encoder = Base64.getEncoder();
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
                .getDefault();
        try {
            sock = (SSLSocket) sslsocketfactory.createSocket(
                    "smtp.yandex.ru", 587);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br = new BufferedReader(new
                    InputStreamReader(sock.getInputStream()));
            bos = new BufferedOutputStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = ReceiveData();
        if (data.split(" ")[0] != "220")
            throw new Exception("Can't start tls or server doesn't support the secure connection");
    }

    private String SendData(byte[] data) throws IOException {
        bos.write(data);
        bos.write("\r\n".getBytes());
        return ReceiveData();
    }

    private String ReceiveData() throws IOException {
        StringBuilder sb = new StringBuilder();
        String rdata;
        while ((rdata = br.readLine()) != null) {
            sb.append(rdata);
        }
        return sb.toString();
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
