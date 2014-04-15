package ru.deos.scrambler;

import java.io.IOException;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: Deos
 * Date: 3/15/14
 * Time: 3:33 PM
 */
public class Main {
    private static HalterThread halterThread = new HalterThread();

    public static HalterThread getHalterThread() {
        return halterThread;
    }

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(halterThread);

        Application application = new Application();
        application.run();
    }
}
