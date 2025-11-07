/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package info5100.university.example.Ui.StudentRole;

import info5100.university.example.Context.UniversityContext;
import info5100.university.example.CourseCatalog.Course;
import info5100.university.example.CourseSchedule.CourseLoad;
import info5100.university.example.CourseSchedule.CourseOffer;
import info5100.university.example.CourseSchedule.CourseSchedule;
import info5100.university.example.CourseSchedule.SeatAssignment;
import info5100.university.example.Persona.Person;
import info5100.university.example.Persona.StudentDirectory;
import info5100.university.example.Persona.StudentProfile;
import info5100.university.example.Persona.UserAccount;
import info5100.university.example.Persona.Faculty.FacultyProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author Vaishu
 */
public class RegistrationJPanel extends javax.swing.JPanel {
    private UniversityContext ctx;
    private javax.swing.JPanel cardPanel;
    private UserAccount account;


    private CourseSchedule schedule;             // current term schedule
    private String currentTerm = "Item 1";       // mirrors cmbSemester selection
    private List<CourseOffer> filteredOffers = new ArrayList<>();

    /**
     * Creates new form RegistrationJPanel
     */

   
public RegistrationJPanel() {
        initComponents();
        // small defaults, you can customize the terms you actually have
        cmbSemester.removeAllItems();
        cmbSemester.addItem("Item 1");
        cmbSemester.addItem("Item 2");

        cmbSearchBy.removeAllItems();
        cmbSearchBy.addItem("Course ID");
        cmbSearchBy.addItem("Course Name");
        cmbSearchBy.addItem("Instructor");
        cmbSearchBy.addItem("Time");

        tblOffers.getSelectionModel().addListSelectionListener(e ->
                btnEnroll.setEnabled(tblOffers.getSelectedRow() >= 0));
        tblSchedule.getSelectionModel().addListSelectionListener(e ->
                btnDrop.setEnabled(tblSchedule.getSelectedRow() >= 0));
    }

