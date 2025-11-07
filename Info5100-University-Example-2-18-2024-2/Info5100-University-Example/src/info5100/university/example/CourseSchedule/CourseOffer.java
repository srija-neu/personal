/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info5100.university.example.CourseSchedule;

import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.Persona.Faculty.FacultyAssignment;
import info5100.university.example.Persona.Faculty.FacultyProfile;
import java.util.ArrayList;

/**
 *
 * @author kal bugrara
 */
public class CourseOffer {

    Course course;
    ArrayList<Seat> seatlist;
    FacultyAssignment facultyassignment;
    private String roomLabel;   // e.g., "Snell-201"
    private String day;         // e.g., "Mon/Wed"
    private String timeSlot;

    public String getRoomLabel() { return roomLabel; }
    public String getDay()       { return day; }
    public String getTimeSlot()  { return timeSlot; }
    
    public void setRoomAndTime(String roomLabel, String day, String timeSlot){
    this.roomLabel = roomLabel;
    this.day = day;
    this.timeSlot = timeSlot;
}


    public CourseOffer(Course c) {
        course = c;
        seatlist = new ArrayList();
    }
     
    public void AssignAsTeacher(FacultyProfile fp) {

        facultyassignment = new FacultyAssignment(fp, this);
    if (!fp.getFacultyAssignments().contains(facultyassignment)) {
        fp.getFacultyAssignments().add(facultyassignment);  // add reverse link
    }
    }
    
    

    public FacultyProfile getFacultyProfile() {
    return (facultyassignment == null) ? null : facultyassignment.getFacultyProfile();
}

    public String getCourseNumber() {
        return course.getCOurseNumber();
    }
    
    public String getCourseName() {
        // If your Course class has a getName() or getCourseName(), use it.
        // Since we only see getCOurseNumber() here, return number as a safe label.
        return getCourseNumber();
    }

    public void generatSeats(int n) {

        for (int i = 0; i < n; i++) {

            seatlist.add(new Seat(this, i));

        }

    }

    public Seat getEmptySeat() {

        for (Seat s : seatlist) {

            if (!s.isOccupied()) {
                return s;
            }
        }
        return null;
    }


    public SeatAssignment assignEmptySeat(CourseLoad cl) {

        Seat seat = getEmptySeat();
        if (seat == null) {
            return null;
        }
        SeatAssignment sa = seat.newSeatAssignment(cl); //seat is already linked to course offer
        cl.registerStudent(sa); //coures offer seat is now linked to student
        return sa;
    }

    public int getTotalCourseRevenues() {

        int sum = 0;

        for (Seat s : seatlist) {
            if (s.isOccupied() == true) {
                sum = sum + course.getCoursePrice();
            }

        }
        return sum;
    }
    public Course getSubjectCourse(){
        return course;
    }
    public int getCreditHours(){
        return course.getCredits();
    }
    
    public int getEnrolledCount() {
        int count = 0;
        for (int i = 0; i < seatlist.size(); i++) {
            if (seatlist.get(i).isOccupied()) {
                count++;
            }
        }
        return count;
    }
    
    public ArrayList<Seat> getSeatList() {
        return seatlist;
    }
    
    public boolean setCapacity(int newCapacity){
    int enrolled = getEnrolledCount();
    if (newCapacity < enrolled) return false;     // don’t drop students
    int current = seatlist.size();
    if (newCapacity == current) return true;

    if (newCapacity > current) {
        for (int i = current; i < newCapacity; i++){
            seatlist.add(new Seat(this, i));
        }
        return true;
    } else {
        // shrink seats that are not occupied
        for (int i = seatlist.size() - 1; i >= 0 && seatlist.size() > newCapacity; i--){
            if (!seatlist.get(i).isOccupied()){
                seatlist.remove(i);
            }
        }
        return seatlist.size() == newCapacity; // may fail if too many are occupied
    }
}

}
