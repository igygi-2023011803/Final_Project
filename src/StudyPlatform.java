import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

class Student implements Serializable {
    private String name;
    private String email;

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}

class StudyGroup implements Serializable {
    private String groupName;
    private String subject;
    private List<Student> members;

    public StudyGroup(String groupName, String subject) {
        this.groupName = groupName;
        this.subject = subject;
        this.members = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public String getSubject() {
        return subject;
    }

    public void addMember(Student student) {
        members.add(student);
    }

    public void removeMember(Student student) {
        members.remove(student);
    }

    public List<Student> getMembers() {
        return members;
    }

    @Override
    public String toString() {
        return groupName + " - " + subject + " (" + members.size() + "명)";
    }
}

public class StudyPlatform extends JFrame {
    private List<StudyGroup> studyGroups;
    private JTextArea displayArea;
    private JTextField nameField, emailField, groupNameField, subjectField;

    private final String DATA_FILE = "studyGroups.dat";

    public StudyPlatform() {
        super("스터디 플랫폼");
        studyGroups = new ArrayList<>();
        loadGroups();

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        inputPanel.add(new JLabel("이름:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("이메일:"));
        emailField = new JTextField();
        inputPanel.add(emailField);

        inputPanel.add(new JLabel("그룹 이름:"));
        groupNameField = new JTextField();
        inputPanel.add(groupNameField);

        inputPanel.add(new JLabel("과목:"));
        subjectField = new JTextField();
        inputPanel.add(subjectField);

        add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();

        JButton createButton = new JButton("스터디 그룹 생성");
        JButton joinButton = new JButton("그룹 참여");
        JButton viewButton = new JButton("그룹 보기");

        buttonPanel.add(createButton);
        buttonPanel.add(joinButton);
        buttonPanel.add(viewButton);

        add(buttonPanel, BorderLayout.CENTER);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.SOUTH);

        createButton.addActionListener(e -> createStudyGroup());
        joinButton.addActionListener(e -> joinStudyGroup());
        viewButton.addActionListener(e -> displayGroups());

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveGroups();
                System.exit(0);
            }
        });

        setSize(500, 600);
        setVisible(true);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void createStudyGroup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String groupName = groupNameField.getText().trim();
        String subject = subjectField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || groupName.isEmpty() || subject.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 필드를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "유효한 이메일 주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (StudyGroup group : studyGroups) {
            if (group.getGroupName().equalsIgnoreCase(groupName)) {
                JOptionPane.showMessageDialog(this, "이미 존재하는 그룹 이름입니다.", "입력 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Student student = new Student(name, email);
        StudyGroup group = new StudyGroup(groupName, subject);
        group.addMember(student);
        studyGroups.add(group);
        JOptionPane.showMessageDialog(this, "스터디 그룹이 생성되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        saveGroups();
    }

    private void joinStudyGroup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String groupName = groupNameField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || groupName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름, 이메일, 그룹 이름을 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "유효한 이메일 주소를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudyGroup selectedGroup = null;
        for (StudyGroup group : studyGroups) {
            if (group.getGroupName().equalsIgnoreCase(groupName)) {
                selectedGroup = group;
                break;
            }
        }

        if (selectedGroup != null) {
            Student student = new Student(name, email);
            selectedGroup.addMember(student);
            JOptionPane.showMessageDialog(this, "그룹에 참여하였습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            saveGroups();
        } else {
            JOptionPane.showMessageDialog(this, "그룹을 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayGroups() {
        StringBuilder sb = new StringBuilder();
        for (StudyGroup group : studyGroups) {
            sb.append(group.toString()).append("\n");
            for (Student member : group.getMembers()) {
                sb.append("  - ").append(member.toString()).append("\n");
            }
        }
        displayArea.setText(sb.toString());
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        groupNameField.setText("");
        subjectField.setText("");
    }

    @SuppressWarnings("unchecked")
    private void loadGroups() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            studyGroups = (List<StudyGroup>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            studyGroups = new ArrayList<>();
        }
    }

    private void saveGroups() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(studyGroups);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "데이터 저장에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new StudyPlatform();
    }
}
