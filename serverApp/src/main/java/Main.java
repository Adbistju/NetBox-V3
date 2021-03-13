import DataBase.DataBaseList;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Start");
        DataBaseList.getUserList();
        NetWorkSerwer netWorkSerwer = new NetWorkSerwer(8189);
        netWorkSerwer.run();
    }
}