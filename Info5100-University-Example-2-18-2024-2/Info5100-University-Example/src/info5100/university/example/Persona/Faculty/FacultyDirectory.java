/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.Persona.Faculty;

import info5100.university.example.Persona.*;
import info5100.university.example.Department.Department;
import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class FacultyDirectory {

    Department department;
    ArrayList<FacultyProfile> teacherlist;

    public FacultyDirectory(Department d) {

        department = d;
        teacherlist = new ArrayList();

    }
    
    public ArrayList<FacultyProfile> getFacultyList() {
        return teacherlist;
    }

    public FacultyProfile newFacultyProfile(Person p) {

        FacultyProfile sp = new FacultyProfile(p);
        teacherlist.add(sp);
        return sp;
    }
    
    public FacultyProfile getTopProfessor(){
        
        double bestratingsofar = 0.0;
        FacultyProfile BestProfSofar = null;
        for(FacultyProfile fp: teacherlist)
           if(fp.getProfAverageOverallRating()>bestratingsofar){
           bestratingsofar = fp.getProfAverageOverallRating();
           BestProfSofar = fp;
           }
        return BestProfSofar;
        
    }

    public FacultyProfile findTeachingFaculty(String id) {

        for (FacultyProfile sp : teacherlist) {

            if (sp.isMatch(id)) {
                return sp;
            }
        }
            return null; //not found after going through the whole list
         }
    
    public FacultyProfile findByUniversityId(String uid) {
        if (uid == null) return null;
        for (FacultyProfile fp : teacherlist) {
            if (fp != null && fp.getPerson() != null &&
                fp.getPerson().getUniversityId() != null &&
                fp.getPerson().getUniversityId().equalsIgnoreCase(uid)) {
                return fp;
            }
        }
        return null;
    }
    
    public void delete(FacultyProfile fp) {
        if (fp != null) {
            teacherlist.remove(fp);
        }
    }
    
    public ArrayList<FacultyProfile> findByDepartment(String departmentName) {
        ArrayList<FacultyProfile> results = new ArrayList<>();
        if (departmentName == null) return results;
        for (FacultyProfile fp : teacherlist) {
            if (fp != null && fp.getPerson() != null &&
                fp.getPerson().getDepartment() != null &&
                fp.getPerson().getDepartment().equalsIgnoreCase(departmentName)) {
                results.add(fp);
            }
        }
        return results;
    }

    public ArrayList<FacultyProfile> findByName(String name) {
        ArrayList<FacultyProfile> results = new ArrayList<>();
        if (name == null) return results;
        for (FacultyProfile fp : teacherlist) {
            if (fp != null && fp.getPerson() != null &&
                fp.getPerson().getName() != null &&
                fp.getPerson().getName().equalsIgnoreCase(name)) {
                results.add(fp);
            }
        }
        return results;
    }
    
}
