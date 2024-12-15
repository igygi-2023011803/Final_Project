import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List; // java.util.List를 명시적으로 가져옵니다.
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 학생을 나타내는 클래스
class Student implements Serializable {
    private String name;
    private String email;

    // 생성자
    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // 이름 반환
    public String getName() {
        return name;
    }

    // 이메일 반환
    public String getEmail() {
        return email;
    }

    // 문자열 표현 반환
    @Override
    public String toString() {
        return name + " (" + email + ")";
    }
}

// 스터디 그룹을 나타내는 클래스
class StudyGroup implements Serializable {
    private String groupName;
    private String subject;
    private List<Student> members; // java.util.List 사용

    // 생성자
    public StudyGroup(String groupName, String subject) {
        this.groupName = groupName;
        this.subject = subject;
        this.members = new ArrayList<>();
    }

    // 그룹 이름 반환
    public String getGroupName() {
        return groupName;
    }

    // 과목 반환
    public String getSubject() {
        return subject;
    }

    // 멤버 추가
    public void addMember(Student student) {
        members.add(student);
    }

    // 멤버 제거
    public void removeMember(Student student) {
        members.remove(student);
    }

    // 멤버 목록 반환
    public List<Student> getMembers() {
        return members;
    }

    // 문자열 표현 반환
    @Override
    public String toString() {
        return groupName + " - " + subject + " (" + members.size() + "명)";
    }
}

// 메인 클래스
public class StudyPlatform extends JFrame {
    private List<StudyGroup> studyGroups; // java.util.List 사용
    private JTextArea displayArea;
    private JTextField nameField, emailField, groupNameField, subjectField;

    private final String DATA_FILE = "studyGroups.dat";

    // 생성자
    public StudyPlatform() {
        super("스터디 플랫폼");
        studyGroups = new ArrayList<>();
        loadGroups();

        // GUI 구성
        setLayout(new BorderLayout());

        // 상단 패널 - 학생 정보 입력
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

        // 중앙 패널 - 버튼
        JPanel buttonPanel = new JPanel();

        JButton createButton = new JButton("스터디 그룹 생성");
        JButton joinButton = new JButton("그룹 참여");
        JButton viewButton = new JButton("그룹 보기");

        buttonPanel.add(createButton);
        buttonPanel.add(joinButton);
        buttonPanel.add(viewButton);

        add(buttonPanel, BorderLayout.CENTER);

        // 하단 패널 - 그룹 목록 표시
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.SOUTH);

        // 버튼 액션 리스너
        createButton.addActionListener(e -> createStudyGroup());
        joinButton.addActionListener(e -> joinStudyGroup());
        viewButton.addActionListener(e -> displayGroups());

        // 창 종료 시 데이터 저장
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveGroups();
                System.exit(0);
            }
        });

        setSize(500, 600);
        setVisible(true);
    }

    // 이메일 형식 검증 메소드
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 스터디 그룹 생성 메소드
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

        // 새로운 학생 생성
        Student student = new Student(name, email);
        // 새로운 그룹 생성
        StudyGroup group = new StudyGroup(groupName, subject);
        group.addMember(student);
        studyGroups.add(group);
        JOptionPane.showMessageDialog(this, "스터디 그룹이 생성되었습니다.", "성공", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        saveGroups();
    }

    // 그룹 참여 메소드
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

    // 그룹 보기 메소드
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

    // 필드 초기화 메소드
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        groupNameField.setText("");
        subjectField.setText("");
    }

    // 그룹 데이터를 파일에서 로드
    @SuppressWarnings("unchecked")
    private void loadGroups() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            studyGroups = (List<StudyGroup>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // 파일이 없거나 오류 발생 시 빈 리스트 사용
            studyGroups = new ArrayList<>();
        }
    }

    // 그룹 데이터를 파일에 저장
    private void saveGroups() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(studyGroups);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "데이터 저장에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 메인 메소드
    public static void main(String[] args) {
        new StudyPlatform();
    }
}
