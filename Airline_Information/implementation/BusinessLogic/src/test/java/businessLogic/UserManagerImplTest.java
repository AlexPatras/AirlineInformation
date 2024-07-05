/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package businessLogic;

import dataRecords.UserData;
import java.lang.reflect.Field;
import java.sql.SQLException;
import persistence.UserDataDAO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import persistence.UserDataDAOImpl;

public class UserManagerImplTest {
    
    private UserDataDAO userDataDAO;
    private UserManager manager;
    
    @BeforeEach
    public void setup() {
        manager = new UserManagerImpl();
        userDataDAO = mock(UserDataDAOImpl.class);
    }
    
    @Test
    public void testCreateUserSuccess() throws IllegalArgumentException, IllegalAccessException, SQLException {
        
        //Setting up Test Data
        
        String lastName = "Mustermann";
        String firstName = "Mathias";
        String role = "Sales Employee";
        String password = "TestPassword08";
        
        //Apply the same DAO to Manager class and Test Class (Required)
        
        Field[] fields = UserManagerImpl.class.getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if(fieldName.equals("userDataDAO")) {
                field.set(manager, userDataDAO);
            }
        }
        
        //Train Mock and use method
        
        UserData user = mock(UserData.class);
        when(userDataDAO.createUser(lastName, firstName, role, password)).thenReturn(user);
        manager.createUser(lastName, firstName, role, password);
        //Verify the usage
        
        verify(userDataDAO,times(1)).createUser(lastName, firstName, role, password);
    }
    
    @Test
    public void testUpdateUserSuccess() throws IllegalArgumentException, IllegalAccessException, SQLException {
        
        //Setting up Test Data
        
        String lastName = "Mustermann";
        String firstName = "Mathias";
        String role = "Sales Employee";
        String password = "TestPassword08";
        
        //Apply the same DAO to Manager class and Test Class (Required)
        
        Field[] fields = UserManagerImpl.class.getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if(fieldName.equals("userDataDAO")) {
                field.set(manager, userDataDAO);
            }
        }
        
        //Train Mock and use method
        
        UserData user = mock(UserData.class);
        when(userDataDAO.createUser(lastName, firstName, role, password)).thenReturn(user);
        manager.createUser(lastName, firstName, role, password);
        
        user.setPassword("TestPassword09");
        
        doNothing().when(userDataDAO).updateUser(user);
        manager.updateUser(user);
        
        //Verify the usage
        
        verify(userDataDAO,times(1)).updateUser(user);
    }
}
