/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package dataRecords;

public enum UserRole {
    SALES_MANAGER("Sales Manager"),
    SALES_OFFICER("Sales Officer"),
    SALES_EMPLOYEE("Sales Employee");

    private final String roleName;

    private UserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
