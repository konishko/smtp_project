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
    private Serializator<ClientPacket> clientPacketSerializator;
    private Serializator<ServerPacket> serverPacketSerializator;
    private SMTP smtp;
    void setQuitTrue() {quit = true;}

    public SMTPWorker(Socket client) {
        smtp = new SMTP();
        clientPacketSerializator = new Serializator<ClientPacket>();
        clientPacketSerializator.register(new IntSerializator());
        clientPacketSerializator.register(new StringSerializator());
        serverPacketSerializator = new Serializator<ServerPacket>();
        serverPacketSerializator.register(new IntSerializator());
        serverPacketSerializator.register(new StringSerializator());
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

    private ServerPacket handleRequest(ClientPacket clientPacket){
        ServerPacket serverPacket = null;
        String errorMsg = null;
        boolean error = false;
        if(clientPacket.Type.equals("Login")) {
            error = !smtp.Login(clientPacket.Login, clientPacket.Password);
            if(error) errorMsg = "Incorrect login or password";
        }
        else if (clientPacket.Type.equals("Letter")){
            ArrayList<byte[]> attachments = null;
            if(clientPacket.AttachmentsCount  != 0) attachments = getAttachments(clientPacket.AttachmentsCount);
            error = !smtp.send_email(clientPacket.Sender, clientPacket.Receivers.split(", "), clientPacket.Theme, clientPacket.Letter, attachments);
        }
        return new ServerPacket(error, errorMsg);
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
                        clientPacket = (ClientPacket)clientPacketSerializator.Deserialize(pack);
                    } catch (DeserializeException e) {
                        e.printStackTrace();
                        quit = true;
                    }
                    ServerPacket serverPacket = handleRequest(clientPacket);
                    out.write(serverPacketSerializator.Serialize(serverPacket));
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
