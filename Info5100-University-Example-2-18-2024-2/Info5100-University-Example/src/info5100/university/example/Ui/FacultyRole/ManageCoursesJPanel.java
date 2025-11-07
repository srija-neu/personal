/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package info5100.university.example.Ui.FacultyRole;

import info5100.university.example.Context.UniversityContext;
import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.CourseSchedule.CourseOffer;
import info5100.university.example.CourseSchedule.CourseSchedule;
import info5100.university.example.Persona.Faculty.FacultyProfile;

import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.File;
/**
 *
 * @author Srija
 */
public class ManageCoursesJPanel extends javax.swing.JPanel {
    
    private final UniversityContext ctx;
    private final FacultyProfile faculty;          // which faculty is logged-in
    private final JPanel CardSequencePanel;
    
    /*private final Map<CourseOffer, String> descriptionByOffer = new HashMap<>();
    private final Map<CourseOffer, String> scheduleByOffer    = new HashMap<>();
    private final Map<CourseOffer, String> syllabusByOffer    = new HashMap<>();
    private final Map<CourseOffer, Boolean> openByOffer       = new HashMap<>();*/
    
    private static final Map<CourseOffer, String> DESCRIPTION_STORE = new WeakHashMap<>();
    private static final Map<CourseOffer, String> SCHEDULE_STORE    = new WeakHashMap<>();
    private static final Map<CourseOffer, String> SYLLABUS_STORE    = new WeakHashMap<>();
    private static final Map<CourseOffer, Boolean> OPEN_STORE       = new WeakHashMap<>();

    private final List<CourseOffer> tableIndexToOffer = new ArrayList<>();
    /**
     * Creates new form ManageCoursesJPanel
     */
    public ManageCoursesJPanel(UniversityContext ctx,
                               FacultyProfile faculty,
                               JPanel cardSequencePanel) {
        initComponents();
        this.ctx = ctx;
        this.faculty = faculty;
        this.CardSequencePanel = cardSequencePanel;

        configureTableModel();
        populateTable();
    }
    
    private void configureTableModel() {
        // Make Title / Description / Schedule / Capacity / Syllabus editable in-table.
        DefaultTableModel dtm = new DefaultTableModel(
                new Object[][]{},
                new String[]{"CourseID", "Title", "Description", "Schedule", "Capacity", "Syllabus", "Enrollment open?"}
        ) {
            @Override public boolean isCellEditable(int r, int c) {
                // Editable except CourseID and Enrollment Open? (we use buttons for the boolean)
                return c == 1 || c == 2 || c == 3 || c == 4 || c == 5;
            }
            @Override public Class<?> getColumnClass(int col) {
                if (col == 4) return Integer.class;    // Capacity
                if (col == 6) return Boolean.class;    // Enrollment open?
                return String.class;
            }
        };
        tblCourseDetails.setModel(dtm);
    }

    private void populateTable() {
        DefaultTableModel dtm = (DefaultTableModel) tblCourseDetails.getModel();
        dtm.setRowCount(0);
        tableIndexToOffer.clear();

        if (ctx == null || faculty == null) return;

        for (Map.Entry<String, CourseSchedule> e : ctx.getAllSchedules().entrySet()) {
            CourseSchedule cs = e.getValue();
            if (cs == null) continue;

            for (CourseOffer co : cs.getCourseOffers()) {
        if (co == null) continue;

        FacultyProfile fp = co.getFacultyProfile();
        if (fp == null || fp.getPerson() == null || faculty == null || faculty.getPerson() == null) {
            continue;
        }
        String assignedUid = fp.getPerson().getUniversityId();
        String myUid       = faculty.getPerson().getUniversityId();
        if (assignedUid == null || myUid == null || !assignedUid.equalsIgnoreCase(myUid)) {
            continue; // not my course
        }

        Course c = co.getSubjectCourse();
        String courseId = (c != null) ? c.getCOurseNumber() : co.getCourseNumber();

        String title = (c != null && c.getName() != null && !c.getName().isEmpty())
                ? c.getName() : courseId;
        String desc  = DESCRIPTION_STORE.getOrDefault(co, "");
        String sched = SCHEDULE_STORE.getOrDefault(co, "");
        int capacity = (co.getSeatList() != null) ? co.getSeatList().size() : 0;
        String syll  = SYLLABUS_STORE.getOrDefault(co, "");
        Boolean open = OPEN_STORE.getOrDefault(co, Boolean.TRUE);
        dtm.addRow(new Object[]{ courseId, title, desc, sched, capacity, syll, open });
                tableIndexToOffer.add(co);
        tableIndexToOffer.add(co);
    }
        }
    }

    private CourseOffer selectedOffer() {
        int row = tblCourseDetails.getSelectedRow();
        if (row < 0) return null;
        if (row >= tableIndexToOffer.size()) return null;
        return tableIndexToOffer.get(row);
    }

