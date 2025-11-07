/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.bootstrap;

import info5100.university.example.Context.UniversityContext;
import info5100.university.example.AccessControl.AuthManager;
import info5100.university.example.Department.Department;
import info5100.university.example.CourseCatalog.CourseCatalog;
import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.CourseSchedule.CourseSchedule;
import info5100.university.example.CourseSchedule.CourseOffer;
import info5100.university.example.Persona.UserAccount;
import info5100.university.example.Persona.Person;
import info5100.university.example.Persona.PersonDirectory;
import info5100.university.example.Persona.StudentDirectory;
import info5100.university.example.Persona.StudentProfile;
/**
 *
 * @author Srija
 */
public class ConfigureAUniversity {
    public static UniversityContext init(){
        // Department + catalog
        Department dept = new Department("Information Systems");
        CourseCatalog catalog = dept.getCourseCatalog();
        Course info5100 = catalog.newCourse(
            "Application Engineering and Development", "INFO 5100", 4);
        Course info6150 = catalog.newCourse(
            "Web Design and User Experience", "INFO 6150", 4);
        Course info6210 = catalog.newCourse(
            "Data Management and Database Design", "INFO 6210", 4);

        // Schedules / course offers
        CourseSchedule fall = dept.newCourseSchedule("Fall 2025");
        CourseOffer o5100 = fall.newCourseOffer("INFO 5100");
        o5100.generatSeats(30);
        CourseOffer o6150 = fall.newCourseOffer("INFO 6150");
        o6150.generatSeats(25);
        CourseOffer o6210 = fall.newCourseOffer("INFO 6210");
        o6210.generatSeats(25);
        
        PersonDirectory persondirectory = dept.getPersonDirectory();

        Person adminPerson = persondirectory.newPerson("admin");
        adminPerson.setName("System Admin");
        adminPerson.setEmail("admin@northeastern.edu");
        adminPerson.setPhone("617-555-0000");
        adminPerson.setDepartment("Information Systems");
        adminPerson.setRole("Admin");

        Person facultyPerson = persondirectory.newPerson("faculty1");
        facultyPerson.setName("Prof. Jane Faculty");
        facultyPerson.setEmail("jane.faculty@northeastern.edu");
        facultyPerson.setPhone("617-555-0001");
        facultyPerson.setDepartment("Information Systems");
        facultyPerson.setRole("Faculty");

        Person studentPerson = persondirectory.newPerson("student1");
        studentPerson.setName("Alex Student");
        studentPerson.setEmail("alex.student@northeastern.edu");
        studentPerson.setPhone("617-555-0002");
        studentPerson.setDepartment("Information Systems");
        studentPerson.setRole("Student");

        Person registrarPerson = persondirectory.newPerson("reg1");
        registrarPerson.setName("Riley Registrar");
        registrarPerson.setEmail("riley.registrar@northeastern.edu");
        registrarPerson.setPhone("617-555-0003");
        registrarPerson.setDepartment("Information Systems");
        registrarPerson.setRole("Registrar");

        StudentDirectory studentdirectory = dept.getStudentDirectory();
        StudentProfile studentprofile1 = studentdirectory.newStudentProfile(studentPerson);
        // Auth and default users
        AuthManager auth = new AuthManager();
        info5100.university.example.Persona.Faculty.FacultyDirectory fdir = dept.getFacultyDirectory();
fdir.newFacultyProfile(facultyPerson);
        

        UserAccount admin = new UserAccount("admin", "admin123", adminPerson, "Admin");
        UserAccount faculty = new UserAccount("faculty1", "faculty123", facultyPerson, "Faculty");
        UserAccount student = new UserAccount("student1", "student123", studentPerson, "Student");
        UserAccount registrar = new UserAccount("reg1", "reg123", registrarPerson, "Registrar");
        auth.register(admin);
        auth.register(faculty);
        auth.register(student);
        auth.register(registrar);

        UniversityContext ctx = new UniversityContext(dept, auth);
        ctx.addSchedule("Fall 2025", fall);
        return ctx;
    }
}
