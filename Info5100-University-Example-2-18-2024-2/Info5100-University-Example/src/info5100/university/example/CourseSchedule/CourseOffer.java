package info5100.university.example.CourseSchedule;

import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.Persona.Faculty.FacultyAssignment;
import info5100.university.example.Persona.Faculty.FacultyProfile;
import java.util.ArrayList;

/**
 * Minimal, compatibility-friendly CourseOffer.
 */
public class CourseOffer {

    /* ------------ Core fields ------------ */
    private Course course;
    private ArrayList<Seat> seatlist;
    private FacultyAssignment facultyassignment;

    /* ------------ Optional display fields ------------ */
    // e.g., "Snell-201", "Mon/Wed", "10:00–11:40"
    private String roomLabel;
    private String day;
    private String timeSlot;

    /* ------------ Constructors ------------ */
    public CourseOffer(Course c) {
        this.course = c;
        this.seatlist = new ArrayList<>();
    }

    /* ------------ Instructor / faculty ------------ */
    public void AssignAsTeacher(FacultyProfile fp) {
        facultyassignment = new FacultyAssignment(fp, this);
    }

    public FacultyProfile getFacultyProfile() {
        return (facultyassignment == null) ? null : facultyassignment.getFacultyProfile();
    }

    /**
     * Return instructor name if available, otherwise empty string.
     * (Avoid throwing UnsupportedOperationException.)
     */
    public String getInstructor() {
        try {
            FacultyProfile fp = getFacultyProfile();
            if (fp != null && fp.getPerson() != null) {
                // The Person class in this project exposes getName()
                String n = fp.getPerson().getName();
                return n == null ? "" : n;
            }
        } catch (Exception ignore) {}
        return "";
    }

    /* ------------ Course identity / labels ------------ */

    /** Some seeds use this exact method name on Course (note the weird capitalization). */
    public String getCourseNumber() {
        return course.getCOurseNumber();
    }

    /** Alias expected by some UIs. */
    public String getCourseId() {
        return getCourseNumber();
    }

    /**
     * If your Course has a real name, you can adjust here.
     * We return the number as a safe label to avoid compile errors.
     */
    public String getCourseName() {
        return getCourseNumber();
    }

    /* ------------ Room & time helpers (for table display) ------------ */
    public void setRoomAndTime(String roomLabel, String day, String timeSlot) {
        this.roomLabel = roomLabel;
        this.day = day;
        this.timeSlot = timeSlot;
    }

    public String getRoomLabel() { return roomLabel; }
    public String getDay()       { return day; }
    public String getTimeSlot()  { return timeSlot; }

    /** Aliases some UIs expect. */
    public String getRoom()     { return roomLabel; }
    public String getLocation() { return roomLabel; }
    public String getTime()     { return (day == null ? "" : day) + (timeSlot == null ? "" : " " + timeSlot); }

    /* ------------ Capacity / seats ------------ */

    /** Generate seats once capacity is known. */
    public void generatSeats(int n) {
        for (int i = 0; i < n; i++) seatlist.add(new Seat(this, i));
    }

    public int getCapacity()        { return seatlist.size(); }
    public int getSeatCapacity()    { return seatlist.size(); }
    public ArrayList<Seat> getSeatList() { return seatlist; }
    /** Alias (lowercase L) some code uses. */
    public ArrayList<Seat> getSeatlist() { return seatlist; }

    public Seat getEmptySeat() {
        for (Seat s : seatlist) {
            if (!s.isOccupied()) return s;
        }
        return null;
    }

    public int getEnrolledCount() {
        int count = 0;
        for (Seat s : seatlist) if (s.isOccupied()) count++;
        return count;
    }

    /** Some UIs call this. */
    public int getEnrollment() { return getEnrolledCount(); }

    public int getAvailableSeats() {
        return Math.max(0, getCapacity() - getEnrolledCount());
    }

    /**
     * Change capacity without dropping currently-occupied seats.
     * Returns false if requested capacity would force dropping students.
     */
    public boolean setCapacity(int newCapacity){
        int enrolled = getEnrolledCount();
        if (newCapacity < enrolled) return false;    // don't evict
        int current = seatlist.size();
        if (newCapacity == current) return true;

        if (newCapacity > current) {
            for (int i = current; i < newCapacity; i++) seatlist.add(new Seat(this, i));
            return true;
        } else {
            // shrink by removing only unoccupied seats from the end
            for (int i = seatlist.size() - 1; i >= 0 && seatlist.size() > newCapacity; i--) {
                if (!seatlist.get(i).isOccupied()) seatlist.remove(i);
            }
            return seatlist.size() == newCapacity;
        }
    }

    /* ------------ Enrollment operations ------------ */

    /**
     * Enroll by assigning an empty seat and registering the SeatAssignment with the CourseLoad.
     */
    public SeatAssignment assignEmptySeat(CourseLoad cl) {
        Seat seat = getEmptySeat();
        if (seat == null) return null;
        SeatAssignment sa = seat.newSeatAssignment(cl); // links seat <-> SA
        cl.registerStudent(sa);                         // links SA -> courseload
        return sa;
    }

    /**
     * Best-effort drop. We avoid assuming specific methods on Seat/SeatAssignment
     * and simply replace the occupied seat with a fresh, empty seat at the same index.
     * Returns true if a matching seat was found and cleared.
     */
   public boolean dropStudent(SeatAssignment sa) {
    if (sa == null) return false;

    // 1) Best path: use the Seat from the SeatAssignment
    try {
        Seat s = sa.getSeat();                 // many seeds have this
        if (s != null) {
            int idx = seatlist.indexOf(s);     // same seat object in the list?
            if (idx >= 0) {
                seatlist.set(idx, new Seat(this, idx)); // replace with empty seat
                return true;
            }
        }
    } catch (Exception ignore) { /* proceed to fallback */ }

    // 2) Fallback: try to find the seat by reflection, if Seat exposes getSeatAssignment()
    try {
        for (int i = 0; i < seatlist.size(); i++) {
            Seat s = seatlist.get(i);
            try {
                Object held = s.getClass().getMethod("getSeatAssignment").invoke(s);
                if (held == sa) {
                    seatlist.set(i, new Seat(this, i)); // empty seat at same index
                    return true;
                }
            } catch (Exception ignoreInner) {
                // Seat may not have getSeatAssignment(); just skip
            }
        }
    } catch (Exception ignoreOuter) { }

    return false;
}

    /* ------------ Fees / credits / subject course passthroughs ------------ */

    public int getTotalCourseRevenues() {
        int sum = 0;
        for (Seat s : seatlist) if (s.isOccupied()) sum += course.getCoursePrice();
        return sum;
    }

    /** Original API kept for compatibility. */
    public Course getSubjectCourse() { return course; }

    /** Alias most UIs expect. */
    public Course getCourse() { return course; }

    public int getCreditHours() { return course.getCredits(); }
}