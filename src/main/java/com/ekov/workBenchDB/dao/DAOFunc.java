package com.ekov.workBenchDB.dao;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;


@Repository
public class DAOFunc {

    private static Connection connection;

    @Autowired
    private EntityManager entityManager;


    public String getAllNameTables(String adr, String user, String pass) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {  //вывод названий таблиц
        setConnIfNull(adr, user, pass);
        Statement a1 = connection.createStatement();
        ResultSet rs = null;
        if (adr.contains("mysql")) {
            rs = a1.executeQuery("Show tables");
        }
        else if(adr.contains("postgres")){
            rs = a1.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");
        }
        StringBuilder myTables = new StringBuilder();
        while(rs.next()) {
            myTables.append(rs.getString(1)).append("\n");
        }
        StringBuilder res = new StringBuilder(myTables);
        String resStr = res.toString();
        return resStr;
    }

    public static void registration(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "bestuser", "bestuser");
        PreparedStatement statement = connection.prepareStatement("INSERT my_db.polz(username, password) VALUES (?,?)");
        statement.setString(1, username);
        statement.setString(2, password);
        statement.executeUpdate();
    }

    public static boolean login(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "bestuser", "bestuser");
        PreparedStatement statement = connection.prepareStatement("SELECT ? FROM my_db.polz where username = ?");
        statement.setString(2, username);
        statement.setString(1, password);
        ResultSet  resultSet = statement.executeQuery();
        int count = 0;
        if (resultSet.next()) {
            count += 1;
        }
        if (count == 1) {
            System.out.println("Уже зареган");
            return true;
        }
        return false;
    }

    public void setConnIfNull(String adr, String user, String pass) throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(adr, user, pass);
            System.out.println("------------------------------------------- EMployeeeDAOIMPL::getConnection");
            System.out.println("URL = " + adr);
            System.out.println("username = " +user);
            System.out.println("password = " +pass);
        }
    }

    public RowsAndCols query(String query, String adr, String user, String pass) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException { //вывод таблицы(строчки)
        setConnIfNull(adr, user, pass);
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(query);

        ResultSetMetaData rsmd = result.getMetaData();
        int columnCount = rsmd.getColumnCount();
        List<List<String>> rows = getRows(result, columnCount);
        List<String> cols = getCols(rsmd, columnCount);
        return new RowsAndCols(rows, cols);
    }

    private List<List<String>> getRows(ResultSet result, int ColumnCount) throws SQLException, IOException {
        List<String[]> rows = new ArrayList<>();
        while (result.next()) {
            String[] row = new String[ColumnCount];
            for (int i = 1; i <= ColumnCount; i++) {
                row[i - 1] = result.getString(i);
            }
            rows.add(row);
        }
        String[][] array = new String[rows.size()][ColumnCount];
        for (int i = 0; i < rows.size(); i++) {
            array[i] = rows.get(i);
        }
        List<List<String>> valueNames = new ArrayList<List<String>>();
        for (int i = 0; i < array.length; i++) {
            valueNames.add(List.of(array[i]));
        }
        return valueNames;
    }

    private List<String> getCols(ResultSetMetaData rsmd, int ColumnCount) throws SQLException, IOException { //вывод колоноки(столбца)
        List<String> columnNames = new ArrayList<String>();
        for (int i = 1; i < ColumnCount + 1; i++) {
            columnNames.add(rsmd.getColumnName(i));
        }
        return columnNames;
    }



    @Override
    public String toString() {
        return "EmployeeDAOImpl{" +
                "entityManager=" + entityManager +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DAOFunc that = (DAOFunc) o;
        return Objects.equals(entityManager, that.entityManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityManager);
    }
}
