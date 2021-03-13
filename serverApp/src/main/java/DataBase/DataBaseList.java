package DataBase;

import java.util.ArrayList;

public class DataBaseList {
    private static final ArrayList<User> userList = new ArrayList<>();

    public static void getUserList() {
        userList.add(new User("1", "1mail", "1"));
        userList.add(new User("2", "2mail", "2"));
        userList.add(new User("3", "3mail", "3"));
    }

    public static void addUSer(String name, String email, String password){
        userList.add(new User(name, email, password));
    }

    public static boolean searchName(String name, String password){
        for (int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getName().equals(name) && userList.get(i).getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    public static String getName(String name){
        for (int i = 0; i < userList.size(); i++) {
            if(userList.get(i).getName().equals(name)){
                return userList.get(i).getEmail();
            }
        }
        return "root";
    }


}