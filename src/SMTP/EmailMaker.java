package SMTP;

public class EmailMaker {
    public static String MakeSenderField(String sender, String addr){
        return String.format("From: %s <%s>\r\n", sender, addr);
    }

    public static String MakeReceiverField(String[] addr){
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
}
