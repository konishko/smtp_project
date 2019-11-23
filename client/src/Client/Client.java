package Client;
import Serializator.*;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;

public class Client {
    private Socket client;
    private DataInputStream in;
    private DataOutputStream dos;
    private BufferedOutputStream bos;
    private Serializator<ClientPacket> clientPacketSerializator;
    private Serializator<ServerPacket> serverPacketSerializator;
    private String sender;

    public Client(String serverIP, int port){
        try {
            client = new Socket(serverIP, port);
            bos = new BufferedOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientPacketSerializator = new Serializator<ClientPacket>();
        clientPacketSerializator.register(new IntSerializator());
        clientPacketSerializator.register(new StringSerializator());
        serverPacketSerializator = new Serializator<ServerPacket>();
        serverPacketSerializator.register(new IntSerializator());
        serverPacketSerializator.register(new StringSerializator());
        serverPacketSerializator.register(new BooleanSerializator());
    }
    public ServerPacket Login(String login, String password){
        this.sender = login;
        ClientPacket packet = new ClientPacket();
        ServerPacket response = null;
        packet.Type = "Login";
        packet.Login = login;
        packet.Password = password;
        byte[] pack = clientPacketSerializator.Serialize(packet);
        try {
            System.out.println(pack.length);
            dos.writeInt(pack.length);
            bos.write(pack);
            bos.flush();
            int len = in.readInt();
            System.out.println(len);
            byte[] resp = new byte[len];
            in.readFully(resp);
            response = serverPacketSerializator.Deserialize(resp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeserializeException e) {
            e.printStackTrace();
        }
        System.out.println(response.Exception);
        return response;
    }

    public ServerPacket SendEmail(String[] receivers, String theme, String text, String[] attachments){
        ClientPacket packet = new ClientPacket();
        ServerPacket response = null;
        packet.Type = "Letter";
        packet.Sender = sender;
        packet.Theme = theme;
        packet.Letter = text;
        packet.AttachmentsCount = attachments.length;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < receivers.length; i++){
            sb.append(receivers[i]);
            if(i != receivers.length - 1) sb.append(", ");
        }
        packet.Receivers = sb.toString();
        byte[] pack = clientPacketSerializator.Serialize(packet);
        try {
            dos.writeInt(pack.length);
            bos.write(pack);
            bos.flush();
            for(int i = 0; i < attachments.length; i++){
                File file = new File(attachments[i]);
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                dos.writeInt(fileBytes.length);
                bos.write(fileBytes);
                bos.flush();
                dos.writeUTF(attachments[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int len = in.readInt();
            byte[] resp = new byte[len];
            in.readFully(resp);
            response = serverPacketSerializator.Deserialize(resp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DeserializeException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void Quit(){
        ClientPacket packet = new ClientPacket();
        packet.Type = "Quit";
        byte[] pack = clientPacketSerializator.Serialize(packet);
        try {
            System.out.println(pack.length);
            dos.writeInt(pack.length);
            bos.write(pack);
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
                bos.close();
                dos.close();
                client.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
