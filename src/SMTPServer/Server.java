package SMTPServer;

import ThreadDispatcher.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private final ServerSocket server;
    final ThreadDispatcher dispatcher;

    public Server(int port) {
        dispatcher = ThreadDispatcher.getInstance();
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private void killConnections(){
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
        ArrayList<Threaded> activeConnections = dispatcher.monitor.getThreads();
        if (activeConnections.size() > 1){
            for (Threaded th: activeConnections){
                if(th instanceof SMTPWorker){
                    SMTPWorker connection = (SMTPWorker)th;
                    connection.setQuitTrue();
                }
            }
        }
    }

    public void run() {
        try {
            while (true) {
                Socket client = server.accept();
                SMTPWorker wb = new SMTPWorker(client);
                dispatcher.Add(wb);
            }
        } catch (IOException e) {
            killConnections();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
