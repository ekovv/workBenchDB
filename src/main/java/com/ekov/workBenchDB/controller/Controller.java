package com.ekov.workBenchDB.controller;

import com.ekov.workBenchDB.dao.RowsAndCols;
import com.ekov.workBenchDB.dao.DAOFunc;
import jakarta.servlet.ServletException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Controller
@RequestMapping("/api")
public class Controller {

    private Map<String, String> db;

    @Autowired
    private DAOFunc employeeService;


    @GetMapping("/Table")
    public ModelAndView showHomePage(Model model) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        String nameTables = employeeService.getAllNameTables(db.get("adr"), db.get("user"), db.get("pass"));
        model.addAttribute("nameTables", nameTables);
        return new ModelAndView("Table");
    }

    @GetMapping("/home")
    public ModelAndView showHomePageGet(String adr, String user, String pass, Boolean isStored, Model model) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (db == null) {
            isStored = false;
        }
        else {
            isStored = true;
        }
        model.addAttribute("isStored", isStored);
        return new ModelAndView("home");
    }

    @PostMapping("/home")
    public ModelAndView showHomePagePost(@RequestBody String query, String adr, String user, String pass, Boolean isStored, Model model, MultipartFile file) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        isStored = false;
        if (db == null) {
            db = new HashMap<>();
            db.put("adr", adr);
            db.put("user", user);
            db.put("pass", pass);
        }
        else {
            isStored = true;
            adr = db.get(adr);
            user = db.get(user);
            pass = db.get(pass);
        }
        return new ModelAndView("redirect:/api/home?query=" + query + "&adr=" + adr + "&user=" + user + "&pass=" + pass + "&isStored=" + isStored + "&file=" + file);
    }


    @PostMapping("/register")
    public ModelAndView registerPost(String username, String password) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (DAOFunc.login(username, password)) {
            return new ModelAndView("redirect:/api/register");
        }
        return new ModelAndView("redirect:/api/home?username=" + username + "&password=" + password);
    }



    @GetMapping("/register")
    public ModelAndView showRegisterPage(Model model) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return new ModelAndView("register");
    }

    @PostMapping("/allTables")
    public ModelAndView allTables(Model model, String query, MultipartFile file) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException {
        String content = null;
        if (file != null) {
            InputStream inputStream = file.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            content = new String(bdata, StandardCharsets.UTF_8);
        }

        if (content != null) {
            query = content;
        }

        RowsAndCols rowsAndCols = employeeService.query(query, db.get("adr"), db.get("user"), db.get("pass"));
        model.addAttribute("rows", rowsAndCols.getRows());
        model.addAttribute("column", rowsAndCols.getCols());
        return new ModelAndView("showTable");
    }

}






//url = jdbc:mysql://localhost/my_db?characterEncoding=utf8
//        username = bestuser
//        password = bestuser
//
//        urlP = jdbc:postgresql://localhost:5432/postgres
//        usernameP=bestuser
//        passwordP=bestuser