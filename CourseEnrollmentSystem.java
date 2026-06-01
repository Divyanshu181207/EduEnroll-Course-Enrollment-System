import javax.swing.*;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// -- Person (Abstract Base Class) --
abstract class Person {
    protected String id, name, email;
    Person(String id, String name, String email) { this.id=id; this.name=name; this.email=email; }
    public String getId()   { return id; }
    public String getName() { return name; }
    public abstract String getRole();
}

// -- Learner (Inherits Person) --
class Learner extends Person {
    List<String> enrolledCourseIds = new ArrayList<>();
    Learner(String id, String name, String email) { super(id, name, email); }
    public String getRole() { return "Learner"; }
}

// -- Course --
class Course {
    String id, title, instructor, category;
    int maxSeats, enrolled;
    Course(String id, String title, String instructor, int maxSeats, String category) {
        this.id=id; this.title=title; this.instructor=instructor;
        this.maxSeats=maxSeats; this.category=category; this.enrolled=0;
    }
    boolean isFull()  { return enrolled >= maxSeats; }
    int available()   { return maxSeats - enrolled; }
}

// -- Enrollment Record --
class Enrollment {
    static int seq = 100;
    String eid, learnerId, learnerName, courseId, courseTitle, date, status;
    Enrollment(Learner l, Course c) {
        eid = "ENR-"+(++seq); learnerId=l.getId(); learnerName=l.getName();
        courseId=c.id; courseTitle=c.title; status="Active";
        date = new java.text.SimpleDateFormat("dd-MM-yyyy").format(new Date());
    }
}

// -- Main Application --
public class CourseEnrollmentSystem extends JFrame {

    List<Learner>    learners    = new ArrayList<>();
    List<Course>     courses     = new ArrayList<>();
    List<Enrollment> enrollments = new ArrayList<>();

    static final Color BG   = new Color(18,24,48),   CARD = new Color(28,36,68),
                       ACC  = new Color(99,179,237),  ACC2 = new Color(72,219,151),
                       TXT  = new Color(220,228,255), TSEC = new Color(130,150,190),
                       DANG = new Color(245,101,101), BORD = new Color(45,55,95);

    DefaultTableModel courseModel, learnerModel, enrollModel;
    JComboBox<String> cmbLearner = new JComboBox<>(), cmbCourse = new JComboBox<>();
    JLabel statusBar;

