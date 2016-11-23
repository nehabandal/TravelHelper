package cs601.travelHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by npbandal on 11/22/16.
 */
public class InsertExisting {


    private static final DatabaseHandler dbhandler = DatabaseHandler.getInstance();
    private static final String SELECT_EXISTING = "select usern,pass from logindup";
    public static DatabaseConnector db;

    /**
     * Class and method to add existing users
     */
    public void setSelectExisting() {


        try {
            db = new DatabaseConnector("database.properties");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }


        try {
            Connection connection = db.getConnection();
            PreparedStatement stmt = connection.prepareStatement(SELECT_EXISTING);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("usern");
                String password = rs.getString("pass");
                Status status = dbhandler.registerUser(username, password);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
