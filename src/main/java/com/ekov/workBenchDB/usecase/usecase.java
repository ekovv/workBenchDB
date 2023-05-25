package com.ekov.workBenchDB.usecase;

import org.springframework.util.FileCopyUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

public class usecase {

    public String readFileWithSQL(MultipartFile file) {
        String content = null;
        try {
            InputStream inputStream = file.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            content = new String(bdata, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return content;
    }

}
