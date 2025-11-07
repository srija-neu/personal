/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Persona;

import java.util.ArrayList;
/**
 *
 * @author Srija
 */
public class UserAccountDirectory {
    ArrayList<UserAccount> useraccountlist;

    public UserAccountDirectory(){
        useraccountlist = new ArrayList<UserAccount>();
    }
    
    public ArrayList<UserAccount> getUserAccountList() {
        return useraccountlist;
    }

    public UserAccount createUserAccount(String un, String pw, Person person, String role){
        UserAccount ua = new UserAccount(un, pw, person, role);
        useraccountlist.add(ua);
        return ua;
    }

    public UserAccount IsValidUser(String un, String pw){
        for (UserAccount ua : useraccountlist) {
            if (ua.IsValidUser(un, pw)) return ua;
        }
        return null;
    }

    

    public UserAccount findByUsername(String un){
        for (UserAccount ua : useraccountlist) {
            if (ua != null && ua.toString().equals(un)) return ua;
        }
        return null;
    }
    
    public void deleteUserAccount(UserAccount ua) {
        if (ua == null) return;
        useraccountlist.remove(ua);
    }
    
    public boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }
    
}
