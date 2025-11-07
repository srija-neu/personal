/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package info5100.university.example.Ui.StudentRole;
import info5100.university.example.Context.UniversityContext;
import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.CourseSchedule.CourseLoad;
import info5100.university.example.CourseSchedule.CourseOffer;
import info5100.university.example.CourseSchedule.SeatAssignment;
import info5100.university.example.Persona.Person;
import info5100.university.example.Persona.StudentDirectory;
import info5100.university.example.Persona.StudentProfile;
import info5100.university.example.Persona.UserAccount;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Vaishu
 */
public class TranscriptJPanel extends javax.swing.JPanel {
private UniversityContext ctx;
    private JPanel cardPanel;
    private UserAccount account;

    public TranscriptJPanel() {
        initComponents();
    }
public TranscriptJPanel(UniversityContext ctx, JPanel cardPanel, UserAccount account) {
    this(); // calls initComponents()
    this.ctx = ctx;
    this.cardPanel = cardPanel;
    this.account = account;
    cmbSemester.removeAllItems();
        cmbSemester.addItem("Semester 1");
        cmbSemester.addItem("Semester 2");
        cmbSemester.addItem("Semester 3");
        cmbSemester.addItem("Semester 4");
        refreshAll();
}
 // ---------- tiny safe helper ----------
    private static String S(Object v) { return v == null ? "" : String.valueOf(v); }

    // ---------- look up currently-logged-in StudentProfile ----------
    private StudentProfile student() {
        if (account == null || ctx == null) return null;
        Person p = account.getPerson();
        if (p == null) return null;

        // Primary path: scan directory for matching person/personId
        try {
            StudentDirectory dir = ctx.getDepartment().getStudentDirectory();
            // try common directory accessors
            List<StudentProfile> list = null;
            try { list = dir.getStudentlist(); } catch (Throwable ignore) {}
            if (list == null) { try { list = dir.getStudentList(); } catch (Throwable ignore) {} }
            if (list == null) list = new ArrayList<>();

            for (StudentProfile sp : list) {
                try {
                    if (sp.getPerson() == p) return sp;
                    if (sp.getPerson() != null &&
                        sp.getPerson().getPersonId() != null &&
                        p.getPersonId() != null &&
                        sp.getPerson().getPersonId().equals(p.getPersonId())) {
                        return sp;
                    }
                } catch (Throwable ignore) {}
            }

            // alternative lookups (covers most seeds)
            try { return dir.findStudent(p.getPersonId()); } catch (Throwable ignore) {}
            try { return dir.findStudent(account.getUserLoginName()); } catch (Throwable ignore) {}
        } catch (Throwable ignoreOuter) {}

        return null; // final fallback
    }

    // ---------- term helpers ----------
    private String currentTerm() {
        Object t = cmbSemester.getSelectedItem();
        return t == null ? "Item 1" : t.toString();
    }

    // ---------- safe getters from Course/Offer/SeatAssignment ----------
//private static String S(Object v){ return v==null ? "" : String.valueOf(v); }

private String courseIdOf(CourseOffer co){
    // On CourseOffer
    try { return S(co.getCourseNumber()); } catch (Exception ignore) {}
    try { return S(co.getCourseId()); } catch (Exception ignore) {}

    // On Course
    try {
        Object c = co.getCourse();
        if (c != null) {
            try { return S(c.getClass().getMethod("getCOurseNumber").invoke(c)); } catch (Exception ignore) {}
            try { return S(c.getClass().getMethod("getNumber").invoke(c)); }        catch (Exception ignore) {}
            try { return S(c.getClass().getMethod("getId").invoke(c)); }            catch (Exception ignore) {}
        }
    } catch (Exception ignore) {}

    return "";
}

private String courseNameOf(CourseOffer co){
    try { return S(co.getCourseName()); } catch (Exception ignore) {}
    try {
        Object c = co.getCourse();
        if (c != null) {
            try { return S(c.getClass().getMethod("getCourseName").invoke(c)); } catch (Exception ignore) {}
            try { return S(c.getClass().getMethod("getName").invoke(c)); }        catch (Exception ignore) {}
        }
    } catch (Exception ignore) {}
    return "";
}

private int courseCreditsOf(CourseOffer co){
    try { return co.getCreditHours(); } catch (Exception ignore) {}
    try {
        Object c = co.getCourse();
        if (c != null) {
            try { return (int)c.getClass().getMethod("getCredits").invoke(c); } catch (Exception ignore) {}
        }
    } catch (Exception ignore) {}
    return 0;
}

