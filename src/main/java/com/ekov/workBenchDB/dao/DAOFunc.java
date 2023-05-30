package com.ekov.workBenchDB.dao;

import jakarta.persistence.EntityManager;
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
    private HttpServletRequest request;


    public void registration(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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



    public Connection setConnIfNullAndReturn(String adr, String user, String pass, String username) throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (connections == null) {
            connections = new HashMap<>();
        }

        connections.put(username.toString(), DriverManager.getConnection(adr, user, pass));
        System.out.println("------------------------------------------- EMployeeeDAOIMPL::getConnection");
        System.out.println("URL = " + adr);
        System.out.println("username = " +user);
        System.out.println("password = " +pass);

        return connections.get(username.toString());
    }

    public void saveQuery(String query, String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Connection conn = setConnIfNullAndReturn("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "bestuser", username);
        Date date = new Date();
        PreparedStatement statement = conn.prepareStatement("INSERT my_db.history(query, username, time) VALUES (?,?,?)");
        statement.setString(2, username);
        statement.setString(1, query);
        statement.setString(3, date.toString());
        statement.executeUpdate();

    }

    public StringBuilder showHistory(String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Connection conn =  setConnIfNullAndReturn("jdbc:mysql://localhost/my_db?serverTimezone=Europe/Moscow&useSSL=false", "root", "bestuser", username);
        PreparedStatement statement = conn.prepareStatement("SELECT query, time FROM my_db.history where username = ?");
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();
        StringBuilder result =  new StringBuilder();

        while (resultSet.next()) {
            result.append(resultSet.getString("query")).append(resultSet.getString("time")).append("\n");
        }

        return result;
    }


    public List<List<String>> getRows(ResultSet result, int ColumnCount) throws SQLException, IOException {
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

    public List<String> getCols(ResultSetMetaData rsmd, int ColumnCount) throws SQLException, IOException { //вывод колоноки(столбца)
        List<String> columnNames = new ArrayList<String>();
        for (int i = 1; i < ColumnCount + 1; i++) {
            columnNames.add(rsmd.getColumnName(i));
        }
        return columnNames;
    }
}
