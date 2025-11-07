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

    // --- Identity / keys ---
    private String id;             // personId / universityId (single source of truth)

    // --- Names ---
    private String firstName;
    private String lastName;
    private String name;           // full name; auto-kept in sync with first/last

    // --- Contact ---
    private String email;
    private String phone;

    // --- Address (for profile UI) ---
    private String address;        // street address
    private String city;
    private String state;
    private String country;
    private String zipCode;

    // --- Organizational meta (kept from your original) ---
    private String department;
    private String role;

    // -------- Constructors --------
    public Person() { }

    public Person(String id) {
        this.id = id;
    }

    // -------- Identity helpers --------
    /** Historic accessor used around the codebase */
    public String getPersonId() { return id; }

    public boolean isMatch(String id) {
        return id != null && id.equals(getPersonId());
    }

    /** Some places use 'universityId' – map to same backing field */
    public String getUniversityId() { return id; }
    public void setUniversityId(String universityId) { this.id = universityId; }

    public void setPersonId(String personId) { this.id = personId; }

    // -------- Names --------
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        refreshFullName();
    }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        this.lastName = lastName;
        refreshFullName();
    }

    /** Primary full-name accessor used widely */
    public String getName() {
        if ((name == null || name.isBlank()) &&
            (firstName != null || lastName != null)) {
            refreshFullName();
        }
        return name;
    }
    public void setName(String v) { this.name = v; }

    /** Some templates call these aliases */
    public String getPersonName() { return getName(); }
    public void setPersonName(String n) { setName(n); }

    private void refreshFullName() {
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName  == null ? "" : lastName.trim();
        String combined = (fn + " " + ln).trim();
        if (!combined.isBlank()) this.name = combined;
    }

    // -------- Contact --------
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    // Common aliases seen in various seed projects
    public String getPrimaryEmail() { return email; }
    public void setPrimaryEmail(String v) { this.email = v; }
    public String getEmailAddress() { return email; }
    public void setEmailAddress(String v) { this.email = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public String getPhoneNumber() { return phone; }
    public void setPhoneNumber(String v) { this.phone = v; }

    // -------- Address --------
    public String getAddress() { return address; }
    public void setAddress(String v) { this.address = v; }

    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }

    public String getState() { return state; }
    public void setState(String v) { this.state = v; }

    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }

    public String getZipCode() { return zipCode; }
    public void setZipCode(String v) { this.zipCode = v; }

    // -------- Org meta (unchanged from your original) --------
    public String getDepartment() { return department; }
    public void setDepartment(String v) { this.department = v; }

    public String getRole() { return role; }
    public void setRole(String v) { this.role = v; }

    // -------- Utilities --------
    @Override
    public String toString() {
        String n = getName();
        if (n != null && !n.isBlank()) return n;
        return (id != null ? id : super.toString());
    }
}