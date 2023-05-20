package com.ekov.workBenchDB.controller;

import com.ekov.workBenchDB.dao.RowsAndCols;
import com.ekov.workBenchDB.dao.DAOFunc;
import com.fasterxml.uuid.Generators;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

@org.springframework.stereotype.Controller
@RequestMapping("/api")
public class Controller {


    private Map<String, Credential> db;


    @Autowired
    private DAOFunc logic;

    @GetMapping("/Table")
    public ModelAndView showHomePage(Model model, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");
        }
        Credential credential = db.get(uuid);
        String adr = credential.getAdr();
        String user = credential.getUsername();
        String pass = credential.getPassword();
        String nameTables = logic.getAllNameTables(adr, user, pass);
        model.addAttribute("nameTables", nameTables);
        return new ModelAndView("Table");
    }



    @GetMapping("/home")
    public ModelAndView showHomePageGet(String adr, String user, String pass, Boolean isStored, Model model,  HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");

        }
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
    public ModelAndView showHomePagePost(@RequestBody String query, String adr, String user, String pass, Boolean isStored, Model model, MultipartFile file, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");
        }

        isStored = false;
        if (db == null) {
            Credential credential = new Credential(adr, user, pass);
            db = new HashMap<>();
            db.put(uuid, credential);
        }
        else {
            isStored = true;
            Credential credential = db.get(uuid);
            adr = credential.getAdr();
            user = credential.getUsername();
            pass = credential.getPassword();
        }
        return new ModelAndView("redirect:/api/home?query=" + query + "&adr=" + adr + "&user=" + user + "&pass=" + pass + "&isStored=" + isStored + "&file=" + file);
    }



    @GetMapping("/registration")
    public ModelAndView showRegistrationPage(Model model, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");
        }
        return new ModelAndView("registration");
    }

    @PostMapping("/registration")
    public ModelAndView registrationPost(String username, String password, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            db.clear();
            return new ModelAndView("redirect:/api/login");
        }
        DAOFunc.registration(username, password);
        return new ModelAndView("redirect:/api/home?username=" + username + "&password=" + password);
    }


    public String getUUID(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object uuid = session.getAttribute("uuid");

        if (uuid == null) {
            return null;
        }


        return uuid.toString();
    }
    @PostMapping("/login")
    public ModelAndView loginPost(String username, String password, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");
        }
        if (logic.login(username, password)) {
            return new ModelAndView("redirect:/api/home");
        }
        return new ModelAndView("redirect:/api/login");
    }




    @PostMapping("/logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response,String adr,  String username, String password, Boolean isStored) {
        HttpSession session = request.getSession();
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        db = null;
        session.removeAttribute("uuid");
        session.invalidate();

        return new ModelAndView("redirect:/api/logout");
    }

    @GetMapping("/logout")
    public ModelAndView showLogout(HttpServletRequest request, HttpServletResponse response, String adr, String username, String password) {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");
        }
        db.clear();
        return new ModelAndView("logout");
    }

    @GetMapping("/login")
    public ModelAndView showloginPage(HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (uuid == null) {
            uuid = Generators.timeBasedGenerator().generate().toString();
            HttpSession session = request.getSession();
            session.setAttribute("uuid", uuid);
        }
        return new ModelAndView("login");
    }



    @PostMapping("/allTables")
    public ModelAndView allTables(Model model, String query, MultipartFile file, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException {
        String uuid = getUUID(request);
        if (uuid == null) {
            return new ModelAndView("redirect:/api/login");
        }
        String content = null;
        if (file != null) {
            InputStream inputStream = file.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            content = new String(bdata, StandardCharsets.UTF_8);
        }

        if (content != null) {
            query = content;
        }
        Credential credential = db.get(uuid);
        String adr = credential.getAdr();
        String user = credential.getUsername();
        String pass = credential.getPassword();
        RowsAndCols rowsAndCols = logic.query(query, adr, user, pass);
        model.addAttribute("rows", rowsAndCols.getRows());
        model.addAttribute("column", rowsAndCols.getCols());
        return new ModelAndView("showTable");
    }


}