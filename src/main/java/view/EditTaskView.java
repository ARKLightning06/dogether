package view;

import entity.TaskStatus;
import interface_adapter.add_task.AddTaskController;
import interface_adapter.edit_task.EditTaskController;
import interface_adapter.edit_task.EditTaskState;
import interface_adapter.edit_task.EditTaskViewModel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.awt.GridLayout;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.swing.JCheckBox;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * The shared Swing view for adding and editing tasks.
 *
 * <p>The view displays the task editor state and delegates task
 * creation and editing to their respective controllers.
 */
public class EditTaskView extends JPanel implements PropertyChangeListener {
    private static final Color BACKGROUND = new Color(0xFFF8F1);
    private static final Color TILE_BORDER = new Color(0xEFE3D8);
    private static final Color ACCENT = new Color(0xD4537E);
    private static final Color TEXT = new Color(0x3D3A36);
    private static final Color MUTED = new Color(0x8A847C);
    private static final Color ERROR = new Color(0xA32D2D);

    private final EditTaskViewModel EditTaskViewModel;
    private final AddTaskController addTaskController;
    private final EditTaskController editTaskController;

    private final JTextField nameField = new JTextField();
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private LocalDate selectedDeadline;
    private final JButton deadlineButton = new JButton("Choose date");
    private final JButton clearDeadlineButton = new JButton("Clear");
    private final JLabel deadlineSummary = new JLabel("No deadline");
    private final JPopupMenu calendarPopup = new JPopupMenu();
    private YearMonth calendarMonth;
    private static final DateTimeFormatter DEADLINE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, uuuu", Locale.ENGLISH);
    private final JTextField colourField = new JTextField();
    private final JPanel colourPreview = new JPanel();
    private final JLabel colourPreviewLabel = new JLabel();
    private final JPanel assigneeList = new JPanel();
    private final JLabel assigneeSummary = new JLabel();
    private final Map<String, JCheckBox> assigneeOptions = new LinkedHashMap<>();
    private boolean boardMembersLoaded;
    private final JPanel statusSelector = new JPanel(new GridLayout(1, 3, 6, 0));
    private final Map<TaskStatus, javax.swing.JToggleButton> statusButtons = new LinkedHashMap<>();
    private TaskStatus selectedStatus = TaskStatus.TO_DO;

    private final JLabel titleLabel = new JLabel();
    private final JLabel errorLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Status");
    private final JLabel subtitleLabel = new JLabel();
    private final JButton saveButton = new AccentButton("Save Task");

    private final JTextField memberSearch = new JTextField();
    private final JPanel memberChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
    private final JLabel changesLabel = new JLabel();
    private final JLabel nameError = new JLabel();
    private final JLabel descriptionCount = new JLabel();
    private final JLabel previewTitle = new JLabel();
    private final JTextArea previewDescription = new JTextArea(2, 20) {
        @Override
        public void scrollRectToVisible(java.awt.Rectangle rectangle) {
            // A passive preview must never scroll the editor.
        }
    };
    private final JScrollPane formScroll = new JScrollPane();
    private final JLabel previewMeta = new JLabel();
    private final JPanel previewStripe = new JPanel();
    private final JButton discardButton = new JButton("Discard");
    private EditTaskState original;
    private boolean loadingFields;
    private boolean saving;
    private EditTaskState lastSubmittedState;
    private String username;
    private String boardId;

