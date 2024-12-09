import com.studyplatform.model.Group;

import javax.swing.*;
        import java.awt.*;
        import java.util.List;

/**
 * 스터디 그룹 목록을 표시하는 패널
 */
public class GroupListPanel extends JPanel {
    private JList<String> groupJList;
    private DefaultListModel<String> listModel;
    private JButton joinButton;
    private List<Group> groups;

    public GroupListPanel() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        groupJList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(groupJList);

        joinButton = new JButton("그룹 선택 및 참여 신청");

        add(new JLabel("스터디 그룹 목록"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(joinButton, BorderLayout.SOUTH);
    }

    /**
     * 그룹 목록을 설정하고 JList를 업데이트
     */
    public void setGroupList(List<Group> groups) {
        this.groups = groups;
        listModel.clear();
        for (Group group : groups) {
            listModel.addElement(group.getGroupName() + " (" + group.getStudyField() + ")");
        }
    }

    /**
     * 선택된 그룹을 반환
     */
    public Group getSelectedGroup() {
        int selectedIndex = groupJList.getSelectedIndex();
        if (selectedIndex != -1 && groups != null && selectedIndex < groups.size()) {
            return groups.get(selectedIndex);
        }
        return null;
    }

    /**
     * 참여 신청 버튼에 리스너 추가
     */
    public void addJoinButtonListener(ActionListener listener) {
        joinButton.addActionListener(listener);
    }
}
