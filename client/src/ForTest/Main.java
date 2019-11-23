package ForTest;

import Client.Client;

public class Main {
    public static void main(String[] args){
        Client client = new Client("127.0.0.1", 8080);
        client.Login("t1e1s1t123@yandex.ru", "Test123");
        client.SendEmail(new String[] {"voice1081@gmail.com"}, "Привет", "Дэнчик", new String[] {"C:\\smtp\\honklhonk.jpg"});
        client.Quit();
    }
}
