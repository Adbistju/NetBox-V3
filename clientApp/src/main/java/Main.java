import FileManager.FileManager;

import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        CountDownLatch networkStarter = new CountDownLatch(1);
        FileManager fileManagerLocal = new FileManager("Lockall");
        new Thread(() -> Network.getInstance().start(networkStarter, fileManagerLocal)).start();
        ClientControl clientControl = new ClientControl(fileManagerLocal);
        clientControl.start();
    }
}