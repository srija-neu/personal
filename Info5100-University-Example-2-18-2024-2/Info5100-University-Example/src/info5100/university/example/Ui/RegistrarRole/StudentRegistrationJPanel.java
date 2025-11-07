/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package info5100.university.example.Ui.RegistrarRole;

import info5100.university.example.Context.UniversityContext;
import info5100.university.example.CourseSchedule.CourseLoad;
import info5100.university.example.CourseSchedule.CourseOffer;
import info5100.university.example.CourseSchedule.CourseSchedule;
import info5100.university.example.CourseSchedule.SeatAssignment;
import info5100.university.example.Persona.StudentDirectory;
import info5100.university.example.Persona.StudentProfile;

import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

/**
 *
 * @author Srija
 */
public class StudentRegistrationJPanel extends javax.swing.JPanel {

    private final UniversityContext ctx;
    private final StudentDirectory studentDirectory;
    private final JPanel CardSequencePanel;
    /**
     * Creates new form StudentRegistrationJPanel
     */
    public StudentRegistrationJPanel(UniversityContext ctx, JPanel cardSequencePanel) {
        initComponents();
        this.ctx = ctx;
        this.studentDirectory = ctx.getDepartment().getStudentDirectory();
        this.CardSequencePanel = cardSequencePanel;

        populateSemesters();
        populateOffersCombo();     // based on whatever semester is selected initially
        refreshEnrollments();
    }
    
    private void populateSemesters() {
        cmbSemester.removeAllItems();
    // consistent order helps when panel is re-opened
    java.util.List<String> terms = new java.util.ArrayList<>(ctx.getAllSchedules().keySet());
    java.util.Collections.sort(terms);
    for (String term : terms) cmbSemester.addItem(term);
    if (cmbSemester.getItemCount() > 0) cmbSemester.setSelectedIndex(0);
        /*cmbSemester.removeAllItems();
        for (String term : ctx.getAllSchedules().keySet()) {
            cmbSemester.addItem(term);
        }*/
    }

    /** Fill offers combo for current semester. */
    private void populateOffersCombo() {
        cmbOffer.removeAllItems();
        String term = (String) cmbSemester.getSelectedItem();
        if (term == null) return;

        CourseSchedule cs = ctx.getSchedule(term);
        if (cs == null) return;

        for (CourseOffer co : cs.getCourseOffers()) {
            // show "COURSEID - Enrolled/Cap"
            int cap = (co.getSeatList() != null) ? co.getSeatList().size() : 0;
            int en  = co.getEnrolledCount();
            String label = co.getCourseNumber() + " - " + en + "/" + cap;
            cmbOffer.addItem(label);
        }
    }

    private CourseOffer selectedOffer() {
        String term = (String) cmbSemester.getSelectedItem();
        if (term == null) return null;
        CourseSchedule cs = ctx.getSchedule(term);
        if (cs == null) return null;

        int idx = cmbOffer.getSelectedIndex();
        if (idx < 0) return null;

        String sel = (String) cmbOffer.getSelectedItem(); // like "INFO 5100 - 3/30"
        if (sel == null) return null;
        String courseId = sel.split(" - ", 2)[0].trim();
        return cs.getCourseOfferByNumber(courseId);
    }

    private StudentProfile findStudentById(String uid) {
        if (uid == null || uid.isEmpty()) return null;
        for (StudentProfile sp : studentDirectory.getStudentList()) {
            if (sp.getPerson() != null &&
                sp.getPerson().getUniversityId() != null &&
                sp.getPerson().getUniversityId().equalsIgnoreCase(uid)) {
                return sp;
            }
        }
        return null;
    }

    private static String safe(String s){ return (s==null) ? "" : s; }

