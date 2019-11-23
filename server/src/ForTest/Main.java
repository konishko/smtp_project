package ForTest;

import SMTP.SMTP;
import SMTPServer.Server;
import Serializator.Tuple;

import java.io.File;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {
//        try {
//            File testFile = new File("C:\\smtp\\honklhonk.jpg");
//            byte[] file = Files.readAllBytes(testFile.toPath());
//            Tuple<String, byte[]>[] att = new Tuple[1];
//            att[0] = new Tuple<>("test", file);
//            SMTP s = new SMTP();
//            s.Login("t1e1s1t123@yandex.ru", "Test123");
//            s.send_email("t1e1s1t123@yandex.ru", new String[] {"voice1081@gmail.com"}, "Привет", "Дэнчик", att);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Server server = new Server(8080);
        server.run();
    }
}
