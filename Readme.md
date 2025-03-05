# Хранимые процедуры
``` sql
-- Создание таблицы
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    role TEXT NOT NULL,
    contact TEXT
);

-- Создание базы данных
CREATE OR REPLACE PROCEDURE sp_createDatabase()
LANGUAGE plpgsql
AS $$
BEGIN
    EXECUTE 'CREATE DATABASE platformdb';
EXCEPTION WHEN duplicate_database THEN
    RAISE NOTICE 'База данных platformdb уже существует.';
END;
$$;

-- Удаление базы данных
CREATE OR REPLACE PROCEDURE sp_deleteDatabase()
LANGUAGE plpgsql
AS $$
BEGIN
    EXECUTE 'DROP DATABASE IF EXISTS platformdb';
END;
$$;

-- Очистка таблицы
CREATE OR REPLACE PROCEDURE sp_clearTable()
LANGUAGE plpgsql
AS $$
BEGIN
    TRUNCATE TABLE users;
EXCEPTION WHEN undefined_table THEN
    RAISE NOTICE 'Таблица users не существует.';
END;
$$;

-- Добавление нового пользователя
CREATE OR REPLACE PROCEDURE sp_insertUser(p_name TEXT, p_role TEXT, p_contact TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    INSERT INTO users (name, role, contact) VALUES (p_name, p_role, p_contact);
END;
$$;

-- Поиск пользователя по имени
CREATE OR REPLACE PROCEDURE sp_searchUser(p_name TEXT, OUT p_cursor refcursor)
LANGUAGE plpgsql
AS $$
BEGIN
    p_cursor := 'search_user_cursor';
    OPEN p_cursor FOR
        SELECT u.id, u.name, u.role, u.contact
        FROM users u
        WHERE u.name = p_name;
END;
$$;


-- Обновление пользователя
CREATE OR REPLACE PROCEDURE sp_updateUser(p_id INT, p_name TEXT, p_role TEXT, p_contact TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    UPDATE users 
    SET name = p_name, role = p_role, contact = p_contact 
    WHERE id = p_id;
END;
$$;

-- Удаление пользователя по имени
CREATE OR REPLACE PROCEDURE sp_deleteUserByName(p_name TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM users WHERE name = p_name;
END;
$$;

-- Просмотр всех пользователей
CREATE OR REPLACE PROCEDURE sp_viewAllUsers(OUT p_cursor refcursor)
LANGUAGE plpgsql
AS $$
BEGIN
    p_cursor := 'view_all_users_cursor';
    OPEN p_cursor FOR
        SELECT u.id, u.name, u.role, u.contact
        FROM users u;
END;
$$;


-- Создание нового пользователя базы данных
CREATE OR REPLACE PROCEDURE sp_createDBUser(p_newUser TEXT, p_newPassword TEXT, p_accessMode TEXT)
LANGUAGE plpgsql
AS $$
BEGIN
    EXECUTE format('CREATE USER %I WITH PASSWORD %L', p_newUser, p_newPassword);
    
    IF p_accessMode = 'admin' THEN
        EXECUTE format('GRANT ALL PRIVILEGES ON DATABASE platformdb TO %I', p_newUser);
    ELSIF p_accessMode = 'guest' THEN
        EXECUTE format('GRANT CONNECT ON DATABASE platformdb TO %I', p_newUser);
    ELSE
        RAISE NOTICE 'Неизвестный режим доступа. Пользователь создан без дополнительных прав.';
    END IF;
END;
$$;

```