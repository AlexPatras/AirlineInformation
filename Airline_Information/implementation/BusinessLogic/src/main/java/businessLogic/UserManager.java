/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package businessLogic;

import dataRecords.UserData;
import java.util.List;

public interface UserManager {
    List<UserData> getAllUsers();
    UserData createUser(String lastName,String firstName,String role,String password);
    void updateUser(UserData user);
    void removeUser(UserData user);
}
