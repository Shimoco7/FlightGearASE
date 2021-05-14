package ptm1;


import ptm1.Commands.DefaultIO;
import ptm1.Server.ClientHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class AnomalyDetectionHandler implements ClientHandler{

    public class SocketIO implements DefaultIO{

        Scanner inFromClient;
        PrintWriter outToClient;

        public SocketIO(InputStream inputStream, OutputStream outputStream) {
            inFromClient = new Scanner(inputStream);
            outToClient = new PrintWriter(outputStream);
        }

        @Override
        public String readText() {
            return inFromClient.nextLine();
        }

        @Override
        public void write(String text) {
            outToClient.print(text);
            outToClient.flush();
        }

        @Override
        public float readVal() {
            return inFromClient.nextFloat();
        }

        @Override
        public void write(float val) {
            outToClient.print(val);
            outToClient.flush();
        }

        public void close() {
            inFromClient.close();
            outToClient.close();
        }

    }

    @Override
    public void communicate(InputStream inputStream, OutputStream outputStream) {
        SocketIO sio = new SocketIO(inputStream,outputStream);
        CLI cli = new CLI(sio);
        cli.start();
        sio.write("bye");
        sio.close();
    }




}