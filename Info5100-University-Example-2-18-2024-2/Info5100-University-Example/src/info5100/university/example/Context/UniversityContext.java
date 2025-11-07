/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Context;


import java.util.HashMap;
import java.util.Map;
import info5100.university.example.AccessControl.AuthManager;
import info5100.university.example.Department.Department;
import info5100.university.example.CourseSchedule.CourseSchedule;
import info5100.university.example.Finance.StudentLedger;
/**
 *
 * @author Srija
 */
public class UniversityContext {
    private Department department;
    private Map<String, CourseSchedule> schedules = new HashMap<>();
    private AuthManager auth;
    private StudentLedger ledger;

    public UniversityContext(Department dept, AuthManager auth){
        this.department = dept;
        this.auth = auth;
        this.ledger = new StudentLedger();
    }

    public Department getDepartment(){ return department; }
    public void addSchedule(String term, CourseSchedule cs){ schedules.put(term, cs); }
    public CourseSchedule getSchedule(String term){ return schedules.get(term); }
    public Map<String, CourseSchedule> getAllSchedules(){ return schedules; }
    public AuthManager getAuth(){ return auth; }
    public StudentLedger getLedger(){ return ledger; }
}