    public RegistrationJPanel(UniversityContext ctx, javax.swing.JPanel cardPanel, UserAccount account) {
        this();
        this.ctx = ctx;
        this.cardPanel = cardPanel;
        this.account = account;
        refreshAll();
    }



// --- tiny safe string helper
// --- tiny safe string helper ---
private static String S(Object v) { return v == null ? "" : String.valueOf(v); }

// --- simple, Project-friendly accessors (only use CourseOffer APIs) ---
private String courseIdOf(CourseOffer co) {
    try { return S(co.getCourseNumber()); } catch (Exception ignore) {}
    try { return S(co.getCourseId()); }     catch (Exception ignore) {}
    return "";
}

private String courseNameOf(CourseOffer co) {
    try { return S(co.getCourseName()); } catch (Exception ignore) {}
    // fall back to id if there’s no separate name
    String id = courseIdOf(co);
    return id.isEmpty() ? "" : id;
}

private String creditsOf(CourseOffer co) {
    try { return S(co.getCreditHours()); } catch (Exception ignore) {}
    return "";
}

private String instructorOf(CourseOffer co) {
    try { return S(co.getInstructor()); } catch (Exception ignore) {}
    try {
        // secondary path via FacultyProfile → Person → Name
        FacultyProfile fp = co.getFacultyProfile();     
        if (fp != null && fp.getPerson() != null) return S(fp.getPerson().getName());
    } catch (Exception ignore) {}
    return "";
}

private String capacityOf(CourseOffer co) {
    try { return S(co.getCapacity()); }     catch (Exception ignore) {}
    try { return S(co.getSeatCapacity()); } catch (Exception ignore) {}
    return "";
}

private String enrolledOf(CourseOffer co) {
    try { return S(co.getEnrolledCount()); } catch (Exception ignore) {}
    try { return S(co.getEnrollment()); }    catch (Exception ignore) {}
    return "0";
}

private String roomOf(CourseOffer co) {
    try { return S(co.getRoom()); }     catch (Exception ignore) {}
    try { return S(co.getLocation()); } catch (Exception ignore) {}
    try { return S(co.getRoomLabel()); } catch (Exception ignore) {}
    return "";
}

private String timeOf(CourseOffer co) {
    try { return S(co.getTime()); }      catch (Exception ignore) {}
    try {
        String d = S(co.getDay());
        String t = S(co.getTimeSlot());
        return (d + " " + t).trim();
    } catch (Exception ignore) {}
    return "";
}

private boolean seatsAvailable(CourseOffer co) {
    try { return co.getAvailableSeats() > 0; } catch (Exception ignore) {}
    try {
        int cap = Integer.parseInt(capacityOf(co));
        int enr = Integer.parseInt(enrolledOf(co));
        return cap > enr;
    } catch (Exception ignore) {}
    return true;
}

private void loadOffersTable() {
    String mode = (String) cmbSearchBy.getSelectedItem();
    String q = txtSearchQuery.getText();
    q = (q == null) ? "" : q.trim().toLowerCase();

    // Get all offers for the currently selected term
    // Replace offersForTerm() with your own source if needed:
    //   List<CourseOffer> all = (schedule != null) ? schedule.getCourseOffers() : Collections.emptyList();
    List<CourseOffer> all = (schedule != null) ? schedule.getCourseOffers() : java.util.Collections.emptyList();
    if (all == null) all = java.util.Collections.emptyList();

    // Filter
    List<CourseOffer> filtered = new java.util.ArrayList<>();
    for (CourseOffer co : all) {
        if (co == null) continue;

        boolean keep = q.isEmpty();
        if (!keep) {
            String cid   = courseIdOf(co).toLowerCase();
            String cname = courseNameOf(co).toLowerCase();
            String instr = instructorOf(co).toLowerCase();
            String time  = timeOf(co).toLowerCase();

            if ("Course ID".equals(mode)      && cid.contains(q))   keep = true;
            else if ("Course Name".equals(mode) && cname.contains(q)) keep = true;
            else if ("Instructor".equals(mode) && instr.contains(q)) keep = true;
            else if ("Time".equals(mode)        && time.contains(q))  keep = true;
        }
        if (keep) filtered.add(co);
    }

    // Table model (non-editable)
    DefaultTableModel m = new DefaultTableModel(
        new Object[] { "Course ID", "Course Name", "Instructor", "Credits",
                       "Capacity", "Enrolled", "Room", "Time", "Status" }, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    // Rows
    for (CourseOffer co : filtered) {
        m.addRow(new Object[] {
            courseIdOf(co),
            courseNameOf(co),
            instructorOf(co),
            creditsOf(co),
            capacityOf(co),
            enrolledOf(co),
            roomOf(co),
            timeOf(co),
            seatsAvailable(co) ? "Open" : "Closed"
        });
    }

    tblOffers.setModel(m);
}

private void loadMyScheduleTable() {
    DefaultTableModel m = new DefaultTableModel(
            new Object[] { "Course ID", "Course Name", "Semester", "Credits", "Status" }, 0
    ) { public boolean isCellEditable(int r, int c) { return false; } };

    StudentProfile sp = student();
    if (sp != null) {
        try {
            // get courseload for the selected term (handles both method names)
            CourseLoad cl = null;
            try { cl = sp.getCourseLoadBySemester(currentTerm); } catch (Exception ignore) {}
            if (cl == null) { try { cl = sp.getCourseLoadBySemester(currentTerm); } catch (Exception ignore) {} }
            if (cl == null) { try { cl = sp.getCurrentCourseLoad(); } catch (Exception ignore) {} }

            if (cl != null) {
                for (SeatAssignment sa : cl.getSeatAssignments()) {
                    CourseOffer co = sa.getCourseOffer();
                    m.addRow(new Object[] {
                            courseIdOf(co),
                            courseNameOf(co),
                            currentTerm,
                            creditsOf(co),
                            "Enrolled"
                    });
                }
            }
        } catch (Exception ignore) { /* empty is fine */ }
    }

    tblSchedule.setModel(m);
    btnDrop.setEnabled(false);   // re-enable when a row is selected
}

    private void refreshAll() {
        loadOffersTable();
        loadMyScheduleTable();
    }

// --- search matching ---
 private boolean matches(CourseOffer co, String by, String q) {
        String cid   = courseIdOf(co).toLowerCase();
        String cname = courseNameOf(co).toLowerCase();
        String instr = instructorOf(co).toLowerCase();
        String time  = timeOf(co).toLowerCase();

        if ("Course ID".equals(by))   return cid.contains(q);
        if ("Course Name".equals(by)) return cname.contains(q);
        if ("Instructor".equals(by))  return instr.contains(q);
        if ("Time".equals(by))        return time.contains(q);
        return true;
    }





private static String safe(java.util.concurrent.Callable<String> c) {
    try { String s = c.call(); return s == null ? "" : s; } catch (Exception e) { return ""; }
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
        lblSearchBy = new javax.swing.JLabel();
        cmbSearchBy = new javax.swing.JComboBox<>();
        txtSearchQuery = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        scrOffers = new javax.swing.JScrollPane();
        tblOffers = new javax.swing.JTable();
        btnEnroll = new javax.swing.JButton();
        btnDrop = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        scrSchedule = new javax.swing.JScrollPane();
        tblSchedule = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 204, 204));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 20)); // NOI18N
        jLabel1.setText("Course Registeration");

        lblSemester.setText("Semester");

        cmbSemester.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblSearchBy.setText("Search By");

        cmbSearchBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Course ID", "Instructor", "Course Name", "Time" }));
        cmbSearchBy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbSearchByActionPerformed(evt);
            }
        });

        txtSearchQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchQueryActionPerformed(evt);
            }
        });

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnClear.setText("Clear");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        tblOffers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Name", "Instructor", "Credits", "Capacity", "Enrolled", "Room", "Time", "Status"
            }
        ));
        tblOffers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrOffers.setViewportView(tblOffers);
        if (tblOffers.getColumnModel().getColumnCount() > 0) {
            tblOffers.getColumnModel().getColumn(2).setHeaderValue("Instructor");
            tblOffers.getColumnModel().getColumn(4).setHeaderValue("Capacity");
            tblOffers.getColumnModel().getColumn(5).setHeaderValue("Enrolled");
            tblOffers.getColumnModel().getColumn(6).setHeaderValue("Room");
            tblOffers.getColumnModel().getColumn(7).setHeaderValue("Time");
        }

        btnEnroll.setText("Enroll");
        btnEnroll.setEnabled(false);
        btnEnroll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnrollActionPerformed(evt);
            }
        });

        btnDrop.setText("Drop");
        btnDrop.setEnabled(false);
        btnDrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDropActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.setEnabled(false);
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnBack.setText("<<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        tblSchedule.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Course ID", "Course Name", "Semester", "Credits", "Status"
            }
        ));
        tblSchedule.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrSchedule.setViewportView(tblSchedule);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnEnroll)
                .addGap(380, 380, 380))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(14, 14, 14)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblSemester)
                                            .addComponent(lblSearchBy))
                                        .addGap(23, 23, 23)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(cmbSemester, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cmbSearchBy, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                    .addComponent(btnBack))
                                .addGap(86, 86, 86)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtSearchQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(btnSearch)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btnClear))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel1))))
                            .addComponent(scrOffers, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scrSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(353, 353, 353)
                        .addComponent(btnDrop)
                        .addGap(18, 18, 18)
                        .addComponent(btnRefresh)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnBack))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearchQuery, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSemester)
                        .addComponent(cmbSemester, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSearch)
                        .addComponent(btnClear)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearchBy)
                    .addComponent(cmbSearchBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(33, 33, 33)
                .addComponent(scrOffers, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEnroll)
                .addGap(43, 43, 43)
                .addComponent(scrSchedule, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDrop)
                    .addComponent(btnRefresh))
                .addGap(70, 70, 70))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchQueryActionPerformed
        // TODO add your handling code here:
        loadOffersTable();
    }//GEN-LAST:event_txtSearchQueryActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
      if (cardPanel != null && cardPanel.getLayout() instanceof java.awt.CardLayout) {
            ((java.awt.CardLayout) cardPanel.getLayout()).previous(cardPanel);
        }
    }//GEN-LAST:event_btnBackActionPerformed

    private void cmbSearchByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSearchByActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbSearchByActionPerformed

    private void btnEnrollActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnrollActionPerformed
                                       
    // must select a row in the top (offers) table
    int row = tblOffers.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Select a course to enroll.");
        return;
    }

    // resolve the chosen offer (you already keep this list when loading the table)
    if (filteredOffers == null || row >= filteredOffers.size()) {
        JOptionPane.showMessageDialog(this, "Offer not found.");
        return;
    }
    CourseOffer chosen = filteredOffers.get(row);

    // seat availability
    try {
        if (chosen.getAvailableSeats() <= 0) {
            JOptionPane.showMessageDialog(this, "Section is full.");
            return;
        }
    } catch (Exception ignore) {
        // if the method isn't available, fall through and try to enroll anyway
    }

    // get the logged-in student's profile
    StudentProfile sp = student();
    if (sp == null) {
        JOptionPane.showMessageDialog(this, "No student profile linked to this account.");
        return;
    }

   // ---- enforce 8-credit limit ----
