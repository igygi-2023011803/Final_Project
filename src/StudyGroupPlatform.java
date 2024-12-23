import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
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
        return groupName + " : " + subject;
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
    private Map<String, List<StudyGroup>> studyGroupMap;
    private CSVHandler csvHandler;
    private JList<StudyGroup> groupList;
    private DefaultListModel<StudyGroup> listModel;
    private JTextField searchField;

    public StudyGroupPlatform() {
        csvHandler = new CSVHandler("study_groups.csv");
        studyGroups = new ArrayList<>();
        studyGroupMap = new HashMap<>();
        setupGUI();
        loadStudyGroups();
    }

    private void setupGUI() {
        JFrame frame = new JFrame("스터디 그룹 플랫폼");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);

        // 왼쪽 패널 (그룹 생성)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(6, 2));
        leftPanel.add(new JLabel("이름:"));
        JTextField nameField = new JTextField();
        leftPanel.add(nameField);
        leftPanel.add(new JLabel("학번:"));
        JTextField idField = new JTextField();
        leftPanel.add(idField);
        leftPanel.add(new JLabel("그룹명:"));
        JTextField groupNameField = new JTextField();
        leftPanel.add(groupNameField);
        leftPanel.add(new JLabel("과목:"));
        JTextField subjectField = new JTextField();
        leftPanel.add(subjectField);
        JButton createButton = new JButton("그룹 생성");
        leftPanel.add(createButton);

        // 오른쪽 패널 (그룹 리스트 및 검색)
        JPanel rightPanel = new JPanel(new BorderLayout());
        listModel = new DefaultListModel<>();
        groupList = new JList<>(listModel);
        rightPanel.add(new JScrollPane(groupList), BorderLayout.CENTER);
        JButton joinButton = new JButton("그룹 참가");
        rightPanel.add(joinButton, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("과목 검색:"), BorderLayout.WEST);
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        JButton searchButton = new JButton("검색");
        searchPanel.add(searchButton, BorderLayout.EAST);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        frame.add(splitPane);
        frame.setVisible(true);

        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String studentId = idField.getText();
            String groupName = groupNameField.getText();
            String subject = subjectField.getText();

            if (!name.isEmpty() && !studentId.isEmpty() && !groupName.isEmpty() && !subject.isEmpty()) {
                StudyGroup newGroup = new StudyGroup(groupName, subject);
                studyGroups.add(newGroup);
                studyGroupMap.computeIfAbsent(subject, k -> new ArrayList<>()).add(newGroup);
                listModel.addElement(newGroup);
                try {
                    csvHandler.writeStudyGroup(newGroup);
                    JOptionPane.showMessageDialog(frame, "그룹이 생성되었습니다.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "오류 발생: " + ex.getMessage());
                }
            }
        });

        joinButton.addActionListener(e -> {
            StudyGroup selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                JTextField userName = new JTextField();
                JTextField userId = new JTextField();
                JPanel panel = new JPanel(new GridLayout(2, 2));
                panel.add(new JLabel("이름:"));
                panel.add(userName);
                panel.add(new JLabel("학번:"));
                panel.add(userId);
                int result = JOptionPane.showConfirmDialog(frame, panel, "그룹 참가", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    selectedGroup.addMember(new Student(userName.getText(), userId.getText()));
                    JOptionPane.showMessageDialog(frame, "그룹에 참가했습니다.");
                }
            }
        });
    }

    private void loadStudyGroups() {
        try {
            studyGroups = csvHandler.readStudyGroups();
            for (StudyGroup group : studyGroups) {
                studyGroupMap.computeIfAbsent(group.subject, k -> new ArrayList<>()).add(group);
                listModel.addElement(group);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new StudyGroupPlatform();
    }
}
