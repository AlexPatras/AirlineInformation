/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package persistence;

import dataRecords.UserData;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author maxim
 */
public interface UserDataDAO {
    List<UserData> getAllUsers() throws SQLException;
    UserData createUser(String lastName,String firstName,String role,String password) throws SQLException;
    void updateUser(UserData user) throws SQLException;
    void removeUser(UserData user) throws SQLException;
}