    private void refreshEnrollments() {
        
        DefaultTableModel dtm = (DefaultTableModel) tblEnrollments.getModel();
        dtm.setRowCount(0);

        String selTerm = (String) cmbSemester.getSelectedItem();

        // FIX: Always show ALL enrollments across ALL students (ignore txtStudentId filter)
        for (StudentProfile sp : studentDirectory.getStudentList()) {
            if (sp == null) continue;
            String suid = (sp.getPerson() != null) ? sp.getPerson().getUniversityId() : "";
            String sname = (sp.getPerson() != null) ? sp.getPerson().getName() : "";
            java.util.ArrayList<SeatAssignment> all = sp.getCourseList();
            if (all == null) continue;

            for (SeatAssignment sa : all) {
                if (sa == null) continue;
                CourseOffer co = sa.getCourseOffer();
                if (co == null) continue;

                String cid = co.getCourseNumber();

                // FIX: Removed CourseName column; row now has 5 columns
                dtm.addRow(new Object[] {
                        suid,
                        safe(sname),
                        cid,
                        safe(selTerm),   // we lack a public getter for exact semester; show selected term
                        "Enrolled"
                });
            }
        }
    /*DefaultTableModel dtm = (DefaultTableModel) tblEnrollments.getModel();
    dtm.setRowCount(0);

    String selTerm = (String) cmbSemester.getSelectedItem(); // for display only
    String uid = txtStudentId.getText() == null ? "" : txtStudentId.getText().trim();

    if (uid.isEmpty()) {
        // Show ALL enrollments across ALL students for visibility after navigation
        for (StudentProfile sp : studentDirectory.getStudentList()) {
            if (sp == null) continue;
            String suid = (sp.getPerson() != null) ? sp.getPerson().getUniversityId() : "";
            String sname = (sp.getPerson() != null) ? sp.getPerson().getName() : "";
            java.util.ArrayList<SeatAssignment> all = sp.getCourseList();
            if (all == null) continue;

            for (SeatAssignment sa : all) {
                if (sa == null) continue;
                CourseOffer co = sa.getCourseOffer();
                if (co == null) continue;

                String cid   = co.getCourseNumber();
                String cname = co.getCourseName();

                dtm.addRow(new Object[] {
                        suid,
                        safe(sname),
                        cid,
                        cname,
                        // We don’t have a public getter for CourseLoad.semester here,
                        // so display the currently selected term for consistency.
                        safe(selTerm),
                        "Enrolled"
                });
            }
        }
        return;
    }

    // Otherwise: filter by the typed Student ID (original behavior)
    StudentProfile sp = findStudentById(uid);
    if (sp == null) return;

    java.util.ArrayList<SeatAssignment> all = sp.getCourseList();
    for (SeatAssignment sa : all) {
        CourseOffer co = sa.getCourseOffer();
        if (co == null) continue;

        String cid   = co.getCourseNumber();
        String cname = co.getCourseName();

        dtm.addRow(new Object[]{
                uid,
                safe(sp.getName()),
                cid,
                cname,
                safe(selTerm),
                "Enrolled"
        });
    }*/
}

    /*private void refreshEnrollments() {
        DefaultTableModel dtm = (DefaultTableModel) tblEnrollments.getModel();
        dtm.setRowCount(0);

        String uid = txtStudentId.getText().trim();
        if (uid.isEmpty()) return;

        StudentProfile sp = findStudentById(uid);
        if (sp == null) return;

        // list all registrations across semesters for this student
        ArrayList<SeatAssignment> all = sp.getCourseList();
        for (SeatAssignment sa : all) {
            CourseOffer co = sa.getCourseOffer();
            if (co == null) continue;
            String cid   = co.getCourseNumber();
            String cname = co.getCourseName();
            // try to infer semester from the courload (not stored), so show current semester selection
            String term = (String) cmbSemester.getSelectedItem();

            dtm.addRow(new Object[]{
                uid,
                safe(sp.getName()),
                cid,
                cname,
                safe(term),
                "Enrolled"
            });
        }
    }*/
    



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblSemester = new javax.swing.JLabel();
        lblCourseOffer = new javax.swing.JLabel();
        lblStudent = new javax.swing.JLabel();
        cmbSemester = new javax.swing.JComboBox<>();
        txtStudentId = new javax.swing.JTextField();
        cmbOffer = new javax.swing.JComboBox<>();
        jScrollPaneEnrollments = new javax.swing.JScrollPane();
        tblEnrollments = new javax.swing.JTable();
        btnEnroll = new javax.swing.JButton();
        btnDrop = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        lblSemester.setText("Semester");