    private double numericScoreOf(SeatAssignment sa) {
        if (sa == null) return -1;
        try { return sa.GetCourseStudentScore(); } catch (Throwable ignore) {}
        // try some common alternates if present in other seeds
        try { return (double) sa.getClass().getMethod("getScore").invoke(sa); } catch (Throwable ignore) {}
        try {
            Object g = sa.getClass().getMethod("getGrade").invoke(sa); // could be letter
            if (g instanceof Number) return ((Number) g).doubleValue();
        } catch (Throwable ignore) {}
        return -1; // unknown
    }

    private String letterFromScore(double s) {
        if (s < 0) return "N/A";
        // basic 100->4 scale mapping
        if (s >= 93) return "A";
        if (s >= 90) return "A-";
        if (s >= 87) return "B+";
        if (s >= 83) return "B";
        if (s >= 80) return "B-";
        if (s >= 77) return "C+";
        if (s >= 73) return "C";
        if (s >= 70) return "C-";
        if (s >= 60) return "D";
        return "F";
    }

    private double gpaPointsFromLetter(String L) {
        switch (L) {
            case "A":  return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B":  return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C":  return 2.0;
            case "C-": return 1.7;
            case "D":  return 1.0;
            case "F":  return 0.0;
            default:   return 0.0;
        }
    }

    // ---------- compute GPAs ----------
    private double computeTermGpa(StudentProfile sp, String term) {
        if (sp == null || term == null) return 0.0;
        try {
            CourseLoad cl = sp.getCourseLoadBySemester(term);
            if (cl == null) { // some seeds use getCourseLoad(String)
                try { cl = sp.getCourseLoadBySemester(term); } catch (Throwable ignore) {}
            }
            if (cl == null) return 0.0;

            double pts = 0.0;
            int creds = 0;
            for (SeatAssignment sa : cl.getSeatAssignments()) {
                CourseOffer co = sa.getCourseOffer();
                int c = courseCreditsOf(co);
                String L = letterFromScore(numericScoreOf(sa));
                double gp = gpaPointsFromLetter(L);
                pts += gp * c;
                creds += c;
            }
            return creds == 0 ? 0.0 : (pts / creds);
        } catch (Throwable ignore) {
            return 0.0;
        }
    }

    private double computeOverallGpa(StudentProfile sp) {
        if (sp == null) return 0.0;
        try {
            // many seeds expose every enrollment via transcript.getCourseList()
            List<SeatAssignment> all = null;
            try { all = sp.getTranscript().getCourseList(); } catch (Throwable ignore) {}
            if (all == null) all = new ArrayList<>();

            double pts = 0.0;
            int creds = 0;
            for (SeatAssignment sa : all) {
                CourseOffer co = sa.getCourseOffer();
                int c = courseCreditsOf(co);
                String L = letterFromScore(numericScoreOf(sa));
                double gp = gpaPointsFromLetter(L);
                pts += gp * c;
                creds += c;
            }
            return creds == 0 ? 0.0 : (pts / creds);
        } catch (Throwable ignore) {
            return 0.0;
        }
    }

    private String standingFromGpa(double gpa) {
        // adjust thresholds if your project defines different standing rules
        if (gpa >= 3.0) return "Good Standing";
        if (gpa >= 2.0) return "Warning";
        return "Probation";
    }
    
    
    
