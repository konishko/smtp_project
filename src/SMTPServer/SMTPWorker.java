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
    private Serializator<Packet> serializator;
    private SMTP smtp;
    void setQuitTrue() {quit = true;}

    public SMTPWorker(Socket client) {
        smtp = new SMTP();
        serializator = new Serializator<Packet>();
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

    private Packet handleRequest(Packet packet){
        if(packet.Type.equals("Login")) smtp.Login(packet.Login, packet.Password);
        else if (packet.Type.equals("Letter")){
            ArrayList<byte[]> attachments = null;
            if(packet.AttachmentsCount  != 0) attachments = getAttachments(packet.AttachmentsCount);
            smtp.send_email(packet.Sender, packet.Receivers.split(", "), packet.Theme, packet.Letter, attachments);
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
                    Packet packet = null;
                    try {
                        packet = (Packet)serializator.Deserialize(pack);
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
