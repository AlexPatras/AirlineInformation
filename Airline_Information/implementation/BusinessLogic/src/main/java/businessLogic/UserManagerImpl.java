/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package businessLogic;

import dataRecords.UserData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistence.UserDataDAO;
import persistence.UserDataDAOImpl;

public class UserManagerImpl implements UserManager{

    private final UserDataDAO userDataDAO;

    public UserManagerImpl() {
        userDataDAO = new UserDataDAOImpl();
    }
    
    @Override
    public UserData createUser(String lastName,String firstName,String role,String password) {
        try {
            return userDataDAO.createUser(lastName,firstName,role,password);
        } catch (SQLException ex) {
            Logger.getLogger(UserManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void updateUser(UserData user) {
        try {
            userDataDAO.updateUser(user);
        } catch (SQLException ex) {
            Logger.getLogger(UserManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void removeUser(UserData user) {
        try {
            userDataDAO.removeUser(user);
        } catch (SQLException ex) {
            Logger.getLogger(UserManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<UserData> getAllUsers() {
        try {
            return userDataDAO.getAllUsers();
        } catch (SQLException ex) {
            Logger.getLogger(UserManagerImpl.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<>();
        }
    }
}
