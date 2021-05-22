package model;

import other.Properties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class FGPlayer {
    String ip;
    int port,hertzRate;

    Socket fg;
    BufferedReader in;
    PrintWriter out;

    public FGPlayer(Properties p) {
        ip=p.getIp();
        port=p.getPort();
        hertzRate=p.getHertzRate();
        //try {
            //fg=new Socket(ip,port);
            //in= new BufferedReader(new FileReader();
            //out = new PrintWriter(fg.getOutputStream());
       // } catch (IOException e) {
           // e.printStackTrace();
       // }
        start();

    }

    private void start() {

    }


}
