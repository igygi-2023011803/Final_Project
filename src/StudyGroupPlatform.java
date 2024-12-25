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
            System.out.println("Reading line: " + line);
            String[] data = line.split(",");
            StudyGroup group = new StudyGroup(data[0], data[1]);
            groups.add(group);
        }
        reader.close();
        return groups;
    }

    public void writeStudyGroups(List<StudyGroup> groups) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (StudyGroup group : groups) {
            System.out.println("Writing group: " + group);
            writer.write(group.groupName + "," + group.subject);
            writer.newLine();
        }
        writer.close();
    }
}

// 메인 클래스
public class StudyGroupPlatform {
    private List<StudyGroup> studyGroups;
    private Map<String, List<StudyGroup>> studyGroupMap;
    private Set<String> groupNameSet;
    private CSVHandler csvHandler;
    private JList<StudyGroup> groupList;
    private DefaultListModel<StudyGroup> listModel;
    private JTextField searchField;

    public StudyGroupPlatform() {
        System.out.println("Initializing StudyGroupPlatform...");
        csvHandler = new CSVHandler("study_groups.csv");
        studyGroups = new ArrayList<>();
        studyGroupMap = new HashMap<>();
        groupNameSet = new HashSet<>();
        setupGUI();
        loadStudyGroups();
    }

    private void loadStudyGroups() {
        try {
            studyGroups = csvHandler.readStudyGroups();
            for (StudyGroup group : studyGroups) {
                System.out.println("Loaded group: " + group);
                listModel.addElement(group);
                groupNameSet.add(group.groupName);
                studyGroupMap.computeIfAbsent(group.subject, k -> new ArrayList<>()).add(group);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "CSV 파일을 읽는 중 오류 발생: " + e.getMessage());
        }
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

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("과목 검색:"));
        searchField = new JTextField(10);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("검색");
        searchPanel.add(searchButton);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        JButton joinButton = new JButton("그룹 참가");
        JButton deleteButton = new JButton("그룹 삭제");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(joinButton);
        buttonPanel.add(deleteButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        frame.add(splitPane);
        frame.setVisible(true);

        searchButton.addActionListener(e -> {
            String subject = searchField.getText();
            listModel.clear();
            if (studyGroupMap.containsKey(subject)) {
                for (StudyGroup group : studyGroupMap.get(subject)) {
                    listModel.addElement(group);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "해당 과목의 그룹이 없습니다.");
            }
        });
    }

    public static void main(String[] args) {
        new StudyGroupPlatform();
    }
}
