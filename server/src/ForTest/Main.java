package ForTest;

import SMTPServer.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(8080);
        server.run();
    }
}
