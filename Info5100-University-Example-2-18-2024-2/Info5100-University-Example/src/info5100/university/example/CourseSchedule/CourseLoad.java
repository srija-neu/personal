/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.CourseSchedule;

import java.util.ArrayList;
import info5100.university.example.Persona.StudentProfile;

/**
 *
 * @author kal bugrara
 */
public class CourseLoad {
    String semester;
    ArrayList<SeatAssignment> seatassignments;
    private StudentProfile student;
    
    public CourseLoad(String s){
        seatassignments = new ArrayList();
        semester = s;
    }
    public SeatAssignment newSeatAssignment(CourseOffer co){
        
        Seat seat = co.getEmptySeat(); // seat linked to courseoffer
        if (seat==null) return null;
        SeatAssignment sa = seat.newSeatAssignment(this);
        seatassignments.add(sa);  //add to students course 
        return sa;
    }
    
    public StudentProfile getStudentProfile(){
        return student;
    }
    
    public void setStudentProfile(StudentProfile s){
        this.student = s;
    }
    
    public void registerStudent(SeatAssignment sa){
        
        
        sa.assignSeatToStudent(this);
        seatassignments.add(sa);
    }
    
    public boolean dropSeatAssignment(SeatAssignment sa) {
    if (sa == null) return false;
    boolean removed = seatassignments.remove(sa);
    if (removed) {
        Seat s = sa.getSeat();
        if (s != null) s.clearAssignment(); // frees the seat
        sa.clearAssignment();                // clears links on the SA
    }
    return removed;
}
    
    public float getSemesterScore(){ //total score for a full semeter
        float sum = 0;
        for (SeatAssignment sa: seatassignments){
            sum = sum + sa.GetCourseStudentScore();
        }
        return sum;
    }
        public ArrayList<SeatAssignment> getSeatAssignments(){
            return seatassignments;
        }
        
    public boolean dropByCourseOffer(CourseOffer target){
    if (target == null) return false;
    for (int i = 0; i < seatassignments.size(); i++){
        SeatAssignment sa = seatassignments.get(i);
        if (sa != null && sa.getCourseOffer() == target){
            // free seat
            Seat seat = sa.getSeat();
            if (seat != null) {
                // mark unoccupied
                // (Seat already marks occupied=true when assigned)
                // we’ll add a small helper to Seat in the next section
                seat.free();
            }
            seatassignments.remove(i);
            return true;
        }
    }
    return false;
}
            
}
