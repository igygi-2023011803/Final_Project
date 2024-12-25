import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

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

class Student {
    String name;
    String studentId;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }
}

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

    public void writeStudyGroups(List<StudyGroup> groups) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (StudyGroup group : groups) {
            writer.write(group.groupName + "," + group.subject);
            writer.newLine();
        }
        writer.close();
    }
}

public class StudyGroupPlatform {
    private List<StudyGroup> studyGroups;
    private Map<String, List<StudyGroup>> studyGroupMap;
    private Set<String> groupNameSet;
    private CSVHandler csvHandler;
    private JList<StudyGroup> groupList;
    private DefaultListModel<StudyGroup> listModel;  // listModel을 클래스 수준에서 초기화
    private JTextField searchField;

    public StudyGroupPlatform() {
        // listModel을 초기화
        listModel = new DefaultListModel<>();
        csvHandler = new CSVHandler("study_groups.csv");
        studyGroups = new ArrayList<>();
        studyGroupMap = new HashMap<>();
        groupNameSet = new HashSet<>();
        loadStudyGroups(); // GUI가 로드되기 전에 그룹 데이터를 불러옵니다.
        setupGUI();
    }

    // 초기 실행 시 CSV 파일에서 그룹 목록을 불러옴
    private void loadStudyGroups() {
        try {
            studyGroups = csvHandler.readStudyGroups();
            for (StudyGroup group : studyGroups) {
                listModel.addElement(group); // JList에 그룹을 추가
                groupNameSet.add(group.groupName);
                studyGroupMap.computeIfAbsent(group.subject, k -> new ArrayList<>()).add(group);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "CSV 파일을 읽는 중 오류 발생: " + e.getMessage());
        }
    }

    private void setupGUI() {
        JFrame frame = new JFrame("스터디 그룹 플랫폼");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 배경색 설정
        frame.getContentPane().setBackground(new Color(245, 245, 245));

        // JSplitPane 대신 GridBagLayout 사용
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // 왼쪽 패널 (그룹 생성)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(6, 2, 10, 10));
        leftPanel.setBackground(new Color(255, 255, 255));
        leftPanel.setBorder(BorderFactory.createTitledBorder("스터디 그룹 생성"));

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
        rightPanel.setBackground(new Color(255, 255, 255));
        rightPanel.setBorder(BorderFactory.createTitledBorder("그룹 리스트"));

        groupList = new JList<>(listModel); // JList 초기화
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(groupList), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("과목 검색:"));
        searchField = new JTextField(10);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("검색");
        searchPanel.add(searchButton);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton joinButton = new JButton("그룹 참가");
        JButton deleteButton = new JButton("그룹 삭제");
        buttonPanel.add(joinButton);
        buttonPanel.add(deleteButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 레이아웃 설정
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(leftPanel, gbc);

        gbc.gridx = 1;
        mainPanel.add(rightPanel, gbc);

        frame.add(mainPanel);
        frame.setVisible(true);

        // 검색 버튼 액션
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

        // 그룹 생성 버튼 액션
        createButton.addActionListener(e -> {
            String name = nameField.getText();
            String studentId = idField.getText();
            String groupName = groupNameField.getText();
            String subject = subjectField.getText();

            if (name.isEmpty() || studentId.isEmpty() || groupName.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "모든 필드를 입력해주세요.");
                return;
            }

            if (groupNameSet.contains(groupName)) {
                JOptionPane.showMessageDialog(frame, "이미 존재하는 그룹명입니다.");
                return;
            }

            StudyGroup newGroup = new StudyGroup(groupName, subject);
            Student student = new Student(name, studentId);
            newGroup.addMember(student);

            studyGroups.add(newGroup);
            listModel.addElement(newGroup);
            groupNameSet.add(groupName);
            studyGroupMap.computeIfAbsent(subject, k -> new ArrayList<>()).add(newGroup);

            try {
                csvHandler.writeStudyGroups(studyGroups);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "스터디 그룹 저장 중 오류 발생: " + ex.getMessage());
            }

            nameField.setText("");
            idField.setText("");
            groupNameField.setText("");
            subjectField.setText("");
        });
    }

    public static void main(String[] args) {
        new StudyGroupPlatform();
    }
}


