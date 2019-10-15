package SMTPServer;

public class ServerPacket {
    public boolean Exception;
    public String Message;

    public ServerPacket(boolean exc, String msg) {
        Exception = exc;
        Message = msg;
    }
}
