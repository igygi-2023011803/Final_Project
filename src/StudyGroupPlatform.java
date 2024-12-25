import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * 스터디 그룹을 표현하는 클래스입니다.
 * 이 클래스는 그룹명, 과목명, 그리고 그룹에 속한 학생들을 관리합니다.
 */
class StudyGroup {
    String groupName;
    String subject;
    List<Student> members;

    /**
     * 스터디 그룹을 생성하는 생성자입니다.
     *
     * @param groupName 그룹명
     * @param subject 과목명
     */
    public StudyGroup(String groupName, String subject) {
        this.groupName = groupName;
        this.subject = subject;
        this.members = new ArrayList<>();
    }

    /**
     * 스터디 그룹에 학생을 추가합니다.
     *
     * @param student 추가할 학생 객체
     */
    public void addMember(Student student) {
        members.add(student);
    }

    /**
     * 그룹명과 과목명을 반환하는 메서드입니다.
     *
     * @return 스터디 그룹명과 과목을 문자열로 반환
     */
    @Override
    public String toString() {
        return groupName + " : " + subject;
    }
}

/**
 * 학생을 표현하는 클래스입니다.
 * 학생의 이름과 학번을 저장합니다.
 */
class Student {
    String name;
    String studentId;

    /**
     * 학생 객체를 생성하는 생성자입니다.
     *
     * @param name 학생의 이름
     * @param studentId 학생의 학번
     */
    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }
}

/**
 * CSV 파일을 읽고 쓰는 기능을 제공하는 클래스입니다.
 * 이 클래스는 스터디 그룹 데이터를 CSV 파일에서 읽고 저장하는 역할을 합니다.
 */
class CSVHandler {
    private final String filePath;

    /**
     * CSVHandler 객체를 생성하는 생성자입니다.
     *
     * @param filePath CSV 파일의 경로
     */
    public CSVHandler(String filePath) {
        this.filePath = filePath;
    }

    /**
     * CSV 파일에서 스터디 그룹 목록을 읽어옵니다.
     *
     * @return 읽어온 스터디 그룹 목록
     * @throws IOException 파일을 읽는 중에 발생할 수 있는 오류
     */
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

    /**
     * 스터디 그룹 목록을 CSV 파일에 저장합니다.
     *
     * @param groups 저장할 스터디 그룹 목록
     * @throws IOException 파일에 저장하는 중에 발생할 수 있는 오류
     */
    public void writeStudyGroups(List<StudyGroup> groups) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (StudyGroup group : groups) {
            writer.write(group.groupName + "," + group.subject);
            writer.newLine();
        }
        writer.close();
    }
}

/**
 * 스터디 그룹 플랫폼의 메인 클래스입니다.
 * GUI를 통해 사용자가 스터디 그룹을 생성하고, 검색하고, 그룹에 참여할 수 있는 기능을 제공합니다.
 */
public class StudyGroupPlatform {
    private List<StudyGroup> studyGroups;
    private Map<String, List<StudyGroup>> studyGroupMap;
    private Set<String> groupNameSet;
    private CSVHandler csvHandler;
    private JList<StudyGroup> groupList;
    private DefaultListModel<StudyGroup> listModel;  // JList에 표시할 스터디 그룹 목록
    private JTextField searchField;

    /**
     * StudyGroupPlatform 객체를 생성하는 생성자입니다.
     * 스터디 그룹 데이터를 읽고 GUI를 설정합니다.
     */
    public StudyGroupPlatform() {
        listModel = new DefaultListModel<>();
        csvHandler = new CSVHandler("study_groups.csv");
        studyGroups = new ArrayList<>();
        studyGroupMap = new HashMap<>();
        groupNameSet = new HashSet<>();
        loadStudyGroups(); // 초기화 시 CSV 파일에서 데이터를 불러옵니다.
        setupGUI();  // GUI를 설정합니다.
    }

    /**
     * CSV 파일에서 스터디 그룹 목록을 읽어와 JList에 표시합니다.
     * 이 메서드는 StudyGroupPlatform 생성자에서 호출됩니다.
     */
    private void loadStudyGroups() {
        try {
            studyGroups = csvHandler.readStudyGroups();
            for (StudyGroup group : studyGroups) {
                listModel.addElement(group); // JList에 스터디 그룹을 추가
                groupNameSet.add(group.groupName);
                studyGroupMap.computeIfAbsent(group.subject, k -> new ArrayList<>()).add(group);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "CSV 파일을 읽는 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * GUI를 설정하는 메서드입니다. 스터디 그룹 생성, 검색 및 그룹 참가 기능을 제공합니다.
     */
    private void setupGUI() {
        JFrame frame = new JFrame("스터디 그룹 플랫폼");
        frame.setSize(700, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(245, 245, 245)); // 배경색 설정

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

    /**
     * StudyGroupPlatform의 메인 메서드입니다. 프로그램을 시작합니다.
     *
     * @param args 프로그램 실행 시 필요한 명령어 인수
     */
    public static void main(String[] args) {
        new StudyGroupPlatform();
    }
}
