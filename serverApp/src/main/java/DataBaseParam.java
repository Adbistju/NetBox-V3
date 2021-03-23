import java.sql.*;
public class DataBaseParam {
    private static String port = "3306";
    private static String address = "localhost";
    private static String baseName= "chatusers";

    private static String user = "root";
    private static String password = "root";

    private static String tableName = "mailuserpasswordauth";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://"+address+":"+port+"/"+baseName+"",
                    user,
                    password
            );
        } catch (SQLException throwables) {
            throw new RuntimeException("failed to establish connection", throwables);
        }
    }

    public static void close(Connection connection) {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throw new RuntimeException("could not close the connection", throwables);
        }
    }

    public static boolean doAuth(String email, String password) {
        Connection connection = getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM "+tableName+" WHERE email = ? AND password = ?"
            );
            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                rs.close();
                return true;
            }
            rs.close();
            return false;
        } catch (SQLException throwables) {
            throw new RuntimeException("SWW during user fetch", throwables);
        }
    }

    public static void doRegist(String email, String password, String nickName) {
        Connection connection = getConnection();
        try {
            Statement st = connection.createStatement();
            st.executeUpdate(
                    "INSERT INTO `"+tableName+"` (`nickname`, `email`, `password`) VALUES ('"+nickName+"', '"+email+"', '"+password+"')"
            );

        } catch (SQLException throwables) {
            throw new RuntimeException("SWW during user fetch", throwables);
        }
    }

    public static void setPort(String port) {
        DataBaseParam.port = port;
    }

    public static void setAddress(String address) {
        DataBaseParam.address = address;
    }

    public static void setBaseName(String baseName) {
        DataBaseParam.baseName = baseName;
    }

    public static void setUser(String user) {
        DataBaseParam.user = user;
    }

    public static void setPassword(String password) {
        DataBaseParam.password = password;
    }

    public static void setTableName(String tableName) {
        DataBaseParam.tableName = tableName;
    }
}
