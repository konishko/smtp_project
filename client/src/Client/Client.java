package Client;
import Serializator.*;
import zip.Archiver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;

public class Client {
    private Archiver archiver;
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
        this.archiver = new Archiver();
    }
    public ServerPacket login(String login, String password){
        this.sender = login;
        ClientPacket packet = new ClientPacket();
        ServerPacket response = null;
        packet.type = "Login";
        packet.login = login;
        packet.password = password;
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
        } catch (IOException | DeserializeException e) {
            e.printStackTrace();
        }
        System.out.println(response.exception);
        return response;
    }

    public ServerPacket sendEmail(final String[] receivers, final String theme, final String text){
        return sendEmail(receivers, theme, text, new String[0], false);
    }

    public ServerPacket sendEmail(final String[] receivers, final String theme, final String text,
                                  final String[] attachments, final boolean archiveContent){
        ClientPacket packet = new ClientPacket();
        ServerPacket response = null;
        packet.type = "Letter";
        packet.sender = sender;
        packet.theme = theme;
        packet.letter = text;
        if(archiveContent){
            packet.attachmentsCount = 1;
        } else {
            packet.attachmentsCount = attachments.length;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < receivers.length; i++){
            sb.append(receivers[i]);
            if(i != receivers.length - 1) sb.append(", ");
        }
        packet.receivers = sb.toString();
        byte[] pack = clientPacketSerializator.Serialize(packet);
        try {
            dos.writeInt(pack.length);
            bos.write(pack);
            bos.flush();
            if(archiveContent){
                byte[] archive = archiver.archiveFiles(attachments);
                dos.writeInt(archive.length);
                bos.write(archive);
                bos.flush();
                dos.writeUTF("archive.zip");
                dos.writeInt(Arrays.hashCode(archive));
            } else {
                for (String att: attachments) {
                    File file = new File(att);
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    dos.writeInt(fileBytes.length);
                    bos.write(fileBytes);
                    bos.flush();
                    dos.writeUTF(att);
                    dos.writeInt(Arrays.hashCode(fileBytes));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            int len = in.readInt();
            byte[] resp = new byte[len];
            in.readFully(resp);
            response = serverPacketSerializator.Deserialize(resp);
        } catch (IOException | DeserializeException e) {
            e.printStackTrace();
        }
        return response;
    }

    public void quit(){
        ClientPacket packet = new ClientPacket();
        packet.type = "Quit";
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
