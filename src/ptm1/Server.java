package ptm1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Server {

    public interface ClientHandler{
        void communicate(InputStream inputStream, OutputStream outputStream);
    }

    int clientLimit;
    volatile boolean stop;

    public Server() {
        stop=false;
    }


    private void startServer(int port, ClientHandler ch) {
        if(ch==null)
            return;
        try {
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(1000);
            while(!stop) {
                try {
                    Socket aClient = server.accept();
                    //A client has been connected
                    ch.communicate(aClient.getInputStream(),aClient.getOutputStream());
                    aClient.close();
                }
                catch(SocketTimeoutException e) {}
            }
            server.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    // runs the server in its own thread
    public void start(int port, ClientHandler ch) {
        new Thread(()->startServer(port,ch)).start();
    }

    public void stop() {
        stop=true;
    }
}