package SMTPServer;

import SMTP.SMTP;
import SMTP.SMTPException;
import SMTPServer.exception.DefectiveSentException;
import Serializator.*;
import ThreadDispatcher.Threaded;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

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
        try {
            smtp = new SMTP();
        } catch (SMTPException e) {
            setQuitTrue();
        }
        clientPacketSerializator = new Serializator<ClientPacket>();
        clientPacketSerializator.register(new IntSerializator());
        clientPacketSerializator.register(new StringSerializator());
        serverPacketSerializator = new Serializator<ServerPacket>();
        serverPacketSerializator.register(new IntSerializator());
        serverPacketSerializator.register(new StringSerializator());
        serverPacketSerializator.register(new BooleanSerializator());
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
        ServerPacket serverPacket = new ServerPacket();
        serverPacket.exception = false;
        if(clientPacket.type.equals("Login")) {
            try{
                smtp.Login(clientPacket.login, clientPacket.password);
            }
            catch (SMTPException e){
                serverPacket.exception = true;
                serverPacket.message = e.getMessage();
            }
        }
        else if (clientPacket.type.equals("Letter")){
            Tuple<String, byte[]>[] attachments  = null;
            try {
                if (clientPacket.attachmentsCount != 0) attachments = getAttachments(clientPacket.attachmentsCount);
            } catch (DefectiveSentException e){
                serverPacket.exception = true;
                serverPacket.message = e.getMessage();
            }
            if(!serverPacket.exception) {
                try {
                    smtp.send_email(clientPacket.sender, clientPacket.receivers.split(", "), clientPacket.theme, clientPacket.letter, attachments);
                } catch (SMTPException e) {
                    serverPacket.exception = true;
                    serverPacket.message = e.getMessage();
                }
            }
        }
        else if(clientPacket.type.equals("Quit")) setQuitTrue();
        return serverPacket;
    }

    private Tuple<String, byte[]>[] getAttachments(int attachmentsCount) throws DefectiveSentException{
        Tuple<String, byte[]>[] attachments = new Tuple[attachmentsCount];
        for(int i = 0; i < attachmentsCount; i++){
            String name = null;
            int len = 0;
            Tuple<String, byte[]> file = null;
            try {
                len = in.readInt();
                byte[] att = new byte[len];
                in.readFully(att);
                name = in.readUTF();
                if(Arrays.hashCode(att) != in.readInt()){
                    throw new DefectiveSentException("Хэшсуммы отправленных и полученных вложений не совпадают");
                }
                file = new Tuple<String, byte[]>(name, att);
                attachments[i] = file;
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
                    System.out.println(len);
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
                    System.out.println(serverPacket.exception);
                    if(!quit) {
                        byte[] response = serverPacketSerializator.Serialize(serverPacket);
                        out.writeInt(response.length);
                        out.write(response);
                        out.flush();
                    }
                }
        }
        catch (IOException e) { e.printStackTrace();}
        finally {
            try {
                if(out != null && in != null) {
                    if(!quit) out.flush();
                    out.close();
                    in.close();
                }
                client.close();
                System.out.println("close");
            } catch (IOException e){ e.printStackTrace(); }
        }
    }
}
