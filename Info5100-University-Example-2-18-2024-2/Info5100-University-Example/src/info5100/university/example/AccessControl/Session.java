/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.AccessControl;

import info5100.university.example.Persona.UserAccount;

/**
 *
 * @author Srija
 */
public class Session {
    private UserAccount userAccount;
    private Role role;

    public Session(UserAccount ua, Role r){
        this.userAccount = ua;
        this.role = r;
    }

    public UserAccount getUserAccount(){ return userAccount; }
    public Role getRole(){ return role; }
}
