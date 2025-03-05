import java.sql.*;

public class DBManager {
    private Connection connection;

    public DBManager(String role) throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/platformdb";
        String user, password;
        if (role.equalsIgnoreCase("admin")) {
            user = "postgres";
            password = "Bambaleo";
        } else {
            user = "guest_role";
            password = "guest";
        }
        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
    }

    public String createDatabase() {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_createDatabase()");
            cs.execute();
            return "База данных успешно создана.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при создании БД: " + e.getMessage();
        }
    }

    public String deleteDatabase() {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_deleteDatabase()");
            cs.execute();
            return "База данных успешно удалена.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при удалении БД: " + e.getMessage();
        }
    }

    public String clearTable() {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_clearTable()");
            cs.execute();
            return "Таблица очищена.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при очистке таблицы: " + e.getMessage();
        }
    }

    public String addUser(String name, String role, String contact) {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_insertUser(?, ?, ?)");
            cs.setString(1, name);
            cs.setString(2, role);
            cs.setString(3, contact);
            cs.execute();
            return "Пользователь успешно добавлен.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при добавлении пользователя: " + e.getMessage();
        }
    }

    public String searchUser(String name) {
        try {
            // имя, refcursor
            CallableStatement cs = connection.prepareCall("CALL sp_searchUser(?, ?)");
            cs.setString(1, name);
            cs.registerOutParameter(2, java.sql.Types.OTHER);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(2);
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                int id = rs.getInt("id");
                String uName = rs.getString("name");
                String uRole = rs.getString("role");
                String uContact = rs.getString("contact");
                result.append("ID: ").append(id)
                        .append(", Имя: ").append(uName)
                        .append(", Роль: ").append(uRole)
                        .append(", Контакт: ").append(uContact).append("\n");
            }
            if (result.length() == 0) {
                return "Пользователь с именем \"" + name + "\" не найден.";
            }
            return result.toString();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при поиске пользователя: " + e.getMessage();
        }
    }

    public String updateUser(int id, String name, String role, String contact) {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_updateUser(?, ?, ?, ?)");
            cs.setInt(1, id);
            cs.setString(2, name);
            cs.setString(3, role);
            cs.setString(4, contact);
            cs.execute();
            return "Пользователь успешно обновлён.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при обновлении пользователя: " + e.getMessage();
        }
    }

    public String deleteUserByName(String name) {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_deleteUserByName(?)");
            cs.setString(1, name);
            cs.execute();
            return "Пользователь успешно удалён.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при удалении пользователя: " + e.getMessage();
        }
    }

    public String viewAllUsers() {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_viewAllUsers(?)");
            cs.registerOutParameter(1, java.sql.Types.OTHER);
            cs.execute();
            ResultSet rs = (ResultSet) cs.getObject(1);
            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                int id = rs.getInt("id");
                String uName = rs.getString("name");
                String uRole = rs.getString("role");
                String uContact = rs.getString("contact");
                result.append("ID: ").append(id)
                        .append(", Имя: ").append(uName)
                        .append(", Роль: ").append(uRole)
                        .append(", Контакт: ").append(uContact).append("\n");
            }
            if (result.length() == 0) {
                return "В базе нет записей.";
            }
            return result.toString();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при просмотре пользователей: " + e.getMessage();
        }
    }

    public String createDBUser(String newUser, String newPassword, String accessMode) {
        try {
            CallableStatement cs = connection.prepareCall("CALL sp_createDBUser(?, ?, ?)");
            cs.setString(1, newUser);
            cs.setString(2, newPassword);
            cs.setString(3, accessMode);
            cs.execute();
            return "Новый пользователь БД успешно создан.";
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                return "Ошибка: " + ex.getMessage();
            }
            return "Ошибка при создании пользователя БД: " + e.getMessage();
        }
    }
}
