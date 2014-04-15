package ru.deos.scrambler.tcp;

import ru.deos.scrambler.Settings;
import ru.deos.scrambler.arcane.Scrambler;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 4/5/14
 * Time: 12:38 AM
 */
public class TCPClient extends Thread {
    private Socket socket;

    private String address;
    private BufferedInputStream iStream;
    private String fileName;


    public TCPClient(String address, BufferedInputStream iStream, String fileName) {
        this.address = address;
        this.iStream = iStream;
        File file = new File(fileName);
        this.fileName = file.getName();
    }

    @Override
    public void run() {
        try {
            socket = new Socket();
            InetAddress hostAddress = InetAddress.getByName(address);
            SocketAddress host = new InetSocketAddress(hostAddress, Settings.TCP_SERVER_PORT);

            socket.connect(host);
        } catch (UnknownHostException e) {
            System.out.println("Wrong address!");
            return;
        } catch (IOException e) {
            System.out.println("Can't connect to receiver!");
        }

        BufferedOutputStream oStream = null;

        try {
            oStream = new BufferedOutputStream(socket.getOutputStream());
            byte[] buffer = new byte[Settings.BUFFER_SIZE];
            int count;

            Scrambler scrambler = new Scrambler(
                Settings.SCRAMBLER,
                Settings.KEY,
                Settings.LENGTH
            );

            byte[] byteFileName = fileName.getBytes();

            oStream.write(ByteBuffer.allocate(4).putInt(byteFileName.length).array(), 0, 4);
            oStream.write(byteFileName, 0, byteFileName.length);

            while((count = iStream.read(buffer)) > 0) {
                byte[] buffer2 = scrambler.xorByteArrays(buffer, scrambler.getNextScrambledBytes(Settings.BUFFER_SIZE));

                oStream.write(buffer2, 0, count);
            }

            oStream.flush();
        } catch (IOException e) {
            return;
        } finally {
            try {
                socket.close();
                if(iStream != null)
                    iStream.close();
                if(oStream != null)
                    oStream.close();
            } catch (IOException e) {
                System.out.println("Can't close socket!");
            }
        }
    }
}
