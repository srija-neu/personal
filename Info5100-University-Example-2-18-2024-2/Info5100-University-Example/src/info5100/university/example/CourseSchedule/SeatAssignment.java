/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.CourseSchedule;

import info5100.university.example.CourseCatalog.Course;

/**
 *
 * @author kal bugrara
 */
public class SeatAssignment {
    float grade; //(Letter grade mappings: A=4.0, A-=3.7, B+=3.3, B=3.0, )
    Seat seat;
    boolean like; //true means like and false means not like
    CourseLoad courseload;
    public SeatAssignment(CourseLoad cl, Seat s){
        seat = s;
        courseload = cl;
    }
     
    public boolean getLike(){
        return like;
    }
    public void assignSeatToStudent(CourseLoad cl){
        courseload = cl;
    }
    
    public int getCreditHours(){
        return seat.getCourseCredits();
       
    }
    public Seat getSeat(){
        return seat;
    }
    public CourseOffer getCourseOffer(){
        
        return seat.getCourseOffer();
    }
    public Course getAssociatedCourse(){
        
        return getCourseOffer().getSubjectCourse();
    }
    public float GetCourseStudentScore(){
        return getCreditHours()*grade;
    }
    
    public float getGrade(){ return grade; }
public void setGrade(float g){ grade = g; }

// (optional) letter helpers if your UI wants them:
public void setLetterGrade(String letter){
    grade = (float) info5100.university.example.Util.GPAUtil.pointsFor(letter);
}
public String getLetterGrade(){
    // If you need reverse mapping later, add a util for that. For now, return numeric as string.
    return String.valueOf(grade);
}
    
    public float getGradePoints() { return grade; }
public void setGradePoints(float gp) { this.grade = gp; }
public void clearAssignment() {
    this.courseload = null;
    this.seat = null;
    this.grade = 0f;
}
    
    
    
}
