/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistence;

import dataRecords.UserData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class UserDataDAOImpl implements UserDataDAO{

    private final DataSource db;

    public UserDataDAOImpl() {
        db = DBController.getDataSource("db");
    }
    
    @Override
    public List<UserData> getAllUsers() throws SQLException {
        List<UserData> users = new ArrayList<>();
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM users order by userid");
        ResultSet result = statement.executeQuery();
        
        while(result.next()) {
            int id = result.getInt("userid");
            String lastName = result.getString("lastname");
            String firstName = result.getString("firstname");
            String role = result.getString("userrole");
            String password = result.getString("pass_word");
            
            users.add(new UserData(id,lastName,firstName,role,password));
        }
        return users;
    }

    @Override
    public UserData createUser(String lastName,String firstName,String role,String password) throws SQLException {
        int id = getNextFreeID();
        
        PreparedStatement statement = db.getConnection().prepareStatement("insert into users(userid,lastName,firstname,userrole,pass_word)"
                + "values(?,?,?,?,?)");
        statement.setInt(1, id);
        statement.setString(2, lastName);
        statement.setString(3, firstName);
        statement.setString(4, role);
        statement.setString(5, password);
        statement.execute();
        statement.close();
        return new UserData(id, lastName, firstName, role, password);
    }

    @Override
    public void updateUser(UserData user) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE users SET lastname = ?,firstname = ?,userrole = ?,pass_word = ? WHERE userid = ?");
        statement.setString(1, user.getLastName());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getRole());
        statement.setString(4, user.getPassword());
        statement.setInt(5, user.getId());
        statement.execute();
    }

    @Override
    public void removeUser(UserData user) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM users WHERE userid = ?");
        statement.setInt(1, user.getId());
        statement.execute();
    }

    private int getNextFreeID() throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT max(userid) as userid FROM users");
        ResultSet result = statement.executeQuery();
        
        while(result.next()) {
            return result.getInt("userid")+1;
        }
        
        return 0;
    }
}
