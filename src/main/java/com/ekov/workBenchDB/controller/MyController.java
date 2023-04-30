package com.ekov.workBenchDB.controller;

import com.ekov.workBenchDB.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
public class MyController {



    @Autowired
    private EmployeeService employeeService;

//    @RequestMapping("/")
//    public List<Employee> showAllEmployees()  {
//        List<Employee> allEmployees = employeeService.getAllEmployees();
//        return allEmployees;
//    }

    @RequestMapping("/nameTables")
    public StringBuilder showNameTables() throws SQLException {
        return employeeService.getAllNameTables();
    }
}