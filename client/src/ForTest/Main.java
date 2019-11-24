package ForTest;

import Client.Client;

public class Main {
    public static void main(String[] args){
        Client client = new Client("127.0.0.1", 8080);
        client.login("t1e1s1t123@yandex.ru", "Test123");
        client.sendEmail(new String[] {"voice1081@gmail.com"}, "Костян", "Принимай гавно", new String[] {"C:\\Users\\Данил\\Desktop\\ass.jpg", "C:\\Users\\Данил\\Desktop\\ass.jpg", "C:\\Users\\Данил\\Desktop\\gitelman.jpg", "C:\\Users\\Данил\\Desktop\\gitelman.jpg", "C:\\Users\\Данил\\Desktop\\ass.jpg", "C:\\Users\\Данил\\Desktop\\ass.jpg", "C:\\Users\\Данил\\Desktop\\gitelman.jpg", "C:\\Users\\Данил\\Desktop\\gitelman.jpg"}, true);
        client.quit();
    }
}
