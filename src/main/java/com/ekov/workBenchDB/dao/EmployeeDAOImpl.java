package com.ekov.workBenchDB.dao;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {


    @Autowired
    private EntityManager entityManager;


//    @Override
//    public List<Employee> getAllEmployees() {
//
//        Session session = entityManager.unwrap(Session.class);
////        List<Employee> allEmployees = session.createQuery("from Employee", Employee.class).getResultList();
//        Query<Employee> query = session.createQuery("from Employee", Employee.class);
//
//        return query.getResultList();
//    }

    @Override
    public StringBuilder getAllNameTables() throws SQLException {

        String filePath = "/Users/dmitrydenisov/IdeaProjects/workBenchDB/Info/DataConnectionInfo";

        String content = null;
        List<String> lines = new ArrayList<>();
        try {
            content = readFile(filePath);
            lines = List.of(content.split(","));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(lines.get(0), lines.get(1), lines.get(2));
        Statement a1 = connection.createStatement();
        ResultSet rs = a1.executeQuery("Show tables");
        System.out.println("Tables in the current database: ");
        StringBuilder myTables = new StringBuilder();
        while(rs.next()) {
            myTables.append(rs.getString(1) + "\n");
        }



        connection.close();
        return myTables;
    }

    public static String readFile(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
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
        EmployeeDAOImpl that = (EmployeeDAOImpl) o;
        return Objects.equals(entityManager, that.entityManager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityManager);
    }
}