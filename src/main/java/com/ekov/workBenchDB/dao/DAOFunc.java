package com.ekov.workBenchDB.dao;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.Date;


@Repository
public class DAOFunc {

    private static Map<String, Connection> connections;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private HttpServletRequest request;

    //сделать кнопку для смены базы

    public String getAllNameTables(String adr, String user, String pass) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {  //вывод названий таблиц
        Connection conn = setConnIfNullAndReturn(adr, user, pass);
        Statement a1 = conn.createStatement();
        ResultSet rs = null;
        if (adr.contains("mysql")) {
            rs = a1.executeQuery("show tables");
        }
        if (adr.contains("postgresql")) {
            rs = a1.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';");
        }
        if (adr.contains("sqlite")) {
            rs = a1.executeQuery("SELECT NAME FROM sqlite_master WHERE type = 'table'");
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
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "bestuser");
        PreparedStatement statement = conn.prepareStatement("INSERT my_db.polz(username, password) VALUES (?,?)");
        statement.setString(1, username);
        statement.setString(2, password);
        statement.executeUpdate();
    }

    public boolean login(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "bestuser");
        PreparedStatement statement = conn.prepareStatement("SELECT ? FROM my_db.polz where username = ?");
        statement.setString(2, username);
        statement.setString(1, password);
        ResultSet resultSet = statement.executeQuery();
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


    public Connection setConnIfNullAndReturn(String adr, String user, String pass) throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (connections == null) {
            connections = new HashMap<>();
        }
        HttpSession session = request.getSession();
        Object username = session.getAttribute("username");
        connections.put(username.toString(), DriverManager.getConnection(adr, user, pass));
//        if (adr.contains("postgres")) {
//                Class.forName("org.postgresql.Driver");
//                connections.put(user, DriverManager.getConnection(adr, user, pass));
//        }
//        if (adr.contains("mysql")) {
//                Class.forName("com.mysql.cj.jdbc.Driver");
//                connections.put(user, DriverManager.getConnection(adr, user, pass));
//        }
//        if (adr.contains("sqlite")) {
//                Class.forName("org.sqlite.JDBC");
//                connections = DriverManager.getConnection(adr);
//        }
            System.out.println("------------------------------------------- EMployeeeDAOIMPL::getConnection");
            System.out.println("URL = " + adr);
            System.out.println("username = " +user);
            System.out.println("password = " +pass);

        return connections.get(username.toString());
    }

    public void saveQuery(String query, String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Connection conn = setConnIfNullAndReturn("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "bestuser");
        Date date = new Date();
        PreparedStatement statement = conn.prepareStatement("INSERT my_db.history(query, username, time) VALUES (?,?,?)");
        statement.setString(2, username);
        statement.setString(1, query);
        statement.setString(3, date.toString());
        statement.executeUpdate();

    }

    public StringBuilder showHistory() throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Connection conn =  setConnIfNullAndReturn("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "bestuser");
        PreparedStatement statement = conn.prepareStatement("SELECT query, time FROM my_db.history where username = ?");
        HttpSession session = request.getSession();
        String userrr = session.getAttribute("username").toString();
        statement.setString(1, userrr);
        ResultSet resultSet = statement.executeQuery();
        StringBuilder result =  new StringBuilder();

        while (resultSet.next()) {
            result.append(resultSet.getString("query")).append(resultSet.getString("time")).append("\n");
        }

        return result;
    }

    public RowsAndCols query(String query, String adr, String user, String pass) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException { //вывод таблицы(строчки)
        Connection conn = setConnIfNullAndReturn(adr, user, pass);
        Statement statement = conn.createStatement();
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> cols = new ArrayList<String>();
        if (query.contains("INSERT") || query.contains("DELETE")) {
            statement.executeUpdate(query);
        }
        else {
            ResultSet result = statement.executeQuery(query);

            ResultSetMetaData rsmd = result.getMetaData();
            int columnCount = rsmd.getColumnCount();
            rows = getRows(result, columnCount);
            cols = getCols(rsmd, columnCount);
        }
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
