/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package info5100.university.example.Ui.FacultyRole;

import info5100.university.example.Context.UniversityContext;
import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.CourseSchedule.*;
import info5100.university.example.Persona.Faculty.FacultyProfile;
import info5100.university.example.Persona.StudentProfile;
import info5100.university.example.Util.GPAUtil;
import info5100.university.example.Util.StandingUtil;

import java.awt.CardLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Srija
 */
public class ManageStudentsProfilesJPanel extends javax.swing.JPanel {

    private final UniversityContext ctx;
    private final FacultyProfile me;
    private final JPanel CardSequencePanel;
   

    private static final Map<String, List<AssignmentRow>> GRADEBOOK = new HashMap<>();
    //Change private final Map<String, List<AssignmentRow>> assignmentBook = new HashMap<>();
    

    /**
     * Creates new form ManageStudentsProfilesJPanel
     */
    public ManageStudentsProfilesJPanel(UniversityContext ctx, FacultyProfile me, JPanel cardPanel) {
        initComponents();
        
        this.ctx = ctx;
        this.me  = me;
        this.CardSequencePanel = cardPanel;

        configureTables();
        populateCourseCombo();
        populateStudentsTable();
    }
    
    private void configureTables() {
        // students table (only Total%, Letter, GPA, Rank editable by our code; user edits happen in assignments table)
        tblStudents.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Student_ID", "Name", "Total%", "Letter", "GPA", "Rank"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 2 || c == 4) return Double.class;
                if (c == 5) return Integer.class;
                return String.class;
            }
        });

        // transcript summary table
        tblTranscriptSummary.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"CourseID", "Credits", "Grade", "Points", "Academic Standing"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        });

        // assignments table (Weight & Score editable; Max fixed to 100; Percentage computed)
        tblAssignments.setModel(new DefaultTableModel(
            new Object[][]{},
            new String[]{"Assignment", "Weight", "Max", "Score", "Percentage"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return c == 1 || c == 3; } // weight, score
            @Override public Class<?> getColumnClass(int c) {
                if (c == 1 || c == 2 || c == 3 || c == 4) return Double.class;
                return String.class;
            }
        });
    }

    private void populateCourseCombo() {
        cmbCourse.removeAllItems();
        if (ctx == null || ctx.getAllSchedules() == null) return;

        // list only the courses taught by this faculty across all schedules
        Set<String> seen = new LinkedHashSet<>();
        for (CourseSchedule cs : ctx.getAllSchedules().values()) {
            if (cs == null) continue;
            for (CourseOffer co : cs.getCourseOffers()) {
                if (co == null || co.getFacultyProfile() == null || me == null || me.getPerson() == null) continue;
                if (co.getFacultyProfile().getPerson() == null) continue;
                String myUid = me.getPerson().getUniversityId();
                String fUid  = co.getFacultyProfile().getPerson().getUniversityId();
                if (myUid != null && fUid != null && myUid.equalsIgnoreCase(fUid)) {
                    seen.add(co.getCourseNumber()); // e.g., "INFO 5100"
                }
            }
        }
        for (String num : seen) cmbCourse.addItem(num);
        if (cmbCourse.getItemCount() > 0) cmbCourse.setSelectedIndex(0);
    }

    private CourseOffer findSelectedCourseOffer() {
        String courseNum = (String) cmbCourse.getSelectedItem();
        if (courseNum == null || courseNum.trim().isEmpty()) return null;
        for (CourseSchedule cs : ctx.getAllSchedules().values()) {
            CourseOffer co = cs.getCourseOfferByNumber(courseNum);
            if (co != null) return co;
        }
        return null;
    }
    
    private String currentCourseNumber() {
        String courseNum = (String) cmbCourse.getSelectedItem();
        return (courseNum == null) ? "" : courseNum.trim();
    }
    
    private static String keyFor(String courseNum, String sid) {
        return (courseNum == null ? "" : courseNum) + "||" + (sid == null ? "" : sid);
    }

    private void populateStudentsTable() {
        DefaultTableModel m = (DefaultTableModel) tblStudents.getModel();
        m.setRowCount(0);

        CourseOffer co = findSelectedCourseOffer();
        if (co == null) return;
        
        final String courseNum = currentCourseNumber();

        for (Seat s : co.getSeatList()) {
            if (s == null || !s.isOccupied()) continue;
            SeatAssignment sa = s.getSeatAssignment();
            if (sa == null || sa.getCourseload() == null) continue;
            StudentProfile sp = sa.getCourseload().getStudentProfile();
            if (sp == null || sp.getPerson() == null) continue;

            String sid = sp.getPerson().getUniversityId();
            String name = sp.getPerson().getName();

            // derive GPA & letter from this course’s grade points
            double gp = round2(sa.getGradePoints());
            double totalPct = gpToPercent(gp); // simple 0..4.0 -> 0..100 mapping
            String letter = letterForPoints(gp);
            m.addRow(new Object[]{ sid, name, totalPct, letter, gp, null });

            /*m.addRow(new Object[]{ sid, name, totalPct, letter, gp, null });
            if (!assignmentBook.containsKey(sid)) {
            assignmentBook.put(sid, defaultAssignments());
}*/
            // ensure an assignment list exists for this student (lazy init)
            //assignmentBook.computeIfAbsent(sid, k -> defaultAssignments());
            String key = keyFor(courseNum, sid);
        if (!GRADEBOOK.containsKey(key)) {
            GRADEBOOK.put(key, defaultAssignments()); // zeros for a fresh course
        }
        }
    }
    
    private SeatAssignment findSAFor(String sid) {
    CourseOffer co = findSelectedCourseOffer();
    if (co == null || sid == null) return null;
    for (Seat s : co.getSeatList()) {
        if (s == null || !s.isOccupied()) continue;
        SeatAssignment sa = s.getSeatAssignment();
        if (sa == null || sa.getCourseload() == null) continue;
        StudentProfile sp = sa.getCourseload().getStudentProfile();
        if (sp != null && sp.getPerson() != null) {
            String uid = sp.getPerson().getUniversityId();
            if (uid != null && uid.equalsIgnoreCase(sid)) {
                return sa;
            }
        }
    }
    return null;
}
    
    

    // ---------- Buttons ----------

    private void doRefresh() {
        populateStudentsTable();
        ((DefaultTableModel) tblAssignments.getModel()).setRowCount(0);
        ((DefaultTableModel) tblTranscriptSummary.getModel()).setRowCount(0);
        txtClassGPA.setText("");
    }

    private void doRankStudents() {
        DefaultTableModel m = (DefaultTableModel) tblStudents.getModel();
        int n = m.getRowCount();
        List<int[]> rows = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Object val = m.getValueAt(i, 2); // Total%
            double pct = (val instanceof Number) ? ((Number) val).doubleValue() : parseDoubleSafe(val);
            rows.add(new int[]{i, (int) Math.round(pct * 1000)}); // scale for stable sort
        }
        // sort desc by total%
        Collections.sort(rows, new Comparator<int[]>() {
            @Override public int compare(int[] a, int[] b) { return Integer.compare(b[1], a[1]); }
        });
        // assign ranks (1..N) back to table
        int rank = 1;
        for (int[] r : rows) {
            m.setValueAt(rank++, r[0], 5);
        }
    }

    private void doClassGPA() {
        DefaultTableModel m = (DefaultTableModel) tblStudents.getModel();
        int n = m.getRowCount();
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < n; i++) {
            Object v = m.getValueAt(i, 4); // GPA col (grade points)
            if (v instanceof Number) {
                sum += ((Number) v).doubleValue();
                count++;
            } else {
                double p = parseDoubleSafe(v);
                if (!Double.isNaN(p)) { sum += p; count++; }
            }
        }
        txtClassGPA.setText(count == 0 ? "" : String.valueOf(round2(sum / count)));
    }

    private void doShowAssignmentsForSelectedStudent() {
        int row = tblStudents.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Select a student row first.");
        return;
    }
    String sid = String.valueOf(tblStudents.getValueAt(row, 0));

    String courseNum = currentCourseNumber();
    String key = keyFor(courseNum, sid);
    
    List<AssignmentRow> list = GRADEBOOK.get(key);
        if (list == null) {
            list = defaultAssignments();      // new course → start empty (0 scores)
            GRADEBOOK.put(key, list);
        }
    
    /*List<AssignmentRow> list = assignmentBook.get(sid);
    if (list == null) {
        list = defaultAssignments();
        assignmentBook.put(sid, list);
    }*/

    DefaultTableModel m = (DefaultTableModel) tblAssignments.getModel();
    m.setRowCount(0);
    for (AssignmentRow ar : list) {
        double pct = computePercent(ar);
        m.addRow(new Object[]{ ar.name, ar.weight, 100.0, ar.score, pct });
    }
        /*int row = tblStudents.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student row first.");
            return;
        }
        String sid = String.valueOf(tblStudents.getValueAt(row, 0));
        List<AssignmentRow> list = assignmentBook.computeIfAbsent(sid, k -> defaultAssignments());

        DefaultTableModel m = (DefaultTableModel) tblAssignments.getModel();
        m.setRowCount(0);
        for (AssignmentRow ar : list) {
            double pct = computePercent(ar);
            m.addRow(new Object[]{ ar.name, ar.weight, 100.0, ar.score, pct });
        }*/
    
    }
    
    private void doUpdateAssignmentsAndRecompute() {
    int sRow = tblStudents.getSelectedRow();
    if (sRow < 0) {
        JOptionPane.showMessageDialog(this, "Select a student row first.");
        return;
    }
    String sid = String.valueOf(tblStudents.getValueAt(sRow, 0));

    String courseNum = currentCourseNumber();
        String key = keyFor(courseNum, sid);
        
    // ensure list exists for this sid
    List<AssignmentRow> list = GRADEBOOK.get(key);
        if (list == null) {
            list = defaultAssignments();
            GRADEBOOK.put(key, list);
        }
    /*List<AssignmentRow> list = assignmentBook.get(sid);
    if (list == null) {
        list = defaultAssignments();
        assignmentBook.put(sid, list);
    }*/

    // read edited rows from tblAssignments back into our book; recompute row %
    DefaultTableModel m = (DefaultTableModel) tblAssignments.getModel();
    list.clear();
    for (int r = 0; r < m.getRowCount(); r++) {
        String  name  = String.valueOf(m.getValueAt(r, 0));
        double  w     = asDouble(m.getValueAt(r, 1), 0.0); // Weight (editable)
        double  score = asDouble(m.getValueAt(r, 3), 0.0); // Score (editable)
        list.add(new AssignmentRow(name, w, 100.0, score));
        m.setValueAt(computePercent(list.get(list.size() - 1)), r, 4); // Percentage
    }

    // compute total% (weighted by 'weight', normalized if weights != 1.0)
    double totalPct = 0.0;
    double totalWeights = 0.0;
    for (AssignmentRow ar : list) {
        totalPct     += (ar.score / ar.max) * ar.weight * 100.0;
        totalWeights += ar.weight;
    }
    if (totalWeights > 0 && Math.abs(totalWeights - 1.0) > 1e-6) {
        totalPct = totalPct * (1.0 / totalWeights);
    }
    totalPct = clamp(totalPct, 0.0, 100.0);

    double gp = percentToPoints(totalPct); // 0..100 -> 0..4.0
    String lt = letterForPoints(gp);

    // update tblStudents row
    DefaultTableModel sm = (DefaultTableModel) tblStudents.getModel();
    sm.setValueAt(round2(totalPct), sRow, 2); // Total%
    sm.setValueAt(lt,              sRow, 3); // Letter
    sm.setValueAt(round2(gp),      sRow, 4); // GPA points

    // persist into model (SeatAssignment) so transcript summary sees it
    SeatAssignment sa = findSAFor(sid);
    if (sa != null) {
        sa.setGradePoints((float) gp); // this feeds transcript summary
    }

    // refresh transcript summary for the same selected student so the change is visible
    doShowTranscriptSummaryForSelectedStudent();
}


    /*private void doUpdateAssignmentsAndRecompute() {
        int sRow = tblStudents.getSelectedRow();
        if (sRow < 0) {
            JOptionPane.showMessageDialog(this, "Select a student row first.");
            return;
        }
        String sid = String.valueOf(tblStudents.getValueAt(sRow, 0));
        List<AssignmentRow> list = assignmentBook.computeIfAbsent(sid, k -> defaultAssignments());

        // read edited rows back into our book; recompute percentage
        DefaultTableModel m = (DefaultTableModel) tblAssignments.getModel();
        list.clear();
        for (int r = 0; r < m.getRowCount(); r++) {
            String name = String.valueOf(m.getValueAt(r, 0));
            double weight = asDouble(m.getValueAt(r, 1), 0.0);
            double max = 100.0; // fixed
            double score = asDouble(m.getValueAt(r, 3), 0.0);
            list.add(new AssignmentRow(name, weight, max, score));
            double pct = computePercent(list.get(list.size() - 1));
            m.setValueAt(pct, r, 4);
        }

        // compute total% (sum of weight*score/max), map to GPA/Letter, update the students table row
        double totalPct = 0.0;
        double totalWeights = 0.0;
        for (AssignmentRow ar : list) {
            totalPct += (ar.score / ar.max) * ar.weight * 100.0;
            totalWeights += ar.weight;
        }
        // if weights don’t sum to 1, scale them proportionally
        if (totalWeights > 0 && Math.abs(totalWeights - 1.0) > 1e-6) {
            totalPct = totalPct * (1.0 / totalWeights);
        }
        totalPct = clamp(totalPct, 0.0, 100.0);

        double gp  = percentToPoints(totalPct);
        String lt  = letterForPoints(gp);

        DefaultTableModel sm = (DefaultTableModel) tblStudents.getModel();
        sm.setValueAt(round2(totalPct), sRow, 2);
        sm.setValueAt(lt,            sRow, 3);
        sm.setValueAt(round2(gp),    sRow, 4);
        // rank column left untouched here; user can press Rank Students
    }*/

    private void doShowTranscriptSummaryForSelectedStudent() {
        int row = tblStudents.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student row first.");
            return;
        }
        String sid = String.valueOf(tblStudents.getValueAt(row, 0));

        // find the StudentProfile via the selected course’s enrollments
        StudentProfile sp = findStudentProfileInSelectedCourseById(sid);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Could not resolve the selected student profile.");
            return;
        }

        DefaultTableModel tm = (DefaultTableModel) tblTranscriptSummary.getModel();
        tm.setRowCount(0);

        // overall GPA (weighted by credits)
        double totalPoints = 0.0;
        int totalCredits = 0;

        // list all courses (simple: use transcript’s seat assignments)
        for (SeatAssignment sa : sp.getCourseList()) {
            if (sa == null) continue;
            Course c = sa.getAssociatedCourse();
            String cid = (c != null) ? safe(c.getCOurseNumber()) : "N/A";
            int credits = sa.getCreditHours();
            double gp = sa.getGradePoints();
            String letter = letterForPoints(gp);
            totalPoints += gp * credits;
            totalCredits += credits;

            // simple standing: use StandingUtil(termGpa≈course gp, overall≈after loop we’ll compute)
            // we put a placeholder now; we’ll recompute standing after we know overall
            tm.addRow(new Object[]{ cid, credits, letter, gp, "" });
        }

        double overallGpa = (totalCredits == 0) ? 0.0 : round2(totalPoints / totalCredits);

        // now fill academic standing column based on each row’s "term-ish" measure
        for (int r = 0; r < tm.getRowCount(); r++) {
            double rowPoints = asDouble(tm.getValueAt(r, 3), 0.0);
            String standing = StandingUtil.academicStanding(rowPoints, overallGpa);
            tm.setValueAt(standing, r, 4);
        }
    }

    private StudentProfile findStudentProfileInSelectedCourseById(String sid) {
        CourseOffer co = findSelectedCourseOffer();
        if (co == null) return null;
        for (Seat s : co.getSeatList()) {
            if (s == null || !s.isOccupied()) continue;
            SeatAssignment sa = s.getSeatAssignment();
            if (sa == null || sa.getCourseload() == null) continue;
            StudentProfile sp = sa.getCourseload().getStudentProfile();
            if (sp != null && sp.getPerson() != null &&
                sid.equalsIgnoreCase(sp.getPerson().getUniversityId())) {
                return sp;
            }
        }
        return null;
    }

    // ---------- Small helpers ----------

    private static class AssignmentRow {
        String name;
        double weight; // e.g., 0.1 = 10%
        double max;    // fixed 100
        double score;  // 0..100
        AssignmentRow(String name, double weight, double max, double score) {
            this.name = name; this.weight = weight; this.max = max; this.score = score;
        }
    }

    private static List<AssignmentRow> defaultAssignments() {
        List<AssignmentRow> list = new ArrayList<>();
        list.add(new AssignmentRow("A1", 0.20, 100.0, 0.0));
        list.add(new AssignmentRow("Midterm", 0.30, 100.0, 0.0));
        list.add(new AssignmentRow("Project", 0.50, 100.0, 0.0));
        return list;
    }

    private static double computePercent(AssignmentRow ar) {
        if (ar == null || ar.max <= 0) return 0.0;
        return round2((ar.score / ar.max) * 100.0);
    }

    private static String letterForPoints(double gp) {
        // simple reverse mapping that aligns with your GPAUtil grid
        if (gp >= 3.95) return "A";
        if (gp >= 3.60) return "A-";
        if (gp >= 3.25) return "B+";
        if (gp >= 2.85) return "B";
        if (gp >= 2.55) return "B-";
        if (gp >= 2.15) return "C+";
        if (gp >= 1.85) return "C";
        if (gp >= 1.55) return "C-";
        return "F";
    }

    private static double gpToPercent(double gp) {
        // 0..4.0 -> 0..100 (linear)
        gp = clamp(gp, 0.0, 4.0);
        return round2((gp / 4.0) * 100.0);
    }

    private static double percentToPoints(double pct) {
        // 0..100 -> 0..4.0 (linear, matches gpToPercent)
        pct = clamp(pct, 0.0, 100.0);
        return round2((pct / 100.0) * 4.0);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    private static String safe(String s) { return s == null ? "" : s; }

    private static double parseDoubleSafe(Object v) {
        try { return (v == null) ? Double.NaN : Double.parseDouble(String.valueOf(v)); }
        catch (Exception e) { return Double.NaN; }
    }

    private static double asDouble(Object v, double def) {
        if (v instanceof Number) return ((Number) v).doubleValue();
        double x = parseDoubleSafe(v);
        return Double.isNaN(x) ? def : x;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblStudents = new javax.swing.JTable();
        lblCourse = new javax.swing.JLabel();
        cmbCourse = new javax.swing.JComboBox<>();
        btnBack = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTranscriptSummary = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblAssignments = new javax.swing.JTable();
        btnRankStudents = new javax.swing.JButton();
        txtClassGPA = new javax.swing.JTextField();
        btnClassGPA = new javax.swing.JButton();
        btnSummary = new javax.swing.JButton();
        btnAssignments = new javax.swing.JButton();
        btnUpdateAssignments = new javax.swing.JButton();

        tblStudents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Student_ID", "Name", "Total%", "Letter", "GPA", "Rank"
            }
        ));
        jScrollPane1.setViewportView(tblStudents);

        lblCourse.setText("Course");

        cmbCourse.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCourse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCourseActionPerformed(evt);
            }
        });

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTitle.setText("Manage Students Profile");

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        tblTranscriptSummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "CourseID", "Credits", "Grade", "Points", "Academic Standing"
            }
        ));
        jScrollPane2.setViewportView(tblTranscriptSummary);

        tblAssignments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Assignment", "Weight", "Max", "Score", "Percentage"
            }
        ));
        jScrollPane3.setViewportView(tblAssignments);

        btnRankStudents.setText("Rank Students");
        btnRankStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRankStudentsActionPerformed(evt);
            }
        });

        btnClassGPA.setText("Class GPA");
        btnClassGPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClassGPAActionPerformed(evt);
            }
        });

        btnSummary.setText("Show summary");
        btnSummary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSummaryActionPerformed(evt);
            }
        });

        btnAssignments.setText("Show Assignments");
        btnAssignments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAssignmentsActionPerformed(evt);
            }
        });

        btnUpdateAssignments.setText("update assignments");
        btnUpdateAssignments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateAssignmentsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(btnBack)
                        .addGap(396, 396, 396)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCourse)
                        .addGap(18, 18, 18)
                        .addComponent(cmbCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefresh)
                        .addGap(85, 85, 85)
                        .addComponent(btnRankStudents))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSummary))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAssignments)
                            .addComponent(btnUpdateAssignments)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 445, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnClassGPA)
                            .addComponent(txtClassGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBack)
                .addGap(11, 11, 11)
                .addComponent(lblTitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCourse)
                    .addComponent(cmbCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh)
                    .addComponent(btnRankStudents))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(75, 75, 75)
                        .addComponent(btnClassGPA)
                        .addGap(18, 18, 18)
                        .addComponent(txtClassGPA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(btnUpdateAssignments)
                        .addGap(18, 18, 18)
                        .addComponent(btnAssignments)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnSummary)
                        .addGap(80, 80, 80))))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cmbCourseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCourseActionPerformed
        // TODO add your handling code here:
        populateStudentsTable();
    // FIX: clear any previous selection so the user re-selects a student in this course
    tblStudents.clearSelection();

    ((DefaultTableModel) tblAssignments.getModel()).setRowCount(0);
    ((DefaultTableModel) tblTranscriptSummary.getModel()).setRowCount(0);
    txtClassGPA.setText("");
        /*populateStudentsTable();
        ((DefaultTableModel) tblAssignments.getModel()).setRowCount(0);
        ((DefaultTableModel) tblTranscriptSummary.getModel()).setRowCount(0);
        txtClassGPA.setText("");*/
        /*((DefaultTableModel) tblAssignments.getModel()).setRowCount(0);
        ((DefaultTableModel) tblTranscriptSummary.getModel()).setRowCount(0);
        txtClassGPA.setText("");*/
    }//GEN-LAST:event_cmbCourseActionPerformed

    private void btnRankStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRankStudentsActionPerformed
        // TODO add your handling code here:
        doRankStudents();
    }//GEN-LAST:event_btnRankStudentsActionPerformed

    private void btnClassGPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClassGPAActionPerformed
        // TODO add your handling code here:
        doClassGPA();
    }//GEN-LAST:event_btnClassGPAActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        doRefresh();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnAssignmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignmentsActionPerformed
        // TODO add your handling code here:
        doShowAssignmentsForSelectedStudent();
    }//GEN-LAST:event_btnAssignmentsActionPerformed

    private void btnSummaryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSummaryActionPerformed
        // TODO add your handling code here:
        doShowTranscriptSummaryForSelectedStudent();
    }//GEN-LAST:event_btnSummaryActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        CardSequencePanel.remove(this);
        ((CardLayout) CardSequencePanel.getLayout()).previous(CardSequencePanel);
    
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateAssignmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateAssignmentsActionPerformed
        // TODO add your handling code here:
        doUpdateAssignmentsAndRecompute();
    }//GEN-LAST:event_btnUpdateAssignmentsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssignments;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClassGPA;
    private javax.swing.JButton btnRankStudents;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSummary;
    private javax.swing.JButton btnUpdateAssignments;
    private javax.swing.JComboBox<String> cmbCourse;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCourse;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblAssignments;
    private javax.swing.JTable tblStudents;
    private javax.swing.JTable tblTranscriptSummary;
    private javax.swing.JTextField txtClassGPA;
    // End of variables declaration//GEN-END:variables
}
