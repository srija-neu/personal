/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.Persona;

/**
 *
 * @author kal bugrara
 */

import info5100.university.example.workareas.Workarea;

/**
 *
 * @author kal bugrara
 */
public class UserAccount {
    private String userLoginName;
    private String password;
    private String role;     // "Admin", "Faculty", "Student", "Registrar"
    private boolean active = true;
    private Person person;
    
    public UserAccount(String userLoginName, String password, Person person, String role){
        this.userLoginName = userLoginName;
        this.password = password;
        this.person = person;
        this.role = role;
    }
    
    public boolean IsValidUser(String un, String pw){
        if (active == false) return false;
        return userLoginName != null && userLoginName.equals(un)
            && password != null && password.equals(pw);
    }

    public String getUserLoginName() { return userLoginName; }
    public void setUserLoginName(String v) { userLoginName = v; }
    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }
    public String getRole() { return role; }
    public void setRole(String v) { role = v; } 
    public boolean isActive() { return active; }
    public void setActive(boolean v) { active = v; }
    public Person getPerson() { return person; }
    public void setPerson(Person p) { person = p; }

@Override
public String toString() { return userLoginName; }
    // getters/setters for login, password, role, active, person
    // toString() returns userLoginName

    
}