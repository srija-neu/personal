/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.Persona;

import info5100.university.example.CourseSchedule.CourseLoad;
import info5100.university.example.CourseSchedule.SeatAssignment;
import info5100.university.example.Persona.EmploymentHistory.EmploymentHistroy;
import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class StudentProfile {

    Person person;
    Transcript transcript;
    EmploymentHistroy employmenthistory;
    
    private String academicStatus;          // e.g., "Active", "Probation", "Graduated"
    private StudentAccount account;

    public StudentProfile(Person p) {

        person = p;
        transcript = new Transcript(this);
        employmenthistory = new EmploymentHistroy();
        academicStatus = "Active";          // sensible default
        account = new StudentAccount();
    }
    
    public boolean isMatch(String id) {
    if (person == null || id == null) return false;
    String uid = person.getUniversityId();
    return uid != null && uid.equalsIgnoreCase(id.trim());
}

    /*public boolean isMatch(String id) {
        return person.getPersonId().equals(id);
    }*/

    public Transcript getTranscript() {
        return transcript;
    }

    public CourseLoad getCourseLoadBySemester(String semester) {

        CourseLoad cl = transcript.getCourseLoadBySemester(semester);
    if (cl != null && cl.getStudentProfile() == null) {
        cl.setStudentProfile(this);
    }
    return cl;
        //return transcript.getCourseLoadBySemester(semester);
    }

    public CourseLoad getCurrentCourseLoad() {

        CourseLoad cl = transcript.getCurrentCourseLoad();
    if (cl != null && cl.getStudentProfile() == null) {
        cl.setStudentProfile(this);
    }
    return cl;
        //return transcript.getCurrentCourseLoad();
    }

    public CourseLoad newCourseLoad(String s) {
        CourseLoad cl = transcript.newCourseLoad(s);
    if (cl != null) {
        cl.setStudentProfile(this);
    }
    return cl;
        /*CourseLoad cl=transcript.newCourseLoad(s);
        cl.setStudentProfile(this);
        return cl;*/
        //return transcript.newCourseLoad(s);
    }

    public ArrayList<SeatAssignment> getCourseList() {

        return transcript.getCourseList();

    }
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person p) {
        this.person = p;
    }

    public String getStudentId() {
        return (person != null) ? person.getUniversityId() : null;
    }

    public String getName() {
        return (person != null) ? person.getName() : null;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public StudentAccount getAccount() {
        return account;
    }

    public void setAccount(StudentAccount account) {
        this.account = account;
    }
    
    

    @Override
    public String toString() {
        String n = getName();
        return (n != null && n.length() > 0) ? n : "Student";
    }
}
