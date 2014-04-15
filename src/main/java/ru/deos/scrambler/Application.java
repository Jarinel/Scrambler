package ru.deos.scrambler;

import ru.deos.scrambler.tcp.TCPClient;
import ru.deos.scrambler.tcp.TCPServer;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 3/15/14
 * Time: 3:33 PM
 */
public class Application implements Runnable {

    @Override
    public void run() {
        try {
            startListening();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void startListening() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        TCPServer server = new TCPServer();
        server.start();

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.toLowerCase();

            if(line.startsWith("send"))
                doSend(line);

            if(line.equalsIgnoreCase("reload"))
                Settings.reloadConfigs();

            if(line.equalsIgnoreCase("test"))
                doSend("send test.txt 172.20.10.2");


            if(line.equalsIgnoreCase("halt"))
                System.exit(0);
            if(line.equalsIgnoreCase("exit"))
                System.exit(0);
            if(line.equalsIgnoreCase("stop"))
                System.exit(0);
            if(line.equalsIgnoreCase("close"))
                System.exit(0);
        }
    }

    private void doSend(String line) {
        String[] args = line.split("\\s+");

        if(args.length == 1) {
            args = new String[] {args[0], Settings.DEFAULT_FILE, Settings.DEFAULT_HOST};
        }

        if(args.length < 3) {
            System.out.println("Syntax: send %file_path %host_address");
            return;
        }

        BufferedInputStream iStream;
        try {
            iStream = new BufferedInputStream(new FileInputStream(args[1]));
        } catch (FileNotFoundException e) {
            System.out.println("Can't open specified file!");
            return;
        }

        TCPClient tcpClient = new TCPClient(args[2], iStream, args[1]);
        tcpClient.start();
    }
}
