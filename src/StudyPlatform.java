import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// 스터디 그룹을 표현하는 클래스
class StudyGroup {
    String groupName;
    String subject;
    List<Student> members;

    public StudyGroup(String groupName, String subject) {
        this.groupName = groupName;
        this.subject = subject;
        this.members = new ArrayList<>();
    }

    public void addMember(Student student) {
        members.add(student);
    }

    @Override
    public String toString() {
        return groupName; // 콤보박스에 표시되는 이름
    }
}

// 학생을 표현하는 클래스
class Student {
    String name;
    String studentId;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }
}

// CSV 파일을 처리하는 클래스
class CSVHandler {
    private final String filePath;

    public CSVHandler(String filePath) {
        this.filePath = filePath;
    }

    // CSV 파일에서 스터디 그룹 목록을 읽어오는 메서드
    public List<StudyGroup> readStudyGroups() throws IOException {
        List<StudyGroup> groups = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;

        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            StudyGroup group = new StudyGroup(data[0], data[1]);
            groups.add(group);
        }
        reader.close();
        return groups;
    }

    // 새로운 스터디 그룹을 CSV 파일에 쓰는 메서드
    public void writeStudyGroup(StudyGroup group) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
        writer.write(group.groupName + "," + group.subject);
        writer.newLine();
        writer.close();
    }
}

// 메인 클래스
public class StudyGroupPlatform {
    private List<StudyGroup> studyGroups;
    private CSVHandler csvHandler;
    private JComboBox<StudyGroup> groupSelector;
    private JTextField nameField;
    private JTextField idField;
    private JTextField subjectField;

    public StudyGroupPlatform() {
        csvHandler = new CSVHandler("study_groups.csv");
        studyGroups = new ArrayList<>();

        // GUI 구성
        setupGUI();
        loadStudyGroups();
    }

    // GUI 설정 메서드
    private void setupGUI() {
        JFrame frame = new JFrame("스터디 그룹 플랫폼");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        groupSelector = new JComboBox<>();
        frame.add(groupSelector, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));
        inputPanel.add(new JLabel("이름: "));
        nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("학번: "));
        idField = new JTextField();
        inputPanel.add(idField);
        inputPanel.add(new JLabel("원하는 과목: "));
        subjectField = new JTextField();
        inputPanel.add(subjectField);
        frame.add(inputPanel, BorderLayout.CENTER);

        JButton joinButton = new JButton("그룹 참가");
        joinButton.addActionListener(new JoinGroupAction());
        frame.add(joinButton, BorderLayout.SOUTH);

        JButton createButton = new JButton("그룹 생성");
        createButton.addActionListener(new CreateGroupAction());
        frame.add(createButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    // 스터디 그룹을 CSV에서 로드하는 메서드
    private void loadStudyGroups() {
        try {
            studyGroups = csvHandler.readStudyGroups();
            for (StudyGroup group : studyGroups) {
                groupSelector.addItem(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 그룹 참가 액션 리스너
    private class JoinGroupAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String studentId = idField.getText();
            StudyGroup selectedGroup = (StudyGroup) groupSelector.getSelectedItem();

            if (selectedGroup != null && !name.isEmpty() && !studentId.isEmpty()) {
                Student newStudent = new Student(name, studentId);
                selectedGroup.addMember(newStudent);
                try {
                    csvHandler.writeStudyGroup(selectedGroup);
                    JOptionPane.showMessageDialog(null, "그룹에 참가했습니다.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "오류 발생: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "모든 필드를 입력해주세요.");
            }
        }
    }

    // 그룹 생성 액션 리스너
    private class CreateGroupAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String studentId = idField.getText();
            String subject = subjectField.getText();

            if (!name.isEmpty() && !studentId.isEmpty() && !subject.isEmpty()) {
                StudyGroup newGroup = new StudyGroup(name, subject);
                try {
                    csvHandler.writeStudyGroup(newGroup);
                    studyGroups.add(newGroup);
                    groupSelector.addItem(newGroup);
                    JOptionPane.showMessageDialog(null, "그룹이 생성되었습니다.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "오류 발생: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "모든 필드를 입력해주세요.");
            }
        }
    }

    public static void main(String[] args) {
        new StudyGroupPlatform();
    }
}
