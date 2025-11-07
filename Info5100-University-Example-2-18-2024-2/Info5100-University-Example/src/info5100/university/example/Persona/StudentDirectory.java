/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.Persona;

import info5100.university.example.Department.Department;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kal bugrara
 */
public class StudentDirectory {

    Department department;
    ArrayList<StudentProfile> studentlist;

    public StudentDirectory(Department d) {

        department = d;
        studentlist = new ArrayList();

    }
    
    public ArrayList<StudentProfile> getStudentList() {
        return studentlist;
    }

    public StudentProfile newStudentProfile(Person p) {

        StudentProfile sp = new StudentProfile(p);
        studentlist.add(sp);
        return sp;
    }

    public StudentProfile findStudent(String id) {

        for (StudentProfile sp : studentlist) {

            if (sp.isMatch(id)) {
                return sp;
            }
        }
            return null; //not found after going through the whole list
         }
    
    public StudentProfile findByUniversityId(String uid) {
    if (uid == null) return null;
    for (StudentProfile sp : studentlist) {
        if (sp != null && sp.getPerson() != null &&
            sp.getPerson().getUniversityId() != null &&
            sp.getPerson().getUniversityId().equalsIgnoreCase(uid)) {
            return sp;
        }
    }
    return null;
}
    
    
    public ArrayList<StudentProfile> findByDepartment(String departmentName) {
        ArrayList<StudentProfile> results = new ArrayList<>();
        if (departmentName == null) return results;

        for (StudentProfile sp : studentlist) {
            if (sp.getPerson() != null &&
                sp.getPerson().getDepartment() != null &&
                sp.getPerson().getDepartment().equalsIgnoreCase(departmentName)) {
                results.add(sp);
            }
        }
        return results;
    }

    // Find by name (used in search)
    public ArrayList<StudentProfile> findByName(String name) {
        ArrayList<StudentProfile> results = new ArrayList<>();
        if (name == null) return results;

        for (StudentProfile sp : studentlist) {
            if (sp.getPerson() != null &&
                sp.getPerson().getName() != null &&
                sp.getPerson().getName().equalsIgnoreCase(name)) {
                results.add(sp);
            }
        }
        return results;
    }

    // Delete a student record
    public void deleteStudent(StudentProfile sp) {
        if (sp != null) {
            studentlist.remove(sp);
        }
    }

    public List<StudentProfile> getStudentlist() {
    return studentlist;
}
    
}
