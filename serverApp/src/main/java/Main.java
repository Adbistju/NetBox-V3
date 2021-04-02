import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Запуск сервера.");
        NetWorkSerwer netWorkSerwer = new NetWorkSerwer(8189);
        netWorkSerwer.run();
        System.out.println("Cервера запущен.");

    }
}