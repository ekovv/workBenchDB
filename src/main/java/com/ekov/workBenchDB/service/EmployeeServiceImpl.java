package com.ekov.workBenchDB.service;


import com.ekov.workBenchDB.dao.EmployeeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class EmployeeServiceImpl implements EmployeeService{

    @Autowired
    private EmployeeDAO employeeDAO;

//    @Override
//    @Transactional
//    public List<Employee> getAllEmployees() {
//        return employeeDAO.getAllEmployees();
//    }

    @Override
    public StringBuilder getAllNameTables() throws SQLException {
        return employeeDAO.getAllNameTables();
    }

}
