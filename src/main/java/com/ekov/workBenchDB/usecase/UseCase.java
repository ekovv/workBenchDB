package com.ekov.workBenchDB.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import com.ekov.workBenchDB.dao.RowsAndCols;
import com.ekov.workBenchDB.dao.DAOFunc;
import jakarta.servlet.ServletException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

@Repository
public class UseCase {


    private DAOFunc dao;


    public UseCase(DAOFunc dao) {

        this.dao = dao;
    }


    public RowsAndCols query(String query, String adr, String user, String pass, String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException { //вывод таблицы(строчки)
        Connection conn = dao.setConnIfNullAndReturn(adr, user, pass, username);
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
            rows = dao.getRows(result, columnCount);
            System.out.println(rows);
            cols = dao.getCols(rsmd, columnCount);
            System.out.println(cols);
        }
        return new RowsAndCols(rows, cols);
    }

    public String getAllNameTables(String adr, String user, String pass, String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {  //вывод названий таблиц
        Connection conn = dao.setConnIfNullAndReturn(adr, user, pass, username);
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


    public boolean login(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return dao.login(username, password);
    }

    public void saveQuery(String query, String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        dao.saveQuery(query, username);
    }

    public StringBuilder showHistory(String username) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return dao.showHistory(username);
    }

    public void registration(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        dao.registration(username, password);
    }


}
