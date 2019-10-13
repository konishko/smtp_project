package SMTPServer;

import ThreadDispatcher.Threaded;
import Serializator.*;
import SMTP.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class SMTPWorker extends Threaded {

    private final Socket client;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean quit;
    private Serializator<ClientPacket> serializator;
    private SMTP smtp;
    void setQuitTrue() {quit = true;}

    public SMTPWorker(Socket client) {
        smtp = new SMTP();
        serializator = new Serializator<ClientPacket>();
        serializator.register(new IntSerializator());
        serializator.register(new StringSerializator());
        this.client = client;
        quit = false;
        try {
            in = new DataInputStream(new BufferedInputStream(client.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
        } catch (IOException e) {
            quit = true;
            out = null;
            in = null;
        }
    }

    private ClientPacket handleRequest(ClientPacket clientPacket){
        if(clientPacket.Type.equals("Login")) smtp.Login(clientPacket.Login, clientPacket.Password);
        else if (clientPacket.Type.equals("Letter")){
            ArrayList<byte[]> attachments = null;
            if(clientPacket.AttachmentsCount  != 0) attachments = getAttachments(clientPacket.AttachmentsCount);
            smtp.send_email(clientPacket.Sender, clientPacket.Receivers.split(", "), clientPacket.Theme, clientPacket.Letter, attachments);
        }
        return null;
    }

    private ArrayList<byte[]> getAttachments(int attachmentsCount){
        ArrayList<byte[]> attachments = new ArrayList<>();
        for(int i = 0; i < attachmentsCount; i++){
            int len = 0;
            try {
                len = in.readInt();
                byte[] att = new byte[len];
                in.readFully(att);
                attachments.add(att);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return attachments;
    }

    @Override
    public void doRun() {
        try {
                while (!quit) {
                    int len = in.readInt();
                    byte[] pack = new byte[len];
                    in.readFully(pack);
                    ClientPacket clientPacket = null;
                    try {
                        clientPacket = (ClientPacket)serializator.Deserialize(pack);
                    } catch (DeserializeException e) {
                        e.printStackTrace();
                        quit = true;
                    }
                }
        }
        catch (IOException e) { e.printStackTrace();}
        finally {
            try {
                if(out != null && in != null) {
                    out.flush();
                    out.close();
                    in.close();
                }
                client.close();
            } catch (IOException e){ e.printStackTrace(); }
        }
    }
}
