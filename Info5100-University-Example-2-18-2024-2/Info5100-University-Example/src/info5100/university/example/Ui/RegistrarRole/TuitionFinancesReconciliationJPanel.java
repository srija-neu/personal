/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package info5100.university.example.Ui.RegistrarRole;

import info5100.university.example.Context.UniversityContext;
import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.CourseSchedule.CourseLoad;
import info5100.university.example.CourseSchedule.CourseOffer;
import info5100.university.example.CourseSchedule.CourseSchedule;
import info5100.university.example.CourseSchedule.SeatAssignment;
import info5100.university.example.Finance.StudentLedger;
import info5100.university.example.Persona.StudentDirectory;
import info5100.university.example.Persona.StudentProfile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Srija
 */
public class TuitionFinancesReconciliationJPanel extends javax.swing.JPanel {

    private final JPanel CardSequencePanel;
    private final UniversityContext ctx;
    /**
     * Creates new form TuitionFinancesReconciliationJPanel
     */
    public TuitionFinancesReconciliationJPanel(UniversityContext ctx, JPanel cardPanel) {
        initComponents();
        this.ctx = ctx;
        this.CardSequencePanel = cardPanel;

        populateSemesters();
        refreshTables();
    }
    
    private void populateSemesters() {
        cmbSemester.removeAllItems();
        for (String term : ctx.getAllSchedules().keySet()) {
            cmbSemester.addItem(term);
        }
    }

    private static String safe(String s) { return (s == null) ? "" : s; }

    // --------- Tuition calculation (model-safe) ---------

    /** Tuition for a single student in a semester, using Course.getCoursePrice(). */
    private int calcTuitionFor(StudentProfile sp, String semester) {
        if (sp == null || semester == null) return 0;
        CourseLoad cl = sp.getCourseLoadBySemester(semester);
        if (cl == null) return 0;

        int tuition = 0;
        for (SeatAssignment sa : cl.getSeatAssignments()) {
            CourseOffer co = sa.getCourseOffer();
            if (co == null) continue;
            Course c = co.getSubjectCourse();
            if (c == null) continue;
            tuition += c.getCoursePrice(); // price * credits
        }
        return tuition;
    }

    /** Try to read payments via ctx.getLedger() if a suitable method exists; else 0. */
    private int calcPaidFor(StudentProfile sp, String semester) {
        try {
            StudentLedger ledger = ctx.getLedger();
            if (ledger == null) return 0;

            // Try common method shapes with reflection to avoid hard dependency
            // 1) int getTotalPaid(String studentId, String semester)
            Method m1 = null;
            try { m1 = ledger.getClass().getMethod("getTotalPaid", String.class, String.class); } catch (Exception ignore) {}
            if (m1 != null) {
                String sid = (sp.getPerson() != null) ? sp.getPerson().getUniversityId() : null;
                Object v = m1.invoke(ledger, sid, semester);
                return (v instanceof Integer) ? (Integer) v : 0;
            }

            // 2) int getTotalPaid(String studentId)   // no semester breakdown
            Method m2 = null;
            try { m2 = ledger.getClass().getMethod("getTotalPaid", String.class); } catch (Exception ignore) {}
            if (m2 != null) {
                String sid = (sp.getPerson() != null) ? sp.getPerson().getUniversityId() : null;
                Object v = m2.invoke(ledger, sid);
                return (v instanceof Integer) ? (Integer) v : 0;
            }

            // 3) Map<?,?> paymentsFor(String studentId, String semester) returning amounts
            Method m3 = null;
            try { m3 = ledger.getClass().getMethod("paymentsFor", String.class, String.class); } catch (Exception ignore) {}
            if (m3 != null) {
                String sid = (sp.getPerson() != null) ? sp.getPerson().getUniversityId() : null;
                Object v = m3.invoke(ledger, sid, semester);
                if (v instanceof Map) {
                    int sum = 0;
                    for (Object val : ((Map<?, ?>) v).values()) {
                        if (val instanceof Number) sum += ((Number) val).intValue();
                    }
                    return sum;
                }
            }
        } catch (Exception ignore) {}
        return 0;
    }

    // --------- Table population ---------

    private void refreshTables() {
        String term = (String) cmbSemester.getSelectedItem();
        if (term == null) return;

        // Per-student balances
        DefaultTableModel dtmBalances = (DefaultTableModel) tblStudentBalances.getModel();
        dtmBalances.setRowCount(0);

        StudentDirectory sd = ctx.getDepartment().getStudentDirectory();
        int totalTuition = 0;
        int totalPaid    = 0;

        for (StudentProfile sp : sd.getStudentList()) {
            if (sp == null || sp.getPerson() == null) continue;

            String sid   = safe(sp.getPerson().getUniversityId());
            String sname = safe(sp.getName());

            int tuition = calcTuitionFor(sp, term);
            int paid    = calcPaidFor(sp, term);

            if (tuition == 0 && paid == 0) continue; // not enrolled this term

            totalTuition += tuition;
            totalPaid    += paid;

            dtmBalances.addRow(new Object[]{
                    sid, sname, term, tuition, paid
            });
        }

        // By-department revenue (single department in your model)
        DefaultTableModel dtmDept = (DefaultTableModel) tblCourseRevenue.getModel();
        dtmDept.setRowCount(0);

        String deptName = ctx.getDepartment().getName();
        int unpaid = Math.max(0, totalTuition - totalPaid);
        int revenue = totalPaid; // collected = realized revenue
        dtmDept.addRow(new Object[]{ deptName, totalPaid, unpaid, revenue });
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        jScrollPaneBalances = new javax.swing.JScrollPane();
        tblStudentBalances = new javax.swing.JTable();
        jScrollPaneCourseRev = new javax.swing.JScrollPane();
        tblCourseRevenue = new javax.swing.JTable();
        lblSemester = new javax.swing.JLabel();
        cmbSemester = new javax.swing.JComboBox<>();
        btnRefresh = new javax.swing.JButton();

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        tblStudentBalances.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Student ID", "Student Name", "Semester", "Tuition ", "Paid"
            }
        ));
        jScrollPaneBalances.setViewportView(tblStudentBalances);

        tblCourseRevenue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Department", "Total tuition collected", "unpaid tuition", "Revenue"
            }
        ));
        jScrollPaneCourseRev.setViewportView(tblCourseRevenue);

        lblSemester.setText("Semester");

        cmbSemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPaneBalances, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(17, 17, 17)
                            .addComponent(jScrollPaneCourseRev, javax.swing.GroupLayout.PREFERRED_SIZE, 626, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(153, 153, 153)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblSemester)
                                    .addGap(34, 34, 34)
                                    .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(btnRefresh))
                            .addGap(88, 88, 88)
                            .addComponent(btnBack))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSemester)
                    .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnBack))
                .addGap(38, 38, 38)
                .addComponent(jScrollPaneBalances, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPaneCourseRev, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        refreshTables();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        CardSequencePanel.remove(this);
        ((java.awt.CardLayout) CardSequencePanel.getLayout()).previous(CardSequencePanel);
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JComboBox<String> cmbSemester;
    private javax.swing.JScrollPane jScrollPaneBalances;
    private javax.swing.JScrollPane jScrollPaneCourseRev;
    private javax.swing.JLabel lblSemester;
    private javax.swing.JTable tblCourseRevenue;
    private javax.swing.JTable tblStudentBalances;
    // End of variables declaration//GEN-END:variables
}
