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
public class Person {
    
    private String id;
    private String name;
    private String email;
    private String phone;
    private String department;
    private String role;
    
    public Person (String id){
        
        this.id = id;
    }
    
    public Person() {
    }
    
    public String getPersonId(){
        return id;
    }

        public boolean isMatch(String id){
            if(getPersonId().equals(id)) return true;
            return false;
    }
        
    public String getUniversityId() {
        return id;
    }

    public void setUniversityId(String universityId) {
        this.id = universityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String v) {
        this.name = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String v) {
        this.phone = v;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String v) {
        this.department = v;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String v) {
        this.role = v;
    }

    @Override
    public String toString() {
        // helpful for debugging / table models
        return (name != null && name.length() > 0) ? name : (id != null ? id : super.toString());
    }
    
}
