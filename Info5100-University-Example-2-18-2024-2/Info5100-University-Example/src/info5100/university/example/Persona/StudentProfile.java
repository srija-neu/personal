package info5100.university.example.Persona;

import info5100.university.example.CourseSchedule.CourseLoad;
import info5100.university.example.CourseSchedule.SeatAssignment;
import info5100.university.example.Persona.EmploymentHistory.EmploymentHistroy;
import java.util.ArrayList;

/** Student profile model with UI-friendly fields and compat aliases. */
public class StudentProfile {

    // Core relations
    private Person person;
    private Transcript transcript;
    private EmploymentHistroy employmenthistory;
    private StudentAccount account;

    // Status
    private String academicStatus;                 // "Active", "Probation", ...

    // UI/display-oriented attributes
    private String program;                        // e.g., "Information Systems"
    private String major;                          // alias some seeds use
    private String departmentName;                 // e.g., "Khoury College"
    private String degree;                         // e.g., "Masters"
    private Double debitAmount;                    // outstanding balance (optional)

    // -------------------- Constructors --------------------

    public StudentProfile(Person p) {
        this.person = p;
        this.transcript = new Transcript(this);
        this.employmenthistory = new EmploymentHistroy();
        this.account = new StudentAccount();

        this.academicStatus = "Active";
    }

    // -------------------- Identity / Matching --------------------

    /** Match by University ID (null-safe). */
    public boolean isMatch(String id) {
        if (person == null || id == null) return false;
        String uid = person.getUniversityId();
        return uid != null && uid.equalsIgnoreCase(id.trim());
    }

    // -------------------- Transcript / Courses --------------------

    public Transcript getTranscript() { return transcript; }

    public CourseLoad getCourseLoadBySemester(String semester) {
        return transcript == null ? null : transcript.getCourseLoadBySemester(semester);
    }

    /** Compat alias used by some panels/templates. */
    public CourseLoad getCourseLoad(String term) {
        return getCourseLoadBySemester(term);
    }

    public CourseLoad getCurrentCourseLoad() {
        return transcript == null ? null : transcript.getCurrentCourseLoad();
    }

    public CourseLoad newCourseLoad(String semester) {
        return transcript == null ? null : transcript.newCourseLoad(semester);
    }

    public ArrayList<SeatAssignment> getCourseList() {
        return transcript == null ? new ArrayList<>() : transcript.getCourseList();
    }

    // -------------------- Person bridge --------------------

    public Person getPerson() { return person; }
    public void setPerson(Person p) { this.person = p; }

    /** Preferred external ID for a student in this project. */
    public String getStudentId() {
        return person == null ? null : person.getUniversityId();
    }

    public String getName() {
        if (person == null) return null;
        String n = person.getName();
        return (n != null && !n.isBlank())
               ? n
               : ((person.getFirstName() != null ? person.getFirstName() : "") +
                  (person.getLastName() != null ? " " + person.getLastName() : "")).trim();
    }

    // -------------------- Status / Accounts --------------------

    public String getAcademicStatus() { return academicStatus; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }

    public StudentAccount getAccount() { return account; }
    public void setAccount(StudentAccount account) { this.account = account; }

    // -------------------- Program / Department / Degree --------------------
    // Program & Major kept as aliases so either can be used by different templates.

    public String getProgram() { return program != null ? program : major; }
    public void setProgram(String program) {
        this.program = (program == null || program.isBlank()) ? null : program.trim();
    }

    public String getMajor() { return major != null ? major : program; }
    public void setMajor(String major) {
        this.major = (major == null || major.isBlank()) ? null : major.trim();
    }

    /** Display name of the department for UI (kept to match panel code). */
    public String getDepartment() { return departmentName; }

    public String getDepartmentName() { return departmentName; }

    public void setDepartmentName(String name) {
        this.departmentName = (name == null || name.isBlank()) ? null : name.trim();
    }

    /** Compat alias — your panel calls setDepartment(String). */
    public void setDepartment(String name) { setDepartmentName(name); }

    public String getDegree() { return degree; }
    public void setDegree(String degree) {
        this.degree = (degree == null || degree.isBlank()) ? null : degree.trim();
    }

    // -------------------- Optional finance helper --------------------

    public Double getDebitAmount() { return debitAmount; }
    public double getDebitAmountOrZero() { return debitAmount == null ? 0.0 : debitAmount; }
    public void setDebitAmount(Double amt) { this.debitAmount = amt; }

    // -------------------- Misc --------------------

    @Override
    public String toString() {
        String n = getName();
        return (n != null && !n.isBlank()) ? n : "Student";
    }
}