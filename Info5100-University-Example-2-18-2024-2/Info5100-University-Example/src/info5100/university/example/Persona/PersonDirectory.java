/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.Persona;

import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class PersonDirectory {
    
      ArrayList<Person> personlist ;
    
      public PersonDirectory (){
          
       personlist = new ArrayList();

    }
      
    public ArrayList<Person> getPersonList() {
        return personlist;
    }

    public Person newPerson(String id) {

        Person p = new Person(id);
        personlist.add(p);
        return p;
    }
    
    public void addPerson(Person p, String role) {
    if (p == null) return;
    p.setRole(role);        // store the selected role: "Admin", "Faculty", "Student", "Registrar"
    personlist.add(p);
}

    public Person findPerson(String id) {

        for (Person p : personlist) {

            if (p.isMatch(id)) {
                return p;
            }
        }
            return null; //not found after going through the whole list
         }
    public boolean emailExists(String email) {
        if (email == null) return false;
        for (Person p : personlist) {
            if (p.getEmail() != null && p.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    // Prevent duplicate registration using university ID
    public boolean universityIdExists(String uid) {
        if (uid == null) return false;
        for (Person p : personlist) {
            if (p.getUniversityId() != null && p.getUniversityId().equalsIgnoreCase(uid)) {
                return true;
            }
        }
        return false;
    }

    // Find by email (used by UI when searching)
    public Person findByEmail(String email) {
        if (email == null) return null;
        for (Person p : personlist) {
            if (p.getEmail() != null && p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }
    
    public Person findByUniversityId(String uid) {
    if (uid == null) return null;
    for (Person p : personlist) {
        if (p.getUniversityId() != null && p.getUniversityId().equalsIgnoreCase(uid)) {
            return p;
        }
    }
    return null;
}

    // Delete a person record
    public void deletePerson(Person p) {
        if (p != null) {
            personlist.remove(p);
        }
    }
    
}
