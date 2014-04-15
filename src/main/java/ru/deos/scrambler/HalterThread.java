package ru.deos.scrambler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 4/5/14
 * Time: 1:34 AM
 */
public class HalterThread extends Thread {
    private ArrayList<Object> sockets = new ArrayList<Object>();

    public void addSocketToClose(Object socket) {
        sockets.add(socket);
    }

    @Override
    public void run() {
        for(Object socket : sockets) {
            if(socket instanceof ServerSocket)
                if(((ServerSocket) socket).isClosed()) {
                    try {
                        ((ServerSocket) socket).close();
                    } catch (IOException e) {
                        System.err.println("Can't stop tcp server socket");
                    }
                    continue;
                }

            if(socket instanceof Socket)
                if(((Socket) socket).isClosed()) {
                    try {
                        ((Socket) socket).close();
                    } catch (IOException e) {
                        System.err.println("Can't stop tcp socket");
                    }
                    continue;
                }

            if(socket instanceof PrintWriter) {
                ((PrintWriter) socket).close();
            }
        }
    }
}
