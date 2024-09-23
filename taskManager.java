import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TaskManager extends JFrame {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/task_manager";
    private static final String USERNAME = <"your username">;
    private static final String PASSWORD = <"Password">;

    private ArrayList<Task> tasks = new ArrayList<>();
    private JTextField taskNameField;
    private JTextField dueDateField;
    private JTextArea taskListArea;
    private Connection connection;

    public TaskManager() {
        super("Task Manager");

        // UI initialization
        JLabel taskNameLabel = new JLabel("Task Name:");
        taskNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        taskNameField = new JTextField(20);

        JLabel dueDateLabel = new JLabel("Due Date (DD/MM/YYYY):");
        dueDateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dueDateField = new JTextField(20);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(e -> addTask());

        JButton deleteButton = new JButton("Delete Task");
        deleteButton.addActionListener(e -> deleteTask());
        deleteButton.setBackground(Color.RED);

        JButton markDoneButton = new JButton("Mark Done");
        markDoneButton.addActionListener(e -> markTaskDone());
        markDoneButton.setBackground(Color.GREEN);

        JButton refreshButton = new JButton("Refresh Tasks");
        refreshButton.addActionListener(e -> refreshTasksFromDatabase());

        taskListArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(taskListArea);

        // Layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(taskNameLabel, gbc);
        gbc.gridy++;
        panel.add(taskNameField, gbc);
        gbc.gridy++;
        panel.add(dueDateLabel, gbc);
        gbc.gridy++;
        panel.add(dueDateField, gbc);
        gbc.gridy++;
        panel.add(addButton, gbc);
        gbc.gridy++;
        panel.add(deleteButton, gbc);
        gbc.gridy++;
        panel.add(markDoneButton, gbc);
        gbc.gridy++;
        panel.add(refreshButton, gbc);
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(scrollPane, gbc);

        add(panel);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void refreshTaskList() {
        taskListArea.setText("");
        tasks.clear();
        try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks")) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                Date dueDate = resultSet.getDate("due_date");
                boolean isDone = resultSet.getBoolean("is_done");
                tasks.add(new Task(id, name, dueDate, isDone));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Task task : tasks) {
            String status = task.isDone() ? " (Done)" : "";
            taskListArea.append(". ID: " + task.getId() + ", Name: " + task.getName() + " (Due: " + task.getDueDate() + ")" + status + "\n");
        }
    }

    private void addTask() {
        String taskName = taskNameField.getText();
        String dueDateString = dueDateField.getText();
