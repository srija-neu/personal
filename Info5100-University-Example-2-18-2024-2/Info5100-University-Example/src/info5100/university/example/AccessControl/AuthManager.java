/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.AccessControl;

import info5100.university.example.Persona.UserAccount;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Srija
 */
public class AuthManager {
    private Map<String, UserAccount> users = new HashMap<>();

    public void register(UserAccount ua){
        users.put(ua.getUserLoginName(), ua);
        if (ua == null) return;
        String key = ua.getUserLoginName(); 
        if (key == null || key.length() == 0) return;
        users.put(key, ua); 
    }

    public Session login(String username, String password){
        UserAccount ua = users.get(username);
        if (ua == null) return null;
        if (!ua.getPassword().equals(password)) return null;
        Role role = mapRole(ua);
        return new Session(ua, role);
    }
    
    public Collection<UserAccount> getAllAccounts(){
        return users.values();
    }

    private Role mapRole(UserAccount ua){
        String r = ua.getRole();
        if (r == null) return Role.STUDENT;
        String s = r.toLowerCase();
        if (s.contains("admin")) return Role.ADMIN;
        if (s.contains("faculty")) return Role.FACULTY;
        return Role.STUDENT;
    }
    
    
}

