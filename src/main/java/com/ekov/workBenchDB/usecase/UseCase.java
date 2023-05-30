package com.ekov.workBenchDB.usecase;


import org.springframework.stereotype.Repository;
import com.ekov.workBenchDB.dao.RowsAndCols;
import com.ekov.workBenchDB.dao.DAOFunc;
import java.sql.*;
import java.util.*;

@Repository
public class UseCase {


    private final DAOFunc dao;


    public UseCase(DAOFunc dao) {

        this.dao = dao;
    }


    private RowsAndCols query(String query, String adr, String user, String pass, String username) throws SQLException {
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

    private String getAllNameTables(String adr, String user, String pass, String username) throws SQLException {
        Connection conn = dao.setConnIfNullAndReturn(adr, user, pass, username);
        Statement st = conn.createStatement();
        ResultSet rs = null;
        if (adr.contains("mysql")) {
            rs = st.executeQuery("show tables");
        }
        if (adr.contains("postgresql")) {
            rs = st.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';");
        }
        if (adr.contains("sqlite")) {
            rs = st.executeQuery("SELECT NAME FROM sqlite_master WHERE type = 'table'");
        }
        StringBuilder myTables = new StringBuilder();
        try {
            while (rs.next()) {
                myTables.append(rs.getString(1)).append("\n");
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        StringBuilder res = new StringBuilder(myTables);
        String resStr = res.toString();
        return resStr;
    }


    private boolean login(String username, String password) throws SQLException {
        return dao.login(username, password);
    }

    private void saveQuery(String query, String username) throws SQLException {
        dao.saveQuery(query, username);
    }

    private StringBuilder showHistory(String username) throws SQLException {
        return dao.showHistory(username);
    }

    private void registration(String username, String password) throws SQLException {
        dao.registration(username, password);
    }


    public RowsAndCols getQuery(String query, String adr, String user, String pass, String username) throws SQLException {
        return query(query, adr, user, pass, username);
    }

    public String getGetAllNameTables(String adr, String user, String pass, String username) throws SQLException {
        return getAllNameTables(adr, user, pass, username);
    }

    public boolean getLogin(String username, String password) throws SQLException {
        return login(username, password);
    }

    public void getSaveQuery(String query, String username) throws SQLException {
        saveQuery(query, username);
    }

    public StringBuilder getShowHistory(String username) throws SQLException {
        return showHistory(username);
    }

    public void getRegistration(String username, String password) throws SQLException {
        registration(username, password);
    }

}
