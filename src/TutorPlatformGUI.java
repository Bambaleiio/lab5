import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TutorPlatformGUI extends JFrame {
    private DBManager dbManager;
    private JTextArea outputArea;
    private String userRole;

    public TutorPlatformGUI(String role) {
        this.userRole = role;
        try {
            dbManager = new DBManager(role);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка подключения к БД: " + e.getMessage());
            System.exit(1);
        }
        setTitle("Платформа для репетиторов – Режим: " + role);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        // Панель для ввода данных и кнопок
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));

        // Кнопки для основных операций
        JButton btnCreateDB = new JButton("Создать БД");
        JButton btnDeleteDB = new JButton("Удалить БД");
        JButton btnClearTable = new JButton("Очистить таблицу");
        JButton btnAddUser = new JButton("Добавить пользователя");
        JButton btnSearchUser = new JButton("Найти пользователя по имени");
        JButton btnUpdateUser = new JButton("Обновить пользователя");
        JButton btnDeleteUser = new JButton("Удалить пользователя по имени");
        JButton btnViewAll = new JButton("Просмотр всех пользователей");
        JButton btnCreateDBUser = new JButton("Создать нового пользователя БД");

        // Текстовые поля для ввода данных
        JTextField tfName = new JTextField();
        JTextField tfRole = new JTextField();
        JTextField tfContact = new JTextField();
        JTextField tfSearchName = new JTextField();
        JTextField tfUserId = new JTextField();
        JTextField tfNewName = new JTextField();
        JTextField tfNewRole = new JTextField();
        JTextField tfNewContact = new JTextField();
        JTextField tfDBUser = new JTextField();
        JTextField tfDBPassword = new JTextField();
        JTextField tfAccessMode = new JTextField();

        // Обработчики событий для кнопок
        btnCreateDB.addActionListener(e -> {
            String result = dbManager.createDatabase();
            outputArea.setText(result);
        });

        btnDeleteDB.addActionListener(e -> {
            String result = dbManager.deleteDatabase();
            outputArea.setText(result);
        });

        btnClearTable.addActionListener(e -> {
            String result = dbManager.clearTable();
            outputArea.setText(result);
        });

        btnAddUser.addActionListener(e -> {
            String name = tfName.getText().trim();
            String roleText = tfRole.getText().trim();
            String contact = tfContact.getText().trim();
            String result = dbManager.addUser(name, roleText, contact);
            outputArea.setText(result);
        });

        btnSearchUser.addActionListener(e -> {
            String searchName = tfSearchName.getText().trim();
            String result = dbManager.searchUser(searchName);
            outputArea.setText(result);
        });

        btnUpdateUser.addActionListener(e -> {
            try {
                int id = Integer.parseInt(tfUserId.getText().trim());
                String newName = tfNewName.getText().trim();
                String newRole = tfNewRole.getText().trim();
                String newContact = tfNewContact.getText().trim();
                String result = dbManager.updateUser(id, newName, newRole, newContact);
                outputArea.setText(result);
            } catch (NumberFormatException ex) {
                outputArea.setText("Неверный формат ID.");
            }
        });

        btnDeleteUser.addActionListener(e -> {
            String name = tfSearchName.getText().trim();
            String result = dbManager.deleteUserByName(name);
            outputArea.setText(result);
        });

        btnViewAll.addActionListener(e -> {
            String result = dbManager.viewAllUsers();
            outputArea.setText(result);
        });

        btnCreateDBUser.addActionListener(e -> {
            String newDBUser = tfDBUser.getText().trim();
            String newDBPassword = tfDBPassword.getText().trim();
            String accessMode = tfAccessMode.getText().trim();
            String result = dbManager.createDBUser(newDBUser, newDBPassword, accessMode);
            outputArea.setText(result);
        });

        // компоненты с подписями
        panel.add(new JLabel("Добавить пользователя: Имя"));
        panel.add(tfName);
        panel.add(new JLabel("Роль"));
        panel.add(tfRole);
        panel.add(new JLabel("Контакт"));
        panel.add(tfContact);
        panel.add(btnAddUser);
        panel.add(new JLabel(""));

        panel.add(new JLabel("Найти/Удалить пользователя по имени:"));
        panel.add(tfSearchName);
        panel.add(btnSearchUser);
        panel.add(btnDeleteUser);

        panel.add(new JLabel("Обновить пользователя: ID"));
        panel.add(tfUserId);
        panel.add(new JLabel("Новое имя"));
        panel.add(tfNewName);
        panel.add(new JLabel("Новая роль"));
        panel.add(tfNewRole);
        panel.add(new JLabel("Новый контакт"));
        panel.add(tfNewContact);
        panel.add(btnUpdateUser);
        panel.add(new JLabel(""));

        panel.add(btnViewAll);

        // Функции, доступные только администратору
        panel.add(btnCreateDB);
        panel.add(btnDeleteDB);
        panel.add(btnClearTable);
        panel.add(new JLabel("Создать пользователя БД: Имя"));
        panel.add(tfDBUser);
        panel.add(new JLabel("Пароль"));
        panel.add(tfDBPassword);
        panel.add(new JLabel("Режим доступа"));
        panel.add(tfAccessMode);
        panel.add(btnCreateDBUser);

        // Если пользователь – Гость, отключаем административные функции
        if(userRole.equalsIgnoreCase("guest")){
            btnCreateDB.setEnabled(false);
            btnDeleteDB.setEnabled(false);
            btnClearTable.setEnabled(false);
            btnAddUser.setEnabled(false);
            btnUpdateUser.setEnabled(false);
            btnDeleteUser.setEnabled(false);
            btnCreateDBUser.setEnabled(false);
        }

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Окно логина
        JFrame loginFrame = new JFrame("Вход в систему");
        JTextField tfUser = new JTextField();
        JPasswordField pfPass = new JPasswordField();
        Object[] message = {
                "Имя пользователя:", tfUser,
                "Пароль:", pfPass
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Вход", JOptionPane.OK_CANCEL_OPTION);
        if(option == JOptionPane.OK_OPTION) {
            String username = tfUser.getText().trim();
            String password = new String(pfPass.getPassword());
            String role;
            //admin/admin – режим администратора, guest/guest – режим гостя.
            if(username.equalsIgnoreCase("admin") && password.equals("admin")){
                role = "admin";
            } else if(username.equalsIgnoreCase("guest") && password.equals("guest")){
                role = "guest";
            } else {
                JOptionPane.showMessageDialog(null, "Неверные учетные данные.");
                return;
            }
            TutorPlatformGUI gui = new TutorPlatformGUI(role);
            gui.setVisible(true);
        }
    }
}
