/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package info5100.university.example.Coursework;

import java.time.LocalDateTime;

/**
 *
 * @author vaishu
 */
public class AssignmentSubmission {
     private String studentId;
    private String term;
    private String courseId;
    private String assignment;
    private LocalDateTime submittedAt;
    private String status; // "Pending", "Submitted", "Resubmitted", "Graded"
    private Integer score; // nullable
    private String filePath;

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getAssignment() { return assignment; }
    public void setAssignment(String assignment) { this.assignment = assignment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