    private void loadTableForTerm(StudentProfile sp, String term) {
        DefaultTableModel m = new DefaultTableModel(
                new Object[]{"Term", "Academic Standing", "Course ID", "Course Name", "Grade", "Term GPA", "Overall GPA"}, 0
        ) { public boolean isCellEditable(int r,int c){ return false; } };

        double termGpa   = computeTermGpa(sp, term);
        double overall   = computeOverallGpa(sp);
        String standing  = standingFromGpa(overall);

        if (sp != null) {
            try {
                CourseLoad cl = sp.getCourseLoadBySemester(term);
                if (cl == null) { try { cl = sp.getCourseLoadBySemester(term); } catch (Throwable ignore) {} }
                if (cl != null) {
                    for (SeatAssignment sa : cl.getSeatAssignments()) {
                        CourseOffer co = sa.getCourseOffer();
                        String cid = courseIdOf(co);
                        String cname = courseNameOf(co);
                        String letter = letterFromScore(numericScoreOf(sa));
                        m.addRow(new Object[]{
                                term,
                                standing,
                                cid,
                                cname,
                                letter,
                                String.format("%.2f", termGpa),
                                String.format("%.2f", overall)
                        });
                    }
                }
            } catch (Throwable ignore) {}
        }

        tblTranscript.setModel(m);

        // bottom summary fields
        txtTermGpaValue.setText(String.format("%.2f", termGpa));
        txtOverallGpaValue.setText(String.format("%.2f", overall));
        txtStandingValue.setText(standing);
    }

    private void refreshAll() {
        StudentProfile sp = student();
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "No student profile linked to this account.");
            // still clear fields
            loadTableForTerm(null, currentTerm());
            return;
        }
        loadTableForTerm(sp, currentTerm());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblSemester = new javax.swing.JLabel();
        cmbSemester = new javax.swing.JComboBox<>();
        btnRefresh = new javax.swing.JButton();
        scrTranscript = new javax.swing.JScrollPane();
        tblTranscript = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();
        lblTermGpaLabel = new javax.swing.JLabel();
        lblOverallGpaLabel = new javax.swing.JLabel();
        lblStandingLabel = new javax.swing.JLabel();
        txtTermGpaValue = new javax.swing.JTextField();
        txtOverallGpaValue = new javax.swing.JTextField();
        txtStandingValue = new javax.swing.JTextField();

        setBackground(new java.awt.Color(0, 204, 204));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 20)); // NOI18N
        jLabel1.setText("Transcripts");

        lblSemester.setText("Semester");

        cmbSemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSemesterActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        tblTranscript.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Term", "Academic Standing", "Course ID", "Course Name", "Grade ", "Term GPA", "Overall GPA"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTranscript.setColumnSelectionAllowed(true);
        tblTranscript.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrTranscript.setViewportView(tblTranscript);
        tblTranscript.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        btnBack.setText("<<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTermGpaLabel.setText("Term GPA");

        lblOverallGpaLabel.setText("Overall GPA");

        lblStandingLabel.setText("Academic Standing");

        txtTermGpaValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTermGpaValueActionPerformed(evt);
            }
        });

        txtOverallGpaValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtOverallGpaValueActionPerformed(evt);
            }
        });

        txtStandingValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtStandingValueActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scrTranscript))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnBack)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addComponent(lblSemester)
                                        .addGap(23, 23, 23)
                                        .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addComponent(btnRefresh))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(97, 97, 97)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTermGpaLabel)
                                    .addComponent(lblOverallGpaLabel)
                                    .addComponent(lblStandingLabel))
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtStandingValue, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtOverallGpaValue, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTermGpaValue, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 382, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnBack))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSemester)
                    .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh))
                .addGap(41, 41, 41)
                .addComponent(scrTranscript, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTermGpaLabel)
                    .addComponent(txtTermGpaValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOverallGpaLabel)
                    .addComponent(txtOverallGpaValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStandingLabel)
                    .addComponent(txtStandingValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
     refreshAll();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
   if (cardPanel != null && cardPanel.getLayout() instanceof java.awt.CardLayout) {
            ((java.awt.CardLayout) cardPanel.getLayout()).previous(cardPanel);
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void txtTermGpaValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTermGpaValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTermGpaValueActionPerformed

    private void txtOverallGpaValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOverallGpaValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOverallGpaValueActionPerformed

    private void txtStandingValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtStandingValueActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtStandingValueActionPerformed

    private void cmbSemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSemesterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSemesterActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<String> cmbSemester;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblOverallGpaLabel;
    private javax.swing.JLabel lblSemester;
    private javax.swing.JLabel lblStandingLabel;
    private javax.swing.JLabel lblTermGpaLabel;
    private javax.swing.JScrollPane scrTranscript;
    private javax.swing.JTable tblTranscript;
    private javax.swing.JTextField txtOverallGpaValue;
    private javax.swing.JTextField txtStandingValue;
    private javax.swing.JTextField txtTermGpaValue;
    // End of variables declaration//GEN-END:variables
}
