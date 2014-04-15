package ru.deos.scrambler.tcp;

import com.sun.org.apache.xpath.internal.SourceTree;
import ru.deos.scrambler.Settings;
import ru.deos.scrambler.arcane.Scrambler;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 4/5/14
 * Time: 1:17 AM
 */
public class Client extends Thread {
    Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedInputStream iStream = null;
        try {
            iStream = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Can't read from socket");
            return;
        }

        byte[] buffer = new byte[Settings.BUFFER_SIZE];
        int count;

//        Scrambler scrambler = new Scrambler(
//                new byte[] {0b0100_0000, 0b0000_0000, 0b0101_0010, 0b0000_0011},
//                new byte[] {0b0011_0111, 0b0101_0011, 0b0000_1110, 0b0111_0011},
//                28
//        );

        Scrambler scrambler = new Scrambler(
                Settings.SCRAMBLER,
                Settings.KEY,
                Settings.LENGTH
        );

        BufferedOutputStream oStream = null;

        try {
            byte[] intBuffer = new byte[4];
            iStream.read(intBuffer);

            Integer stringLength = ByteBuffer.wrap(intBuffer).getInt();
            intBuffer = new byte[stringLength];
            iStream.read(intBuffer);

            String filename = new String(intBuffer);
            oStream = new BufferedOutputStream(new FileOutputStream(filename));

            while((count = iStream.read(buffer)) > 0) {
                byte[] buffer2 = scrambler.xorByteArrays(buffer, scrambler.getNextScrambledBytes(Settings.BUFFER_SIZE));

//                for(int i = 0; i < count; i++) {
//                    System.out.print(Integer.toBinaryString(buffer[i]));
//                    System.out.print(" <=> ");
//                    System.out.println(Integer.toBinaryString(buffer2[i]));
//                }

                oStream.write(buffer2, 0, count);
            }

            System.out.println("Received file: " + filename);

            oStream.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
