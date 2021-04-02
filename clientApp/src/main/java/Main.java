import FileManager.FileManager;

import java.util.concurrent.CountDownLatch;

public class Main {
    public static void main(String[] args) {
        CountDownLatch networkStarter = new CountDownLatch(1);
        FileManager fileManagerLocal = new FileManager("Lockall");
        new Thread(() -> Network.getInstance().start(networkStarter, fileManagerLocal)).start();
        ClientControl clientControl = new ClientControl(fileManagerLocal);
        clientControl.start();
        System.out.println("Закрытие сетевого подключения...");
        Network.getInstance().stop();

        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Прекращение работы программы...");
    }
}