int current = 0;
try {
    CourseLoad clNow = sp.getCurrentCourseLoad(); // ✅ use existing method instead of getCourseLoad()
    if (clNow != null) {
        for (SeatAssignment sa : clNow.getSeatAssignments()) {
            try {
                CourseOffer co = sa.getCourseOffer();
                if (co != null) current += co.getCreditHours();
            } catch (Exception ignore) {}
        }
    }
} catch (Exception ignore) {}

int add = 0;
try { 
    add = Integer.parseInt(creditsOf(chosen)); 
} catch (Exception e) { 
    add = 0; 
}
if (current + add > 8) {
    JOptionPane.showMessageDialog(this, 
        "Cannot enroll: " + current + " + " + add + " exceeds 8 credits.");
    return;
}

    // --- attempt enrollment ---
    try {
        CourseLoad cl = sp.getCourseLoadBySemester(currentTerm);
        if (cl == null) {
            JOptionPane.showMessageDialog(this, "No course load for term: " + currentTerm);
            return;
        }
        SeatAssignment sa = chosen.assignEmptySeat(cl);
        if (sa == null) {
            JOptionPane.showMessageDialog(this, "No seat available.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Enrolled in " + courseIdOf(chosen));
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Enroll failed: " + ex.getMessage());
    }

    // refresh both tables
    refreshAll();

    }//GEN-LAST:event_btnEnrollActionPerformed

    private void btnDropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDropActionPerformed
      // must select a row in the bottom (schedule) table
    int row = tblSchedule.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this, "Select a scheduled course to drop.");
        return;
    }

    // course id shown in the first column of your schedule table
    String cid = String.valueOf(tblSchedule.getValueAt(row, 0));

    StudentProfile sp = student();
    if (sp == null) {
        JOptionPane.showMessageDialog(this, "No student profile linked.");
        return;
    }

    try {
        CourseLoad cl = sp.getCourseLoadBySemester(currentTerm);
        if (cl == null) {
            JOptionPane.showMessageDialog(this, "No courseload for term: " + currentTerm);
            return;
        }

        // locate the SeatAssignment for this course id
        SeatAssignment toDrop = null;
        CourseOffer offer = null;
        for (SeatAssignment sa : cl.getSeatAssignments()) {
            try {
                CourseOffer co = sa.getCourseOffer();
                if (co != null && cid.equals(courseIdOf(co))) {
                    toDrop = sa;
                    offer  = co;
                    break;
                }
            } catch (Exception ignore) {}
        }

        if (toDrop == null || offer == null) {
            JOptionPane.showMessageDialog(this, "Could not find enrollment.");
            return;
        }

        // free the seat (best-effort) and unlink from courseload
        boolean ok = false;
        try { ok = offer.dropStudent(toDrop); } catch (Exception ignore) {}
        try { cl.getSeatAssignments().remove(toDrop); } catch (Exception ignore) {}

        JOptionPane.showMessageDialog(this, (ok ? "Dropped " : "Dropped (best effort) ") + cid);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Drop failed: " + ex.getMessage());
    }

    // refresh both tables JOptionPane.showMessageDialog(this, "Drop failed: " + ex.getMessage());
        
        refreshAll();
    }//GEN-LAST:event_btnDropActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        loadOffersTable();
        loadMyScheduleTable();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        loadOffersTable();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        // TODO add your handling code here:
        txtSearchQuery.setText("");
        loadOffersTable();
    }//GEN-LAST:event_btnClearActionPerformed
  
    private StudentProfile student() {
    if (account == null || ctx == null) return null;

    Person p = account.getPerson();
    if (p == null) return null;

    try {
        StudentDirectory dir = ctx.getDepartment().getStudentDirectory();
        for (StudentProfile sp : dir.getStudentlist()) {
            if (sp.getPerson() == p) return sp;
            if (sp.getPerson() != null && p.getPersonId() != null &&
                sp.getPerson().getPersonId().equals(p.getPersonId())) {
                return sp;
            }
        }
    } catch (Exception e) {
        // fallback handling
    }

    // alternative lookups (covers most seed templates)
// alternative lookups (covers most seed templates)
// alternative lookups (covers most seed templates)
try { 
    return ctx.getDepartment().getStudentDirectory().findStudent(p.getPersonId()); 
} catch (Exception ignore) {}

try { 
    return ctx.getDepartment().getStudentDirectory().findStudent(account.getUserLoginName()); 
} catch (Exception ignore) {}

// final fallback
return null;}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDrop;
    private javax.swing.JButton btnEnroll;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cmbSearchBy;
    private javax.swing.JComboBox<String> cmbSemester;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblSearchBy;
    private javax.swing.JLabel lblSemester;
    private javax.swing.JScrollPane scrOffers;
    private javax.swing.JScrollPane scrSchedule;
    private javax.swing.JTable tblOffers;
    private javax.swing.JTable tblSchedule;
    private javax.swing.JTextField txtSearchQuery;
    // End of variables declaration//GEN-END:variables
}