        lblCourseOffer.setText("Course Offer");

        lblStudent.setText("Student");

        cmbSemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbSemester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSemesterActionPerformed(evt);
            }
        });

        cmbOffer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbOffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbOfferActionPerformed(evt);
            }
        });

        tblEnrollments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Student ID", "Student Name", "Course ID", "Semester ", "Status "
            }
        ));
        jScrollPaneEnrollments.setViewportView(tblEnrollments);

        btnEnroll.setText("Enroll");
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        btnDrop.setText("Drop");
        btnDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDropActionPerformed(evt);
            }
        });

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSemester)
                            .addComponent(lblCourseOffer))
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(499, Short.MAX_VALUE))
                            .addComponent(cmbOffer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblStudent)
                                .addGap(58, 58, 58)
                                .addComponent(txtStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPaneEnrollments, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(53, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(68, 68, 68)
                .addComponent(btnEnroll)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDrop)
                .addGap(157, 157, 157)
                .addComponent(btnBack)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSemester)
                    .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCourseOffer)
                    .addComponent(cmbOffer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStudent)
                    .addComponent(txtStudentId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPaneEnrollments, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEnroll)
                    .addComponent(btnDrop)
                    .addComponent(btnBack))
                .addContainerGap(18, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed
        // TODO add your handling code here:
        String uid  = txtStudentId.getText().trim();
        String term = (String) cmbSemester.getSelectedItem();
        if (uid.isEmpty() || term == null) {
            JOptionPane.showMessageDialog(this, "Enter Student ID and select a semester.");
            return;
        }
        StudentProfile sp = findStudentById(uid);
        if (sp == null) {
            JOptionPane.showMessageDialog(this, "Student not found.");
            return;
        }

        CourseOffer co = selectedOffer();
        if (co == null) {
            JOptionPane.showMessageDialog(this, "Select a course offering.");
            return;
        }

        CourseLoad cl = sp.getCourseLoadBySemester(term);
        if (cl == null) cl = sp.newCourseLoad(term);
        
        for (SeatAssignment sa : new ArrayList<>(cl.getSeatAssignments())) {
            if (sa != null && sa.getCourseOffer() == co) {
                JOptionPane.showMessageDialog(this, "Student is already enrolled in " + co.getCourseNumber() + " for " + term + ".");
                return;
            }
        }

        SeatAssignment sa = co.assignEmptySeat(cl);
        if (sa == null) {
            JOptionPane.showMessageDialog(this, "No available seats.");
            return;
        }
        
          txtStudentId.setText("");

        refreshEnrollments();
        // also refresh the offers combo so enrolled count updates
        populateOffersCombo();
        JOptionPane.showMessageDialog(this, "Student enrolled.");
    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDropActionPerformed
        // TODO add your handling code here:
        int r = tblEnrollments.getSelectedRow();
    String term = (String) cmbSemester.getSelectedItem();

    if (r >= 0 && term != null) {
        // Columns: 0=Student ID, 1=Student Name, 2=Course ID, 3=Course Name, 4=Semester, 5=Status
        String uidFromRow = String.valueOf(tblEnrollments.getValueAt(r, 0));
        String courseId   = String.valueOf(tblEnrollments.getValueAt(r, 2));

        if (uidFromRow != null && !uidFromRow.trim().isEmpty() &&
            courseId   != null && !courseId.trim().isEmpty()) {

            StudentProfile sp = findStudentById(uidFromRow.trim());
            if (sp == null) {
                JOptionPane.showMessageDialog(this, "Student not found.");
                return;
            }

            CourseSchedule cs = ctx.getSchedule(term);
            if (cs == null) {
                JOptionPane.showMessageDialog(this, "Selected semester not found.");
                return;
            }

            CourseOffer co = cs.getCourseOfferByNumber(courseId.trim());
            if (co == null) {
                JOptionPane.showMessageDialog(this, "Course offering not found for selected semester.");
                return;
            }

            CourseLoad cl = sp.getCourseLoadBySemester(term);
            if (cl == null) {
                JOptionPane.showMessageDialog(this, "No registrations for this student in the selected semester.");
                return;
            }

            // find the student's SeatAssignment for this offer
            SeatAssignment target = null;
            java.util.ArrayList<SeatAssignment> list = cl.getSeatAssignments();
            for (int i = 0; i < list.size(); i++) {
                SeatAssignment sa = list.get(i);
                if (sa != null && sa.getCourseOffer() == co) {
                    target = sa;
                    break;
                }
            }

            if (target == null) {
                JOptionPane.showMessageDialog(this, "Student is not enrolled in the selected course.");
                return;
            }

            // Use your CourseLoad helper to drop & free the seat (no reflection)
            boolean ok = cl.dropSeatAssignment(target);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Could not drop the student from the course.");
                return;
            }
            
            txtStudentId.setText("");

            // UI refreshes
            refreshEnrollments();
            populateOffersCombo();
            JOptionPane.showMessageDialog(this, "Student dropped from " + courseId + ".");
            return; // done
        }
    }

    String uid  = txtStudentId.getText().trim();
    term = (String) cmbSemester.getSelectedItem();
    if (uid.isEmpty() || term == null) {
        JOptionPane.showMessageDialog(this, "Select a row in the table OR enter Student ID and select a semester.");
        return;
    }
    StudentProfile sp = findStudentById(uid);
    if (sp == null) {
        JOptionPane.showMessageDialog(this, "Student not found.");
        return;
    }
    CourseOffer co = selectedOffer();
    if (co == null) {
        JOptionPane.showMessageDialog(this, "Select a course offering.");
        return;
    }

    CourseLoad cl = sp.getCourseLoadBySemester(term);
    if (cl == null) {
        JOptionPane.showMessageDialog(this, "No registrations for this semester.");
        return;
    }

    SeatAssignment target = null;
    for (SeatAssignment sa : new java.util.ArrayList<SeatAssignment>(cl.getSeatAssignments())) {
        if (sa.getCourseOffer() == co) {
            target = sa; break;
        }
    }
    if (target == null) {
        JOptionPane.showMessageDialog(this, "Student not enrolled in selected offering.");
        return;
    }

    boolean ok = cl.dropSeatAssignment(target);
    if (!ok) {
        JOptionPane.showMessageDialog(this, "Could not drop the student from the course.");
        return;
    }

     txtStudentId.setText("");
    refreshEnrollments();
    populateOffersCombo();
    JOptionPane.showMessageDialog(this, "Student dropped.");
    }//GEN-LAST:event_btnDropActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        CardSequencePanel.remove(this);
        ((java.awt.CardLayout) CardSequencePanel.getLayout()).previous(CardSequencePanel);
    }//GEN-LAST:event_btnBackActionPerformed

    private void cmbSemesterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSemesterActionPerformed
        // TODO add your handling code here:
        populateOffersCombo();   // offering list depends on semester
    refreshEnrollments();
    }//GEN-LAST:event_cmbSemesterActionPerformed

    private void cmbOfferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbOfferActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbOfferActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDrop;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JComboBox<String> cmbOffer;
    private javax.swing.JComboBox<String> cmbSemester;
    private javax.swing.JScrollPane jScrollPaneEnrollments;
    private javax.swing.JLabel lblCourseOffer;
    private javax.swing.JLabel lblSemester;
    private javax.swing.JLabel lblStudent;
    private javax.swing.JTable tblEnrollments;
    private javax.swing.JTextField txtStudentId;
    // End of variables declaration//GEN-END:variables
}
