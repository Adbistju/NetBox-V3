//import DataBase.DataBaseList;
import DataBase.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Запуск сервера.");
        System.out.println("Введите номер порта для активации сервера");
        DataBaseParam.setPort("3306");
        System.out.println("Введите адрес базы данных");
        DataBaseParam.setAddress("localhost");
        System.out.println("Логин, пароль бд");
        DataBaseParam.setPassword("root");
        DataBaseParam.setUser("root");
        System.out.println("Имя бд");
        DataBaseParam.setBaseName("chatusers");
        System.out.println("Таблица бд");
        DataBaseParam.setTableName("mailuserpasswordauth");

        //statement.execute("INSERT INTO `mailuserpasswordauth` (`nickname`, `email`, `password`) VALUES ('TEST@MAIL', 'PASS@TEST', 'NAME@TEST')");
       // System.out.println(DataBaseParam.getConnection());

        //DataBaseList.getUserList();
        NetWorkSerwer netWorkSerwer = new NetWorkSerwer(8189);
        netWorkSerwer.run();
    }
}