    public CourseEnrollmentSystem() {
        super("EduEnroll - Course Enrollment System");
        seedData();
        setSize(1000, 650); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("  EduEnroll - Online Course Enrollment System");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18)); header.setForeground(ACC);
        header.setOpaque(true); header.setBackground(new Color(10,14,32));
        header.setPreferredSize(new Dimension(0,55));
        header.setBorder(BorderFactory.createMatteBorder(0,0,2,0,ACC));
        add(header, BorderLayout.NORTH);

        statusBar = new JLabel("  System ready.");
        statusBar.setFont(new Font("Segoe UI",Font.PLAIN,11)); statusBar.setForeground(TSEC);
        statusBar.setOpaque(true); statusBar.setBackground(new Color(8,12,25));
        statusBar.setPreferredSize(new Dimension(0,26));
        add(statusBar, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CARD); tabs.setForeground(TXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabs.addTab("Courses",  buildCoursesTab());
        tabs.addTab("Learners", buildLearnersTab());
        tabs.addTab("Enroll",   buildEnrollTab());
        tabs.addTab("Records",  buildRecordsTab());
        tabs.addChangeListener(e -> refreshAll());
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    JPanel buildCoursesTab() {
        courseModel = new DefaultTableModel(new String[]{"ID","Title","Instructor","Category","Seats","Enrolled","Free"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable t = mkTable(courseModel); refreshCourses();
        JPanel p = new JPanel(new BorderLayout(0,8)); p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JTextField fId=mkField("Course ID"), fTitle=mkField("Title"), fInst=mkField("Instructor"),
                   fSeats=mkField("Seats"), fCat=mkField("Category");
        JPanel form = new JPanel(new GridLayout(1,6,8,8)); form.setBackground(BG);
        form.add(fId); form.add(fTitle); form.add(fInst); form.add(fSeats); form.add(fCat);
        JButton add = mkBtn("+ Add Course", ACC2);
        add.addActionListener(e -> {
            if(fId.getText().trim().isEmpty() || fTitle.getText().trim().isEmpty()){status("Fill ID & Title!");return;}
            for(Course c:courses) if(c.id.equals(fId.getText().trim())){status("Course ID exists!");return;}
            int s=30; try{s=Integer.parseInt(fSeats.getText().trim());}catch(Exception ex){}
            courses.add(new Course(fId.getText().trim(),fTitle.getText().trim(),
                fInst.getText().trim(),s,fCat.getText().trim()));
            refreshAll(); status("Course added: "+fTitle.getText().trim());
        });
        form.add(add);
        p.add(form, BorderLayout.NORTH); p.add(mkScroll(t), BorderLayout.CENTER);
        return p;
    }

    JPanel buildLearnersTab() {
        learnerModel = new DefaultTableModel(new String[]{"ID","Name","Email","Role","Enrollments"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable t = mkTable(learnerModel); refreshLearners();
        JPanel p = new JPanel(new BorderLayout(0,8)); p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JTextField fId=mkField("Learner ID"), fName=mkField("Full Name"), fEmail=mkField("Email");
        JPanel form = new JPanel(new GridLayout(1,4,8,8)); form.setBackground(BG);
        form.add(fId); form.add(fName); form.add(fEmail);
        JButton add = mkBtn("+ Add Learner", ACC2);
        add.addActionListener(e -> {
            if(fId.getText().trim().isEmpty() || fName.getText().trim().isEmpty()){status("Fill ID & Name!");return;}
            for(Learner l:learners) if(l.id.equals(fId.getText().trim())){status("Learner ID exists!");return;}
            learners.add(new Learner(fId.getText().trim(),fName.getText().trim(),fEmail.getText().trim()));
            refreshAll(); status("Learner added: "+fName.getText().trim());
        });
        form.add(add);
        p.add(form, BorderLayout.NORTH); p.add(mkScroll(t), BorderLayout.CENTER);
        return p;
    }

    JPanel buildEnrollTab() {
        JPanel p = new JPanel(new GridBagLayout()); p.setBackground(BG);
        JPanel card = new JPanel(new GridBagLayout()); card.setBackground(CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORD,1),
            BorderFactory.createEmptyBorder(30,40,30,40)));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10,8,10,8); g.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel("Enroll a Learner into a Course");
        heading.setFont(new Font("Segoe UI",Font.BOLD,16)); heading.setForeground(ACC);
        JLabel res = new JLabel(" ");
        res.setFont(new Font("Segoe UI",Font.BOLD,13)); res.setHorizontalAlignment(SwingConstants.CENTER);

        cmbLearner.setPreferredSize(new Dimension(300,32));
        cmbCourse.setPreferredSize(new Dimension(300,32));

        g.gridx=0;g.gridy=0;g.gridwidth=2; card.add(heading,g);
        g.gridwidth=1;
        g.gridy=1;g.gridx=0; card.add(mkLabel("Select Learner:"),g); g.gridx=1; card.add(cmbLearner,g);
        g.gridy=2;g.gridx=0; card.add(mkLabel("Select Course:"),g);  g.gridx=1; card.add(cmbCourse,g);
        g.gridy=3;g.gridx=0;g.gridwidth=2; card.add(res,g);

        JButton btn = mkBtn("  Enroll Now  ", ACC2);
        btn.setFont(new Font("Segoe UI",Font.BOLD,14));
        btn.addActionListener(e -> {
            String ls=(String)cmbLearner.getSelectedItem(), cs=(String)cmbCourse.getSelectedItem();
            if(ls==null||cs==null){res.setForeground(DANG);res.setText("Select learner and course!");return;}
            Learner learner=null; for(Learner l:learners) if((l.id+" - "+l.name).equals(ls)){learner=l;break;}
            Course  course =null; for(Course  c:courses)  if((c.id+" - "+c.title).equals(cs)){course=c;break;}
            if(learner==null||course==null) return;
            if(course.isFull()){res.setForeground(DANG);res.setText("Course is full!");return;}
            for(Enrollment en:enrollments)
                if(en.learnerId.equals(learner.id)&&en.courseId.equals(course.id)&&en.status.equals("Active")){
                    res.setForeground(new Color(246,173,85));res.setText("Already enrolled!");return;}
            Enrollment en = new Enrollment(learner, course);
            enrollments.add(en); course.enrolled++; learner.enrolledCourseIds.add(course.id);
            res.setForeground(ACC2); res.setText("Enrolled! ID: "+en.eid);
            refreshAll(); status("Enrolled "+learner.name+" in "+course.title);
        });
        g.gridy=4; card.add(btn,g);
        p.add(card); return p;
    }

    JPanel buildRecordsTab() {
        enrollModel = new DefaultTableModel(new String[]{"Enroll ID","Learner","Course","Date","Status"},0){
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable t = mkTable(enrollModel); refreshEnrollments();
        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tb,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(tb,v,s,f,r,c);
                l.setForeground("Active".equals(v)?ACC2:DANG);
                l.setBackground(s?new Color(40,60,110):BG); l.setOpaque(true); return l;
            }
        });
        JPanel p = new JPanel(new BorderLayout(0,8)); p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JButton drop = mkBtn("Drop Selected Enrollment", DANG);
        drop.addActionListener(e -> {
            int row=t.getSelectedRow(); if(row<0){status("Select a record to drop.");return;}
            String eid=(String)enrollModel.getValueAt(row,0);
            for(Enrollment en:enrollments) if(en.eid.equals(eid)){
                if(!en.status.equals("Active")){status("Already dropped.");return;}
                int ok=JOptionPane.showConfirmDialog(this,"Drop "+eid+"?","Confirm",JOptionPane.YES_NO_OPTION);
                if(ok==JOptionPane.YES_OPTION){
                    en.status="Dropped";
                    for(Course c:courses)   if(c.id.equals(en.courseId)){c.enrolled--;break;}
                    for(Learner l:learners) if(l.id.equals(en.learnerId)){l.enrolledCourseIds.remove(en.courseId);break;}
                    refreshAll(); status("Dropped: "+eid);
                } return;
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT)); top.setBackground(BG);
        JLabel lbl = new JLabel("Enrollment Records");
        lbl.setFont(new Font("Segoe UI",Font.BOLD,14)); lbl.setForeground(TXT);
        top.add(lbl); top.add(drop);
        p.add(top, BorderLayout.NORTH); p.add(mkScroll(t), BorderLayout.CENTER);
        return p;
    }

    void refreshAll()         { refreshCourses(); refreshLearners(); refreshEnrollments(); refreshCombos(); }
    void refreshCourses()     { if(courseModel ==null)return; courseModel.setRowCount(0);
        for(Course c:courses)       courseModel.addRow(new Object[]{c.id,c.title,c.instructor,c.category,c.maxSeats,c.enrolled,c.available()}); }
    void refreshLearners()    { if(learnerModel==null)return; learnerModel.setRowCount(0);
        for(Learner l:learners)     learnerModel.addRow(new Object[]{l.id,l.name,l.email,l.getRole(),l.enrolledCourseIds.size()}); }
    void refreshEnrollments() { if(enrollModel ==null)return; enrollModel.setRowCount(0);
        for(Enrollment e:enrollments) enrollModel.addRow(new Object[]{e.eid,e.learnerName,e.courseTitle,e.date,e.status}); }
    void refreshCombos() {
        cmbLearner.removeAllItems(); for(Learner l:learners) cmbLearner.addItem(l.id+" - "+l.name);
        cmbCourse.removeAllItems();  for(Course  c:courses)  cmbCourse.addItem(c.id+" - "+c.title);
    }

    JTable mkTable(DefaultTableModel m) {
        JTable t = new JTable(m); t.setBackground(BG); t.setForeground(TXT); t.setRowHeight(28);
        t.setFont(new Font("Segoe UI",Font.PLAIN,12)); t.setGridColor(BORD);
        t.setSelectionBackground(new Color(40,60,110)); t.setSelectionForeground(ACC);
        t.getTableHeader().setBackground(CARD); t.getTableHeader().setForeground(TSEC);
        t.getTableHeader().setFont(new Font("Segoe UI",Font.BOLD,12));
        t.setShowVerticalLines(false); return t;
    }
    JScrollPane mkScroll(JTable t) {
        JScrollPane s = new JScrollPane(t); s.getViewport().setBackground(BG);
        s.setBorder(BorderFactory.createLineBorder(BORD)); return s;
    }
    JTextField mkField(String hint) {
        JTextField f = new JTextField(hint); f.setBackground(CARD); f.setForeground(TSEC);
        f.setCaretColor(TXT); f.setFont(new Font("Segoe UI",Font.PLAIN,12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORD), BorderFactory.createEmptyBorder(4,8,4,8)));
        f.addFocusListener(new java.awt.event.FocusAdapter(){
            public void focusGained(java.awt.event.FocusEvent e){ if(f.getText().equals(hint)){f.setText("");f.setForeground(TXT);} }
            public void focusLost (java.awt.event.FocusEvent e){ if(f.getText().isEmpty()){f.setText(hint);f.setForeground(TSEC);} }
        }); return f;
    }
    JButton mkBtn(String txt, Color col) {
        JButton b = new JButton(txt); b.setBackground(col.darker()); b.setForeground(col);
        b.setFont(new Font("Segoe UI",Font.BOLD,12)); b.setBorder(BorderFactory.createLineBorder(col));
        b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    JLabel mkLabel(String t) {
        JLabel l = new JLabel(t); l.setForeground(TSEC); l.setFont(new Font("Segoe UI",Font.PLAIN,12)); return l;
    }
    void status(String msg) { statusBar.setText("  " + msg); }

    void seedData() {
        courses.add(new Course("C001","Java Programming","Dr. Priya Sharma",30,"Programming"));
        courses.add(new Course("C002","Data Structures","Prof. Rahul Verma",25,"CS"));
        courses.add(new Course("C003","Web Development","Ms. Ananya Singh",40,"Web"));
        courses.add(new Course("C004","Machine Learning","Dr. Karan Mehta",20,"AI/ML"));
        learners.add(new Learner("L001","Aarav Patel","aarav@email.com"));
        learners.add(new Learner("L002","Diya Nair","diya@email.com"));
        learners.add(new Learner("L003","Rohan Gupta","rohan@email.com"));
        Enrollment e1=new Enrollment(learners.get(0),courses.get(0));
        enrollments.add(e1); courses.get(0).enrolled++; learners.get(0).enrolledCourseIds.add("C001");
        Enrollment e2=new Enrollment(learners.get(1),courses.get(1));
        enrollments.add(e2); courses.get(1).enrolled++; learners.get(1).enrolledCourseIds.add("C002");
    }

    public static void main(String[] args) {
        try{UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());}catch(Exception e){}
        SwingUtilities.invokeLater(CourseEnrollmentSystem::new);
    }
}
