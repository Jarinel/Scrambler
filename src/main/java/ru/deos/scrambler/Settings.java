package ru.deos.scrambler;

import org.apache.commons.lang3.ArrayUtils;
import ru.deos.scrambler.arcane.Scrambler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 4/5/14
 * Time: 12:40 AM
 */
public class Settings {
    public static final String propertiesFile = "./config.properties";

    public static Integer TCP_SERVER_PORT   = 6715;
    public static Integer TCP_CLIENT_PORT   = 6716;

    public static Integer BUFFER_SIZE       = 2048;

    public static byte[] SCRAMBLER;
    public static byte[] KEY;
    public static int LENGTH                = 0;

    public static boolean LOG_SCRAMBLER_KEY = false;
    public static String DEFAULT_FILE       = "config.properties";
    public static String DEFAULT_HOST       = "localhost";

    static {
        reloadConfigs();
    }

    public static void reloadConfigs() {
        Properties properties = new Properties();
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(propertiesFile);
            properties.load(inputStream);

            TCP_SERVER_PORT = Integer.parseInt(properties.getProperty("TCP_SERVER_PORT"));
            TCP_CLIENT_PORT = Integer.parseInt(properties.getProperty("TCP_CLIENT_PORT"));

            BUFFER_SIZE = Integer.parseInt(properties.getProperty("BUFFER_SIZE"));

            LOG_SCRAMBLER_KEY = Boolean.parseBoolean(properties.getProperty("LOG_SCRAMBLER_KEY"));

            DEFAULT_FILE = properties.getProperty("DEFAULT_FILE");
            DEFAULT_HOST = properties.getProperty("DEFAULT_HOST");

            String scrambler = properties.getProperty("SCRAMBLER");
            String key       = properties.getProperty("KEY");

            SCRAMBLER   = stringToByteArray(scrambler);
            KEY         = stringToByteArray(key);
            LENGTH      = scrambler.length();
        } catch (FileNotFoundException e) {
            System.out.println("Can't open the file.");
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Properties error.");
            System.exit(-1);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] stringToByteArray(String source) {
        if(!source.matches("[01]+")) {
            return new byte[]{0};
        }

        int byteSize = (source.length() / 8) + (source.length() % 8 > 0 ? 1 : 0);
        byte[] result = new byte[byteSize];
        byte[] bin = new BigInteger(source, 2).toByteArray();
        ArrayUtils.reverse(bin);

        for(int i = 0; (i < bin.length)&&(i < result.length); i++) {
            result[i] = bin[i];
        }

        return result;
    }
}