    /**
     * Constructs the shared task editor view.
     *
     * @param EditTaskViewModel the view model for the editor
     * @param addTaskController the controller for creating tasks
     * @param editTaskController the controller for editing tasks
     */
    public EditTaskView(EditTaskViewModel EditTaskViewModel,
                          AddTaskController addTaskController,
                          EditTaskController editTaskController) {
        this.EditTaskViewModel = EditTaskViewModel;
        this.addTaskController = addTaskController;
        this.editTaskController = editTaskController;

        setLayout(new BorderLayout(0, 16));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        initializeComponents();

        EditTaskViewModel.addPropertyChangeListener(this);
        updateFields();
        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {
                scrollToTop();
            }
        });
    }

    private void scrollToTop() {
        javax.swing.SwingUtilities.invokeLater(() -> {
            formScroll.getViewport().setViewPosition(new java.awt.Point(0, 0));
            formScroll.getVerticalScrollBar().setValue(0);
        });
    }

    /**
     * Initializes and arranges the Swing components.
     */
    private void initializeComponents() {
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 28f));
        titleLabel.setForeground(TEXT);
        subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(13f));
        subtitleLabel.setForeground(MUTED);
        JLabel eyebrow = new JLabel("DOGETHER  /  TASKS");
        eyebrow.setForeground(ACCENT);
        eyebrow.setFont(eyebrow.getFont().deriveFont(Font.BOLD, 11f));
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(eyebrow);
        header.add(Box.createVerticalStrut(8));
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(5));
        header.add(subtitleLabel);

        styleInput(nameField);
        styleInput(colourField);
        styleInput(descriptionArea);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setBorder(BorderFactory.createLineBorder(TILE_BORDER));
        descriptionScroll.setPreferredSize(new Dimension(200, 108));

        statusSelector.setOpaque(false);
        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        for (TaskStatus status : TaskStatus.values()) {
            javax.swing.JToggleButton button = new javax.swing.JToggleButton(statusText(status));
            button.setFont(button.getFont().deriveFont(Font.BOLD, 12f));
            button.setMargin(new Insets(8, 4, 8, 4));
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.addActionListener(event -> setSelectedStatus(status));
            group.add(button);
            statusButtons.put(status, button);
            statusSelector.add(button);
        }
        setSelectedStatus(TaskStatus.TO_DO);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        JPanel details = createSection("Task details", "Give your task a clear name and description.");
        nameError.setForeground(ERROR);
        addFormRow(details, 1, new JLabel("Task name *"), withHint(nameField, nameError));
        addFormRow(details, 2, new JLabel("Description"), withHint(descriptionScroll, descriptionCount));
        JPanel planning = createSection("Planning", "Set a due date, ownership and workflow status.");
        addFormRow(planning, 1, new JLabel("Deadline (optional)"), createDeadlinePicker());
        addFormRow(planning, 2, new JLabel("Assignees"), createAssigneePicker());
        addFormRow(planning, 3, statusLabel, statusSelector);
        JPanel appearance = createSection("Appearance", "Choose a colour to identify this task on the board.");
        addFormRow(appearance, 1, new JLabel("Task colour"), createColourInput());
        addFormRow(appearance, 2, new JLabel("Live task preview"), createTaskPreview());
        GridBagConstraints section = new GridBagConstraints();
        section.gridx = 0;
        section.weightx = 1;
        section.fill = GridBagConstraints.HORIZONTAL;
        section.insets = new Insets(0, 0, 12, 0);
        for (JPanel card : new JPanel[]{details, planning, appearance}) {
            section.gridy = GridBagConstraints.RELATIVE;
            formPanel.add(card, section);
        }
        section.weighty = 1;
        section.fill = GridBagConstraints.BOTH;
        formPanel.add(Box.createGlue(), section);
        formScroll.setViewportView(formPanel);
        formScroll.setBorder(null);
        formScroll.setOpaque(false);
        formScroll.getViewport().setOpaque(false);
        formScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setUnitIncrement(18);

        errorLabel.setForeground(ERROR);
        errorLabel.setFont(errorLabel.getFont().deriveFont(13f));
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD, 14f));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        saveButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        JLabel required = new JLabel("* Required field");
        required.setForeground(MUTED);
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(changesLabel, BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(discardButton);
        buttons.add(saveButton);
        actions.add(buttons, BorderLayout.EAST);
        discardButton.addActionListener(event -> discardChanges());
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 8));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, TILE_BORDER),
                BorderFactory.createEmptyBorder(12, 0, 0, 0)));
        bottomPanel.add(errorLabel, BorderLayout.NORTH);
        bottomPanel.add(actions, BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);
        add(formScroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(event -> saveTask());
        watch(nameField, this::refreshEditor);
        watch(descriptionArea, this::refreshEditor);
        watch(colourField, this::refreshEditor);
        
        int shortcut = java.awt.GraphicsEnvironment.isHeadless() ? java.awt.event.InputEvent.CTRL_DOWN_MASK
                : java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, shortcut), "save");
        getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "discard");
        getActionMap().put("save", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent event) { if (saveButton.isEnabled()) saveTask(); }
        });
        getActionMap().put("discard", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent event) { discardChanges(); }
        });
    }

    private void watch(JTextComponent field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent event) { action.run(); }
            public void removeUpdate(DocumentEvent event) { action.run(); }
            public void changedUpdate(DocumentEvent event) { action.run(); }
        });
    }

    private JPanel withHint(JComponent field, JLabel hint) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        hint.setFont(hint.getFont().deriveFont(12f));
        panel.add(field, BorderLayout.CENTER);
        panel.add(hint, BorderLayout.SOUTH);
        return panel;
    }

    private String statusText(TaskStatus status) {
        return status == TaskStatus.DONE ? "Done"
                : status == TaskStatus.IN_PROGRESS ? "In progress" : "To do";
    }

    private void setSelectedStatus(TaskStatus status) {
        selectedStatus = status == null ? TaskStatus.TO_DO : status;
        statusButtons.forEach((value, button) -> {
            boolean active = value == selectedStatus;
            Color ink = value == TaskStatus.DONE ? new Color(0x38734E)
                    : value == TaskStatus.IN_PROGRESS ? new Color(0x92651F) : new Color(0x626B78);
            Color tint = value == TaskStatus.DONE ? new Color(0xE6F3E9)
                    : value == TaskStatus.IN_PROGRESS ? new Color(0xFFF4DC) : new Color(0xF0F1F3);
            button.setSelected(active);
            button.setForeground(ink);
            button.setBackground(active ? tint : Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(active ? ink : TILE_BORDER, 1, true),
                    BorderFactory.createEmptyBorder(9, 5, 9, 5)));
        });
        refreshEditor();
    }

    private void filterMembers() {
        String query = memberSearch.getText().strip().toLowerCase(Locale.ROOT);
        assigneeList.removeAll();
        assigneeOptions.forEach((name, option) -> {
            if (!option.isSelected() && name.toLowerCase(Locale.ROOT).contains(query)) {
                JPanel row = new JPanel(new BorderLayout(8, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                JLabel label = new JLabel(name);
                label.setForeground(TEXT);
                JButton add = new JButton("+");
                add.setForeground(ACCENT);
                add.setToolTipText("Assign " + name);
                add.getAccessibleContext().setAccessibleName("Assign " + name);
                add.addActionListener(event -> option.setSelected(true));
                row.add(label, BorderLayout.CENTER);
                row.add(add, BorderLayout.EAST);
                assigneeList.add(row);
            }
        });
        if (assigneeList.getComponentCount() == 0) {
            JLabel empty = new JLabel(!boardMembersLoaded ? "Board members not loaded."
                    : "No matching unassigned members.");
            empty.setForeground(MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            assigneeList.add(empty);
        }
        assigneeList.revalidate();
        assigneeList.repaint();
    }

    private JPanel createTaskPreview() {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(new Color(0xFAFAFA));
        card.setBorder(BorderFactory.createLineBorder(TILE_BORDER, 1, true));
        previewStripe.setPreferredSize(new Dimension(1, 5));
        card.add(previewStripe, BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 6));
        body.setOpaque(false);
        body.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));
        previewTitle.setFont(previewTitle.getFont().deriveFont(Font.BOLD, 15f));
        previewDescription.setLineWrap(true);
        previewDescription.setWrapStyleWord(true);
        previewDescription.setEditable(false);
        previewDescription.setFocusable(false);
        ((javax.swing.text.DefaultCaret) previewDescription.getCaret())
                .setUpdatePolicy(javax.swing.text.DefaultCaret.NEVER_UPDATE);
        previewDescription.setOpaque(false);
        previewDescription.setForeground(MUTED);
        previewMeta.setForeground(MUTED);
        body.add(previewTitle, BorderLayout.NORTH);
        body.add(previewDescription, BorderLayout.CENTER);
        body.add(previewMeta, BorderLayout.SOUTH);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private EditTaskState copyState(EditTaskState source) {
        EditTaskState copy = new EditTaskState();
        copy.setTaskId(source.getTaskId());
        copy.setName(source.getName());
        copy.setDescription(source.getDescription());
        copy.setDeadline(source.getDeadline());
        copy.setColour(source.getColour());
        copy.setAssignees(source.getAssignees());
        copy.setStatus(source.getStatus());
        return copy;
    }

    private boolean hasChanges() {
        if (original == null) return false;
        return !java.util.Objects.equals(nameField.getText(), original.getName())
                || !java.util.Objects.equals(descriptionArea.getText(), original.getDescription())
                || !java.util.Objects.equals(selectedDeadline, original.getDeadline())
                || !java.util.Objects.equals(colourField.getText(), original.getColour())
                || (boardMembersLoaded && !new TreeSet<>(parseAssignees()).equals(new TreeSet<>(original.getAssignees())))
                || (isEditMode() && selectedStatus != original.getStatus());
    }

    private void discardChanges() {
        if (saving || !hasChanges()) return;
        int result = javax.swing.JOptionPane.showConfirmDialog(this,
                "Discard your unsaved changes?", "Discard changes",
                javax.swing.JOptionPane.YES_NO_OPTION);
        if (result == javax.swing.JOptionPane.YES_OPTION) {
            EditTaskViewModel.setState(copyState(original));
            EditTaskViewModel.firePropertyChanged();
        }
    }

    private void refreshEditor() {
        if (loadingFields || original == null) return;
        boolean validName = !nameField.getText().isBlank();
        boolean validColour = colourField.getText().matches("#[0-9a-fA-F]{6}");
        boolean dirty = hasChanges();
        nameError.setText(!validName && dirty ? "Enter a task name." : " ");
        descriptionCount.setForeground(MUTED);
        descriptionCount.setText(descriptionArea.getText().length() + " characters");
        changesLabel.setForeground(dirty ? new Color(0x92651F) : MUTED);
        changesLabel.setText(saving ? "Saving..." : dirty ? "Unsaved changes" : "No unsaved changes");
        saveButton.setEnabled(!saving && validName && validColour && boardMembersLoaded
                && username != null && !username.isBlank() && boardId != null && !boardId.isBlank()
                && (!isEditMode() || dirty));
        discardButton.setEnabled(!saving && dirty);
        previewTitle.setText(" " + (validName ? nameField.getText() : "Untitled task"));
        if (!previewDescription.getText().equals(descriptionArea.getText())) {
            previewDescription.setText(descriptionArea.getText());
        }
        previewStripe.setBackground(validColour ? Color.decode(colourField.getText()) : TILE_BORDER);
        previewMeta.setText((selectedDeadline == null ? "No deadline" : selectedDeadline.format(DEADLINE_FORMAT))
                + "  ·  " + parseAssignees().size() + " assigned  ·  "
                + (!isEditMode() ? "To do" : statusText(selectedStatus)));
    }

    private JPanel createSection(String heading, String description) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TILE_BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 18, 7, 18)));
        JLabel title = new JLabel(heading);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel(description);
        subtitle.setFont(subtitle.getFont().deriveFont(12f));
        subtitle.setForeground(MUTED);
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 16, 0);
        panel.add(header, constraints);
        return panel;
    }

    private static final class AccentButton extends JButton {
        AccentButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(!isEnabled() ? new Color(0xC6B9BE) : getModel().isPressed() ? ACCENT.darker()
                    : getModel().isRollover() ? new Color(0xBD416C) : ACCENT);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private JPanel createAssigneePicker() {
        assigneeList.setLayout(new BoxLayout(assigneeList, BoxLayout.Y_AXIS));
        assigneeList.setBackground(Color.WHITE);
        JScrollPane scroll = new JScrollPane(assigneeList);
        scroll.setBorder(BorderFactory.createLineBorder(TILE_BORDER, 1, true));
        scroll.setPreferredSize(new Dimension(200, 132));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        assigneeSummary.setFont(assigneeSummary.getFont().deriveFont(12f));
        JPanel picker = new JPanel(new BorderLayout(0, 6));
        picker.setOpaque(false);
        styleInput(memberSearch);
        memberSearch.setToolTipText("Search board members");
        memberSearch.getAccessibleContext().setAccessibleName("Search board members");
        memberChips.setOpaque(false);
        JPanel searchPanel = new JPanel(new BorderLayout(0, 4));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search members"), BorderLayout.NORTH);
        searchPanel.add(memberSearch, BorderLayout.CENTER);
        searchPanel.add(memberChips, BorderLayout.SOUTH);
        picker.add(searchPanel, BorderLayout.NORTH);
        watch(memberSearch, this::filterMembers);
        picker.add(scroll, BorderLayout.CENTER);
        picker.add(assigneeSummary, BorderLayout.SOUTH);
        populateAssignees(java.util.Collections.emptyList());
        return picker;
    }

    private void populateAssignees(Collection<String> members) {
        assigneeList.removeAll();
        assigneeOptions.clear();
        for (String member : new TreeSet<>(members)) {
            JCheckBox option = new JCheckBox(member);
            option.setForeground(TEXT);
            option.setBackground(Color.WHITE);
            option.setFont(option.getFont().deriveFont(14f));
            option.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            option.setAlignmentX(LEFT_ALIGNMENT);
            option.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            option.addItemListener(event -> updateAssigneeSummary());
            assigneeOptions.put(member, option);
            assigneeList.add(option);
        }
        if (assigneeOptions.isEmpty()) {
            JLabel empty = new JLabel(boardMembersLoaded
                    ? "No board members available." : "Board members not loaded.");
            empty.setForeground(MUTED);
            empty.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            assigneeList.add(empty);
        }
        selectAssignees(EditTaskViewModel.getState().getAssignees());
        assigneeList.revalidate();
        assigneeList.repaint();
        filterMembers();
        refreshEditor();
    }

    private void selectAssignees(List<String> selected) {
        assigneeOptions.forEach((member, option) -> option.setSelected(selected.contains(member)));
        updateAssigneeSummary();
    }

    private void updateAssigneeSummary() {
        assigneeSummary.setForeground(boardMembersLoaded ? MUTED : ERROR);
        assigneeSummary.setText(boardMembersLoaded
                ? parseAssignees().size() + " selected · Select all members assigned to this task."
                : "Board members have not been loaded.");
        memberChips.removeAll();
        for (String member : parseAssignees()) {
            JButton chip = new JButton(member + " ×");
            chip.setForeground(new Color(0x88405B));
            chip.setBackground(new Color(0xFBE8EF));
            chip.setToolTipText("Remove " + member);
            chip.addActionListener(event -> assigneeOptions.get(member).setSelected(false));
            memberChips.add(chip);
        }
        memberChips.revalidate();
        memberChips.repaint();
        filterMembers();
        refreshEditor();
    }

    private JPanel createDeadlinePicker() {
        JPanel picker = new JPanel(new BorderLayout(8, 0));
        picker.setBackground(Color.WHITE);
        picker.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TILE_BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 8)));
        deadlineSummary.setFont(deadlineSummary.getFont().deriveFont(14f));
        picker.add(deadlineSummary, BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        actions.setOpaque(false);
        deadlineButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearDeadlineButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        deadlineButton.addActionListener(event -> {
            calendarMonth = YearMonth.from(selectedDeadline == null
                    ? LocalDate.now() : selectedDeadline);
            renderCalendar();
            calendarPopup.show(deadlineButton, 0, deadlineButton.getHeight());
        });
        clearDeadlineButton.addActionListener(event -> {
            calendarPopup.setVisible(false);
            setSelectedDeadline(null);
        });
        actions.add(deadlineButton);
        actions.add(clearDeadlineButton);
        picker.add(actions, BorderLayout.EAST);
        setSelectedDeadline(selectedDeadline);
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);
        wrapper.add(picker, BorderLayout.NORTH);
        JPanel quickDates = new JPanel(new GridLayout(1, 3, 6, 0));
        quickDates.setOpaque(false);
        String[] labels = {"Today", "Tomorrow", "Next week"};
        int[] offsets = {0, 1, 7};
        for (int i = 0; i < labels.length; i++) {
            final int offset = offsets[i];
            JButton quick = new JButton(labels[i]);
            quick.setForeground(ACCENT);
            quick.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            quick.addActionListener(event -> setSelectedDeadline(LocalDate.now().plusDays(offset)));
            quickDates.add(quick);
        }
        wrapper.add(quickDates, BorderLayout.SOUTH);
        return wrapper;
    }

    private void setSelectedDeadline(LocalDate deadline) {
        selectedDeadline = deadline;
        deadlineSummary.setText(deadline == null ? "No deadline" : deadline.format(DEADLINE_FORMAT));
        deadlineSummary.setForeground(deadline == null ? MUTED : TEXT);
        deadlineButton.setText(deadline == null ? "Choose date" : "Change date");
        clearDeadlineButton.setEnabled(deadline != null);
        refreshEditor();
    }

    private void renderCalendar() {
        calendarPopup.removeAll();
        JPanel calendar = new JPanel(new BorderLayout(0, 8));
        calendar.setBackground(Color.WHITE);
        calendar.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JPanel navigation = new JPanel(new BorderLayout(6, 0));
        navigation.setOpaque(false);
        JLabel monthLabel = new JLabel(calendarMonth.format(
                DateTimeFormatter.ofPattern("MMMM uuuu", Locale.ENGLISH)), SwingConstants.CENTER);
        monthLabel.setForeground(TEXT);
        monthLabel.setFont(monthLabel.getFont().deriveFont(Font.BOLD, 14f));
        JButton previous = new JButton("<");
        JButton next = new JButton(">");
        previous.addActionListener(event -> {
            calendarMonth = calendarMonth.minusMonths(1);
            renderCalendar();
        });
        next.addActionListener(event -> {
            calendarMonth = calendarMonth.plusMonths(1);
            renderCalendar();
        });
        navigation.add(previous, BorderLayout.WEST);
        navigation.add(monthLabel, BorderLayout.CENTER);
        navigation.add(next, BorderLayout.EAST);
        calendar.add(navigation, BorderLayout.NORTH);

        JPanel days = new JPanel(new GridLayout(7, 7, 3, 3));
        days.setOpaque(false);
        days.setPreferredSize(new Dimension(294, 238));
        for (String weekday : new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}) {
            JLabel label = new JLabel(weekday, SwingConstants.CENTER);
            label.setForeground(MUTED);
            days.add(label);
        }
        int offset = calendarMonth.atDay(1).getDayOfWeek().getValue() - 1;
        for (int cell = 0; cell < 42; cell++) {
            int day = cell - offset + 1;
            if (day < 1 || day > calendarMonth.lengthOfMonth()) {
                days.add(new JLabel());
            } else {
                LocalDate date = calendarMonth.atDay(day);
                boolean selected = date.equals(selectedDeadline);
                JButton button = selected ? new AccentButton(Integer.toString(day))
                        : new JButton(Integer.toString(day));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setForeground(selected ? Color.WHITE : TEXT);
                button.setBackground(Color.WHITE);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                button.getAccessibleContext().setAccessibleName(date.format(DEADLINE_FORMAT));
                if (!selected) {
                    button.setBorder(BorderFactory.createLineBorder(
                            date.equals(LocalDate.now()) ? ACCENT : TILE_BORDER, 1, true));
                }
                button.addActionListener(event -> {
                    setSelectedDeadline(date);
                    calendarPopup.setVisible(false);
                });
                days.add(button);
            }
        }
        calendar.add(days, BorderLayout.CENTER);
        JPanel shortcuts = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        shortcuts.setOpaque(false);
        JButton today = new JButton("Today");
        today.addActionListener(event -> {
            setSelectedDeadline(LocalDate.now());
            calendarPopup.setVisible(false);
        });
        JButton none = new JButton("No deadline");
        none.addActionListener(event -> {
            setSelectedDeadline(null);
            calendarPopup.setVisible(false);
        });
        for (int daysAhead : new int[]{1, 7}) {
            JButton quick = new JButton(daysAhead == 1 ? "Tomorrow" : "Next week");
            quick.addActionListener(event -> {
                setSelectedDeadline(LocalDate.now().plusDays(daysAhead));
                calendarPopup.setVisible(false);
            });
            shortcuts.add(quick);
        }
        shortcuts.add(none);
        shortcuts.add(today);
        calendar.add(shortcuts, BorderLayout.SOUTH);
        calendarPopup.setBorder(BorderFactory.createLineBorder(TILE_BORDER));
        calendarPopup.add(calendar);
        calendarPopup.pack();
        calendarPopup.revalidate();
        calendarPopup.repaint();
    }

    private JPanel createColourInput() {
        colourPreview.setPreferredSize(new Dimension(44, 40));
        JButton chooseColour = new JButton("Choose colour");
        chooseColour.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chooseColour.addActionListener(event -> {
            Color initial = colourField.getText().matches("#[0-9a-fA-F]{6}")
                    ? Color.decode(colourField.getText()) : Color.WHITE;
            Color selected = JColorChooser.showDialog(this, "Choose task colour", initial);
            if (selected != null) {
                colourField.setText(String.format("#%06X", selected.getRGB() & 0xFFFFFF));
            }
        });
        colourPreview.setBorder(BorderFactory.createLineBorder(TILE_BORDER));

        JPanel colourRow = new JPanel(new BorderLayout(10, 0));
        colourRow.setOpaque(false);
        colourRow.add(colourField, BorderLayout.CENTER);
        JPanel colourActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        colourActions.setOpaque(false);
        colourActions.add(colourPreview);
        colourActions.add(chooseColour);
        colourRow.add(colourActions, BorderLayout.EAST);

        colourPreviewLabel.setFont(colourPreviewLabel.getFont().deriveFont(12f));
        JPanel colourInput = new JPanel(new BorderLayout(0, 5));
        colourInput.setOpaque(false);
        colourInput.add(colourRow, BorderLayout.NORTH);
        JPanel presets = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 3));
        presets.setOpaque(false);
        for (String hex : new String[]{"#D4537E", "#E9A363", "#E5CB69", "#79AF91",
                "#739BD0", "#A18ACC", "#9DA3AE", "#FFFFFF"}) {
            JButton swatch = new JButton();
            swatch.setPreferredSize(new Dimension(28, 28));
            swatch.setIcon(new javax.swing.Icon() {
                public int getIconWidth() { return 20; }
                public int getIconHeight() { return 20; }
                public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
                    g.setColor(Color.decode(hex));
                    g.fillOval(x, y, 20, 20);
                    g.setColor(TILE_BORDER);
                    g.drawOval(x, y, 19, 19);
                }
            });
            swatch.setOpaque(true);
            swatch.setContentAreaFilled(false);
            swatch.setBorder(BorderFactory.createLineBorder(TILE_BORDER, 1, true));
            swatch.setToolTipText(hex);
            swatch.getAccessibleContext().setAccessibleName("Colour " + hex);
            swatch.addActionListener(event -> colourField.setText(hex));
            presets.add(swatch);
        }
        colourInput.add(presets, BorderLayout.CENTER);
        colourInput.add(colourPreviewLabel, BorderLayout.SOUTH);

        colourField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                updateColourPreview();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                updateColourPreview();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                updateColourPreview();
            }
        });
        updateColourPreview();
        return colourInput;
    }

    private void updateColourPreview() {
        String hex = colourField.getText();
        if (hex.matches("#[0-9a-fA-F]{6}")) {
            colourPreview.setBackground(Color.decode(hex));
            colourPreviewLabel.setForeground(MUTED);
            colourPreviewLabel.setText("Colour preview  ·  " + hex.toUpperCase(java.util.Locale.ROOT));
        } else {
            colourPreview.setBackground(Color.WHITE);
            colourPreviewLabel.setForeground(hex.isEmpty() ? MUTED : ERROR);
            colourPreviewLabel.setText(hex.isEmpty()
                    ? "Use #RRGGBB format." : "Enter a valid colour.");
        }
    }

    private void styleInput(JTextComponent input) {
        input.setFont(input.getFont().deriveFont(14f));
        input.setForeground(TEXT);
        input.setBackground(Color.WHITE);
        input.setCaretColor(TEXT);
        setInputBorder(input, false);
        input.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                if (input != descriptionArea) setInputBorder(input, true);
            }
            @Override
            public void focusLost(FocusEvent event) {
                if (input != descriptionArea) setInputBorder(input, false);
            }
        });
    }

    private void setInputBorder(JTextComponent input, boolean focused) {
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(focused ? ACCENT : TILE_BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    }

    private void addFormRow(JPanel panel, int row, JLabel label, JComponent input) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        label.setForeground(TEXT);
        label.setLabelFor(input);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row * 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 5, 0);
        panel.add(label, constraints);

        constraints.gridy++;
        constraints.insets = new Insets(0, 0, 12, 0);
        panel.add(input, constraints);
    }

    /**
     * Sets the current user and selected board for task operations.
     *
     * @param username the logged-in user's username
     * @param boardId the ID of the currently selected board
     */
    public void setEditorContext(String username, String boardId) {
        this.username = username;
        this.boardId = boardId;
        boardMembersLoaded = false;
        populateAssignees(java.util.Collections.emptyList());
    }

    /**
     * Sets the editor context and the available board member usernames.
     * Call when opening the editor, with the selected board's current members.
     *
     * @param username the logged-in user's username
     * @param boardId the selected board ID
     * @param members the usernames belonging to the selected board
     */
    public void setEditorContext(String username, String boardId, Collection<String> members) {
        this.username = username;
        this.boardId = boardId;
        boardMembersLoaded = members != null;
        populateAssignees(members == null ? java.util.Collections.emptyList() : members);
    }

    /**
     * Returns the name used to identify this view.
     *
     * @return the task editor view name
     */
    public String getViewName() {
        return EditTaskViewModel.getViewName();
    }

    /**
     * Executes the appropriate task use case using the form input.
     */
    private void saveTask() {
        if (saving || !saveButton.isEnabled()) return;
        if (username == null || username.isBlank()
                || boardId == null || boardId.isBlank()) {
            errorLabel.setText("No user or board selected.");
            return;
        }

        if (!boardMembersLoaded) {
            errorLabel.setText("Board members have not been loaded.");
            return;
        }

        if (!colourField.getText().matches("#[0-9a-fA-F]{6}")) {
            errorLabel.setText("Enter a valid colour.");
            return;
        }

        if (nameField.getText().isBlank()) {
            errorLabel.setText("Enter a task name.");
            nameField.requestFocusInWindow();
            return;
        }
        if ((isEditMode() && editTaskController == null)
                || (!isEditMode() && addTaskController == null)) {
            errorLabel.setText("This task operation is not connected.");
            return;
        }

        final LocalDate deadline = selectedDeadline;

        List<String> assignees = parseAssignees();
        EditTaskState state = EditTaskViewModel.getState();

        state.setName(nameField.getText());
        state.setDescription(descriptionArea.getText());
        state.setDeadline(deadline);
        state.setColour(colourField.getText());
        state.setAssignees(assignees);
        state.setStatus(selectedStatus);
        state.setError("");

        errorLabel.setText("");

        lastSubmittedState = state;
        saving = true;
        refreshEditor();
        try {
            if (isEditMode()) {
                editTaskController.execute(
                        username,
                        boardId,
                        state.getTaskId(),
                        state.getName(),
                        state.getDescription(),
                        state.getDeadline(),
                        state.getColour(),
                        state.getAssignees(),
                        state.getStatus());
            } else {
                addTaskController.execute(
                        username,
                        boardId,
                        state.getName(),
                        state.getDescription(),
                        state.getDeadline(),
                        state.getColour(),
                        state.getAssignees());
            }
        } catch (IllegalArgumentException exception) {
            state.setError(exception.getMessage());
            errorLabel.setText(state.getError());
        } finally {
            saving = false;
            refreshEditor();
        }
    }

    /**
     * Returns the selected board member usernames.
     *
     * @return the list of selected usernames
     */
    private List<String> parseAssignees() {
        List<String> assignees = new ArrayList<>();
        assigneeOptions.forEach((member, option) -> {
            if (option.isSelected()) {
                assignees.add(member);
            }
        });
        return assignees;
    }

    /**
     * Returns whether the editor is modifying an existing task.
     *
     * @return true if an existing task ID is present
     */
    private boolean isEditMode() {
        String taskId = EditTaskViewModel.getState().getTaskId();
        return taskId != null && !taskId.isBlank();
    }

    /**
     * Updates the Swing components to reflect the current editor state.
     */
    private void updateFields() {
        EditTaskState state = EditTaskViewModel.getState();

        if (state == null) {
            return;
        }

        boolean openingTask = original == null
                || !java.util.Objects.equals(original.getTaskId(), state.getTaskId());
        boolean failedSave = saving && state == lastSubmittedState
                && state.getError() != null && !state.getError().isBlank();
        loadingFields = true;
        if (!failedSave) original = copyState(state);
        nameField.setText(state.getName());
        descriptionArea.setText(state.getDescription());

        setSelectedDeadline(state.getDeadline());

        colourField.setText(state.getColour());
        selectAssignees(state.getAssignees());
        setSelectedStatus(state.getStatus());

        boolean editing = isEditMode();

        titleLabel.setText(editing ? "Edit Task" : "Add Task");
        saveButton.setText(editing ? "Save Changes" : "Create Task");
        subtitleLabel.setText(editing ? "Update the details of your task."
                : "Create a new task for your board.");

        statusSelector.setVisible(true);
        statusLabel.setVisible(true);
        statusButtons.forEach((value, button) -> button.setEnabled(editing));
        if (!editing) setSelectedStatus(TaskStatus.TO_DO);
        statusLabel.setText(editing ? "Status" : "Status (new tasks start as To do)");

        errorLabel.setText(state.getError());
        loadingFields = false;
        refreshEditor();
        if (openingTask && !failedSave) scrollToTop();
    }

    /**
     * Updates the view when the task editor state changes.
     *
     * @param event the property change event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if ("state".equals(event.getPropertyName())) {
            if (javax.swing.SwingUtilities.isEventDispatchThread()) updateFields();
            else javax.swing.SwingUtilities.invokeLater(this::updateFields);
        }
    }
}
