package SMTP;

import Serializator.Tuple;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.Scanner;

public class SMTP {
    private SSLSocket sock;
    private Scanner sc;
    private BufferedOutputStream bos;
    private Base64.Encoder encoder;
    private EmailMaker em;

    public SMTP() throws SMTPException {
        String data = null;
        encoder = Base64.getEncoder();
        em = new EmailMaker(encoder);
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
            data = ReceiveData();
            SendData("EHLO user");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!data.split(" ")[0].equals("220"))
            throw new SMTPException("Can't start tls or server doesn't support the secure connection");
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

    private String SendData(String data) throws IOException {
        System.out.println(data);
        return SendData(data.getBytes());
    }
    public void Login(String login, String password) throws SMTPException {
        String data = null;
        byte[] b64Login = encoder.encode(login.getBytes());
        byte[] b64Password = encoder.encode(password.getBytes());
        try {
           SendData("AUTH LOGIN");
           SendData(b64Login);
           data = SendData(b64Password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!data.split(" ")[0].equals("235")) throw new SMTPException("Incorrect login or password");
    }

    private void IndicateSender(String sender) throws SMTPException {
        String data = null;
        try {
            data = SendData(String.format("MAIL FROM:<%s>", sender));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!data.split(" ")[0].equals("250"))
        throw new SMTPException("Incorrect name of sender or need a secure connection");
    }

    private void IndicateReceivers(String[] receivers) throws SMTPException {
        for(String rec : receivers){
            String data = null;
            try {
                data = SendData(String.format("RCPT TO:<%s>", rec));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!data.split(" ")[0].equals("250"))
                throw new SMTPException("Incorrect name of receiver");
        }
    }



    public boolean send_email(String sender, String[] receivers, String theme, String text, Tuple<String, byte[]>[] attachments) throws SMTPException {
        IndicateSender(sender);
        IndicateReceivers(receivers);
        String data = null;
        String email = null;
        if(attachments.length == 0) email = em.MakeEmail(text, sender, sender, receivers, theme);
        else email = em.MakeMultipleEmail(text, sender, sender, receivers, attachments, theme);
        try {
            SendData("DATA");
            data = SendData(email + "\r\n.");
            if(!data.split(" ")[0].equals("250"))
                throw new SMTPException("The e-mail wasn't sent");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }
}
