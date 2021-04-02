public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Запуск сервера.");
        NetWorkServer netWorkServer = new NetWorkServer(8189);
        netWorkServer.run();
        System.out.println("Cервера запущен.");

    }
}