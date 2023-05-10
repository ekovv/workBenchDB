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

    private Connection connection;


    @Autowired
    private EntityManager entityManager;


    public String getAllNameTables(String adr, String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {  //вывод названий таблиц
        setConnIfNull(adr, username, password);
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


    public void setConnIfNull(String adr, String username, String password) throws SQLException, IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(adr, username, password);
            System.out.println("------------------------------------------- EMployeeeDAOIMPL::getConnection");
            System.out.println("URL = " + adr);
            System.out.println("username = " +username);
            System.out.println("password = " +password);

        }
    }

//    public static String readFile(String path) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
//        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
//    }








    public RowsAndCols query(String query, String adr, String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException { //вывод таблицы(строчки)
        setConnIfNull(adr, username, password);
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



//        Connection connection = getConnection();
//        PreparedStatement callableStatement =
//                connection.prepareCall("{call calculateStatistics(?, ?)}");
//        Statement statement = connection.createStatement();
//        String query = "SELECT * FROM my_db.employees";
//        ResultSet result = statement.executeQuery(query);
//        ResultSetMetaData rsmd = result.getMetaData();
//        List<String> columnNames = new ArrayList<String>();
//        int ColumnCount = rsmd.getColumnCount();
//        for (int i = 1; i < ColumnCount + 1; i++) {
//            columnNames.add(rsmd.getColumnName(i));
//        }
//        return columnNames;
//    }


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




//        ResultSetMetaData rsmd = result.getMetaData();

//        while (result.next()) {
//            int id = result.getInt(1);
//            String name = result.getString(2);
//            String surname = result.getString(3);
//            String department = result.getString(4);
//            int salary = result.getInt(5);
//            System.out.printf("%d %s %s %s %d \n", id, name,surname,department,salary);
//        }
//        List<String> myTable= new ArrayList<String>();
//       for (int i = 0; i < rowCount; i++) {
//           myTable.add(result.getString(i));
//       }
//        System.out.println(myTable);

//        for(int i=0; i < rsmd.getColumnCount();i++) {
//            int id = result.getInt(1);
//            String name = result.getString(2);
//            String surname = result.getString(3);
//            String department = result.getString(4);
//            int salary = result.getInt(5);
//            System.out.printf("%d %s %s %s %d \n", id, name,surname,department,salary);
//        }