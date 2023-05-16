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
import java.util.*;

@org.springframework.stereotype.Controller
@RequestMapping("/api")
public class Controller {


    private Map<String, String> db;

    @Autowired
    private DAOFunc logic;

    @GetMapping("/Table")
    public ModelAndView showHomePage(Model model, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
            return new ModelAndView("redirect:/api/login");
        }
        String nameTables = logic.getAllNameTables(db.get("adr"), db.get("user"), db.get("pass"));
        model.addAttribute("nameTables", nameTables);
        return new ModelAndView("Table");
    }



    @GetMapping("/home")
    public ModelAndView showHomePageGet(String adr, String user, String pass, Boolean isStored, Model model,  HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
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
        if (request.getCookies() == null) {
            return new ModelAndView("redirect:/api/login");
        }
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



    @GetMapping("/registration")
    public ModelAndView showRegistrationPage(Model model, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
            return new ModelAndView("redirect:/api/login");
        }
        return new ModelAndView("registration");
    }

    @PostMapping("/registration")
    public ModelAndView registrationPost(String username, String password, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
            return new ModelAndView("redirect:/api/login");
        }
        DAOFunc.registration(username, password);
        return new ModelAndView("redirect:/api/home?username=" + username + "&password=" + password);
    }


    public String getUUID(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("id")) {
                return cookie.getValue();
            }
        }

        return null;
    }
    @PostMapping("/login")
    public ModelAndView loginPost(String username, String password, HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        String cook = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("id")) {
                    if (logic.login(username, password)) {
                        return new ModelAndView("redirect:/api/home");
                    }
                    cook = cookie.getValue();
                }
            }
        }
        if (cook == null) {
            UUID uuid = Generators.timeBasedGenerator().generate();
            final String cookieName = "id";
            final String cookieValue = uuid.toString();
            final Boolean useSecureCookie = false;
            final int expiryTime = 60 * 60 * 24;
            final String cookiePath = "/";

            Cookie cookie = new Cookie(cookieName, cookieValue);

            cookie.setSecure(useSecureCookie);

            cookie.setMaxAge(expiryTime);

            cookie.setPath(cookiePath);

            response.addCookie(cookie);
        }
        System.out.println(cook);
        if (logic.login(username, password)) {
            return new ModelAndView("redirect:/api/home");
        }
        return new ModelAndView("redirect:/api/login");
    }




    @GetMapping("/logout")
    public ModelAndView showLogout(HttpServletRequest request) {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
            return new ModelAndView("redirect:/api/login");
        }
        return new ModelAndView("logout");
    }

    @GetMapping("/login")
    public ModelAndView showloginPage(HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
            return new ModelAndView("redirect:/api/login");
        }
        return new ModelAndView("login");
    }



    @PostMapping("/allTables")
    public ModelAndView allTables(Model model, String query, MultipartFile file, HttpServletRequest request) throws SQLException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, ServletException {
        String uuid = getUUID(request);
        if (request.getCookies() == null) {
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

        RowsAndCols rowsAndCols = logic.query(query, db.get("adr"), db.get("user"), db.get("pass"));
        model.addAttribute("rows", rowsAndCols.getRows());
        model.addAttribute("column", rowsAndCols.getCols());
        return new ModelAndView("showTable");
    }


}