    private static int parseIntSafe(Object v, int defVal) {
        try {
            if (v == null) return defVal;
            if (v instanceof Integer) return (Integer) v;
            return Integer.parseInt(v.toString().trim());
        } catch (Exception e) { return defVal; }
    }
    
    private void updateOpenFlagInTable(boolean isOpen) {
        int row = tblCourseDetails.getSelectedRow();
        if (row >= 0) {
            DefaultTableModel dtm = (DefaultTableModel) tblCourseDetails.getModel();
            dtm.setValueAt(isOpen, row, 6);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblManageCourses = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCourseDetails = new javax.swing.JTable();
        btnUpdate = new javax.swing.JButton();
        btnOpenEnrollment = new javax.swing.JButton();
        btnCloseEnrollment = new javax.swing.JButton();

        lblManageCourses.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblManageCourses.setText("Manage Courses");

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        tblCourseDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Title", "Description", "Schedule", "Capacity", "Syllabus", "Emrollment open?"
            }
        ));
        jScrollPane1.setViewportView(tblCourseDetails);

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnOpenEnrollment.setText("Open Enrollment");
        btnOpenEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenEnrollmentActionPerformed(evt);
            }
        });

        btnCloseEnrollment.setText("Close Enrollment");
        btnCloseEnrollment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseEnrollmentActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(btnBack)
                .addGap(140, 140, 140)
                .addComponent(lblManageCourses)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(423, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnUpdate)
                        .addGap(204, 204, 204))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnOpenEnrollment)
                        .addGap(71, 71, 71)
                        .addComponent(btnCloseEnrollment)))
                .addGap(53, 53, 53))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(17, 17, 17))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblManageCourses)
                    .addComponent(btnBack))
                .addGap(90, 90, 90)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdate)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOpenEnrollment)
                    .addComponent(btnCloseEnrollment))
                .addContainerGap(126, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        CardSequencePanel.remove(this);
        ((java.awt.CardLayout) CardSequencePanel.getLayout()).previous(CardSequencePanel);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        int row = tblCourseDetails.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course row to update.");
            return;
        }
        CourseOffer co = selectedOffer();
        if (co == null) return;

        DefaultTableModel dtm = (DefaultTableModel) tblCourseDetails.getModel();

        // Read fields from table
        String newTitle = String.valueOf(dtm.getValueAt(row, 1));
        String newDesc  = String.valueOf(dtm.getValueAt(row, 2));
        String newSched = String.valueOf(dtm.getValueAt(row, 3));
        int newCap      = parseIntSafe(dtm.getValueAt(row, 4), (co.getSeatList()!=null?co.getSeatList().size():0));
        String newSyll  = String.valueOf(dtm.getValueAt(row, 5));

        // Persist to sidecars (title can't be set on Course in your model; we keep it as a label)
        DESCRIPTION_STORE.put(co, newDesc);
SCHEDULE_STORE.put(co, newSched);
SYLLABUS_STORE.put(co, newSyll);

        // Capacity -> seats (grow or trim if not occupied)
        int currentCap = (co.getSeatList() != null) ? co.getSeatList().size() : 0;
        if (newCap > currentCap) {
            co.generatSeats(newCap - currentCap);
        } else if (newCap < currentCap) {
            ArrayList<info5100.university.example.CourseSchedule.Seat> seats = co.getSeatList();
            for (int i = seats.size()-1; i >= 0 && seats.size() > newCap; i--) {
                if (!seats.get(i).isOccupied()) seats.remove(i);
                else break;
            }
        }

        JOptionPane.showMessageDialog(this, "Course details updated.");
        populateTable();
        // Reselect same row if still present
        if (row < tblCourseDetails.getRowCount()) tblCourseDetails.setRowSelectionInterval(row, row);
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnOpenEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenEnrollmentActionPerformed
        // TODO add your handling code here:
        CourseOffer co = selectedOffer();
        if (co == null) {
            JOptionPane.showMessageDialog(this, "Select a course row first.");
            return;
        }
        OPEN_STORE.put(co, Boolean.TRUE);
        updateOpenFlagInTable(true);
    }//GEN-LAST:event_btnOpenEnrollmentActionPerformed

    private void btnCloseEnrollmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseEnrollmentActionPerformed
        // TODO add your handling code here:
        CourseOffer co = selectedOffer();
        if (co == null) {
            JOptionPane.showMessageDialog(this, "Select a course row first.");
            return;
        }
        OPEN_STORE.put(co, Boolean.FALSE);
        updateOpenFlagInTable(false);
    }//GEN-LAST:event_btnCloseEnrollmentActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCloseEnrollment;
    private javax.swing.JButton btnOpenEnrollment;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblManageCourses;
    private javax.swing.JTable tblCourseDetails;
    // End of variables declaration//GEN-END:variables
}
