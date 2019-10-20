package SMTP;

import Serializator.Tuple;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;

public class EmailMaker {
    private Base64.Encoder encoder;
    public EmailMaker(Base64.Encoder encoder){
        this.encoder = encoder;
    }
    private static String MakeSenderField(String sender, String addr){
        return String.format("From: %s <%s>\r\n", sender, addr);
    }

    private static String MakeReceiverField(String[] addr){
        StringBuilder receiver = new StringBuilder();
        if(addr.length == 1) {
            receiver.append("To: ");
            receiver.append(addr[0]);
        }
        else{
            receiver.append("Cc: ");
            receiver.append(addr[0]);
            for(int i = 1; i < addr.length; i++){
                receiver.append(", ");
                receiver.append(addr[i]);
            }
        }
        receiver.append("\r\n");
        return receiver.toString();
    }

    private String MakeSubjectField(String subject){return "Subject: " + subject + "\r\n";}

    private String MakeOtherField(String addr){
        String other = String.format("MIME-Version: 1.0\r\nContent-Transfer-Encoding: base64\r\nContent-Type: text/plain; charset=utf-8\r\nReturn-Path: %s\r\n\r\n", addr);
        return other;
    }

    private String MakeAttachment(byte[] att, String name){
        String contType = getMime(att);
        String beg = "--kwak\r\n";
        String content = String.format("Content-Type: %s; name=\"%s\"\r\n", contType, name);
        String charset = "Content-Transfer-Encoding: base64\r\n";
        String encoded = encoder.encodeToString(att);
        return beg + content + charset + encoded + "\r\n";
    }

    private String getMime(byte[] file){
        String mimeType = null;
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(file));
        try {
            mimeType = URLConnection.guessContentTypeFromStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mimeType;
    }

    public String MakeEmail(String message, String signature, String laddr, String[] raddr, String subject) {
        String sender = MakeSenderField(signature, laddr);
        String receiver = MakeReceiverField(raddr);
        String subj = MakeSubjectField(subject);
        String other = MakeOtherField(laddr);
        String form = sender + receiver + subj + other;
        return form + encoder.encodeToString(message.getBytes());
    }

    public String MakeMultipleEmail(String message, String signature,
                                    String laddr, String[] raddr, Tuple<String, byte[]>[] files, String subject){
        String field = "MIME-Version: 1.0\r\nContent-Type: multipart/mixed; boundary=\"kwak\"\r\n";
        String sender = MakeSenderField(signature, laddr);
        String receiver = MakeReceiverField(raddr);
        String subj = MakeSubjectField(subject);
        String body1 = "\r\n--kwak\r\nContent-Type: text/plain; charset=utf-8\r\nContent-Transfer-Encoding: base64\r\n\r\n";
        String msg = encoder.encodeToString(message.getBytes());
        StringBuilder att = new StringBuilder();
        for(Tuple<String, byte[]> file : files){
            att.append(MakeAttachment(file.y, file.x));
        }
        String end = "--kwak--";
        return  field + sender + receiver + subj + body1 + msg + "\r\n" + att.toString() + end;
    }
}
