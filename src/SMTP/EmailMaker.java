package SMTP;

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

    public String MakeEmail(String message, String signature, String laddr, String[] raddr, String subject) {
        String sender = MakeSenderField(signature, laddr);
        String receiver = MakeReceiverField(raddr);
        String subj = MakeSubjectField(subject);
        String other = MakeOtherField(laddr);
        String form = sender + receiver + subj + other;
        return form + encoder.encodeToString(message.getBytes());
    }
}
