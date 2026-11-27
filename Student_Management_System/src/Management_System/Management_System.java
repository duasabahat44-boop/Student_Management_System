package Management_System;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Management_System extends JFrame {

    private JTextField txtId, txtFirstName, txtLastName;
    private JTextField txtPhone, txtSearch;
    private JTextArea txtAddress;

    private JRadioButton male, female;

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private final Path filePath = Paths.get("data", "students.txt");

    private String selectedOldId = null;

    private final Color backgroundColor = new Color(218, 222, 232);
    private final Color blueColor = new Color(48, 105, 145);

    public Management_System() { {

        setTitle("STUDENT MANAGEMENT SYSTEM");
        setSize(1020, 590);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(backgroundColor);
        setLayout(new BorderLayout());

        createGUI();
        loadStudents();

        setVisible(true);}
    }

    private void createGUI() {

        // Main heading
        JLabel heading = new JLabel(
                "STUDENT MANAGEMENT SYSTEM", SwingConstants.CENTER);

        heading.setFont(new Font("Arial", Font.BOLD, 34));
        heading.setBorder(new EmptyBorder(12, 0, 15, 0));

        add(heading, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(new EmptyBorder(5, 25, 20, 25));

        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();

        mainPanel.add(formPanel);
        mainPanel.add(tablePanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    // ---------------- FORM PANEL ----------------

    private JPanel createFormPanel() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(17);
        txtFirstName = new JTextField(17);
        txtLastName = new JTextField(17);
        txtPhone = new JTextField(17);

        txtAddress = new JTextArea(5, 17);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);

        JScrollPane addressScroll =
                new JScrollPane(txtAddress);

        male = new JRadioButton("Male");
        female = new JRadioButton("Female");

        male.setBackground(backgroundColor);
        female.setBackground(backgroundColor);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(male);
        genderGroup.add(female);

        JPanel genderPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 5, 0));

        genderPanel.setBackground(backgroundColor);
        genderPanel.add(male);
        genderPanel.add(female);

        addFormRow(panel, gbc, 0, "Id:", txtId);
        addFormRow(panel, gbc, 1, "First Name:", txtFirstName);
        addFormRow(panel, gbc, 2, "Last Name:", txtLastName);
        addFormRow(panel, gbc, 3, "Sex:", genderPanel);
        addFormRow(panel, gbc, 4, "Phone:", txtPhone);
        addFormRow(panel, gbc, 5, "Address:", addressScroll);

        // Buttons
        JButton btnRemove = new JButton("✖  Remove");
        JButton btnEdit = new JButton("↻  Edit");
        JButton btnAdd = new JButton("✚  Add");

        btnRemove.setForeground(Color.RED);
        btnEdit.setForeground(new Color(0, 145, 130));
        btnAdd.setForeground(new Color(20, 150, 65));

        styleButton(btnRemove);
        styleButton(btnEdit);
        styleButton(btnAdd);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10));

        buttonPanel.setBackground(backgroundColor);

        buttonPanel.add(btnRemove);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnAdd);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        panel.add(buttonPanel, gbc);

        // Button actions
        btnAdd.addActionListener(e -> addStudent());
        btnEdit.addActionListener(e -> editStudent());
        btnRemove.addActionListener(e -> removeStudent());

        return panel;
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            Component field) {

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;

        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;

        panel.add(field, gbc);
    }

    // ---------------- TABLE PANEL ----------------

    private JPanel createTablePanel() {

        JPanel panel = new JPanel(new BorderLayout(5, 15));
        panel.setBackground(backgroundColor);

        // Search area
        JPanel searchPanel = new JPanel(
                new BorderLayout(10, 5));

        searchPanel.setBackground(backgroundColor);

        JLabel searchLabel = new JLabel(
                "Enter Value To Search");

        searchLabel.setFont(
                new Font("Arial", Font.BOLD, 14));

        txtSearch = new JTextField();

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {
            "Id", "First Name", "Last Name",
            "Sex", "Phone", "Address"
        };

        model = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(22);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12));

        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        // Search functionality
        txtSearch.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {

                String searchText = txtSearch.getText().trim();

                if (searchText.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(
                        RowFilter.regexFilter(
                            "(?i)" + Pattern.quote(searchText)
                        )
                    );
                }
            }
        });

        // Select table row
        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                showSelectedStudent();
            }
        });

        return panel;
    }

    // ---------------- ADD STUDENT ----------------

    private void addStudent() {

        String id = txtId.getText().trim();
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        String gender = getGender();

        if (id.isEmpty() || first.isEmpty()
                || last.isEmpty() || phone.isEmpty()
                || address.isEmpty() || gender.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        if (containsInvalidCharacters(id, first, last,
                phone, address)) {

            JOptionPane.showMessageDialog(
                    this,
                    "The character | is not allowed.",
                    "Invalid Input",
                    JOptionPane.WARNING_MESSAGE);

            return;
        }

        if (findStudent(id) != -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Student ID already exists.");

            return;
        }

        model.addRow(new Object[] {
            id, first, last, gender, phone, address
        });

        saveStudents();
        clearFields();

        JOptionPane.showMessageDialog(
                this, "Student added successfully!");
    }

    // ---------------- EDIT STUDENT ----------------

    private void editStudent() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a student first.");

            return;
        }

        String id = txtId.getText().trim();
        String first = txtFirstName.getText().trim();
        String last = txtLastName.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();
        String gender = getGender();

        if (id.isEmpty() || first.isEmpty()
                || last.isEmpty() || phone.isEmpty()
                || address.isEmpty() || gender.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this, "Please fill all fields.");

            return;
        }

        if (containsInvalidCharacters(id, first, last,
                phone, address)) {

            JOptionPane.showMessageDialog(
                    this, "The character | is not allowed.");

            return;
        }

        int duplicateRow = findStudent(id);

        if (duplicateRow != -1 && duplicateRow != selectedRow) {

            JOptionPane.showMessageDialog(
                    this, "This Student ID already exists.");

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you want to update this student?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        model.setValueAt(id, selectedRow, 0);
        model.setValueAt(first, selectedRow, 1);
        model.setValueAt(last, selectedRow, 2);
        model.setValueAt(gender, selectedRow, 3);
        model.setValueAt(phone, selectedRow, 4);
        model.setValueAt(address, selectedRow, 5);

        saveStudents();
        clearFields();

        JOptionPane.showMessageDialog(
                this, "Student updated successfully!");
    }

    // ---------------- REMOVE STUDENT ----------------

    private void removeStudent() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this, "Please select a student first.");

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this student?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {

            model.removeRow(selectedRow);

            saveStudents();
            clearFields();

            JOptionPane.showMessageDialog(
                    this, "Student removed successfully!");
        }
    }

    // ---------------- SELECT STUDENT ----------------

    private void showSelectedStudent() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            return;
        }

        int row = table.convertRowIndexToModel(selectedRow);

        txtId.setText(model.getValueAt(row, 0).toString());
        txtFirstName.setText(model.getValueAt(row, 1).toString());
        txtLastName.setText(model.getValueAt(row, 2).toString());
        txtPhone.setText(model.getValueAt(row, 4).toString());
        txtAddress.setText(model.getValueAt(row, 5).toString());

        String gender = model.getValueAt(row, 3).toString();

        if (gender.equals("Male")) {
            male.setSelected(true);
        } else {
            female.setSelected(true);
        }
    }

    // ---------------- FIND STUDENT ----------------

    private int findStudent(String id) {

        for (int i = 0; i < model.getRowCount(); i++) {

            String currentId =
                    model.getValueAt(i, 0).toString();

            if (currentId.equalsIgnoreCase(id)) {
                return i;
            }
        }

        return -1;
    }

    // ---------------- GENDER ----------------

    private String getGender() {

        if (male.isSelected()) {
            return "Male";
        }

        if (female.isSelected()) {
            return "Female";
        }

        return "";
    }

    // ---------------- CLEAR FIELDS ----------------

    private void clearFields() {

        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");

        male.setSelected(false);
        female.setSelected(false);

        table.clearSelection();
        selectedOldId = null;
    }

    private boolean containsInvalidCharacters(String... values) {

        for (String value : values) {

            if (value.contains("|")
                    || value.contains("\n")
                    || value.contains("\r")) {
                return true;
            }
        }

        return false;
    }

    // ---------------- SAVE DATA ----------------

    private void saveStudents() {

        try {

            Files.createDirectories(filePath.getParent());

            List<String> lines = new ArrayList<>();

            for (int i = 0; i < model.getRowCount(); i++) {

                StringBuilder line = new StringBuilder();

                for (int j = 0; j < 6; j++) {

                    if (j > 0) {
                        line.append("|");
                    }

                    line.append(model.getValueAt(i, j));
                }

                lines.add(line.toString());
            }

            Files.write(
                    filePath,
                    lines,
                    StandardCharsets.UTF_8);

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save data: " + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- LOAD DATA ----------------

    private void loadStudents() {

        try {

            if (!Files.exists(filePath)) {
                return;
            }

            List<String> lines = Files.readAllLines(
                    filePath, StandardCharsets.UTF_8);

            for (String line : lines) {

                String[] data = line.split("\\|", -1);

                if (data.length == 6) {
                    model.addRow(data);
                }
            }

        } catch (IOException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load data: " + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------- BUTTON DESIGN ----------------

    private void styleButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(125, 42));
        button.setFocusPainted(false);
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
    }

    // ---------------- MAIN METHOD ----------------

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Management_System();
        });
    }
}
