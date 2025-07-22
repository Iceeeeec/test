package com.qqdemo.demos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    /**
     * 首页
     */
    @GetMapping("/")
    public String index(HttpSession session, Model model) {
        Object qqUser = session.getAttribute("qqUser");
        if (qqUser != null) {
            model.addAttribute("userInfo", qqUser);
            model.addAttribute("isLogin", true);
        } else {
            model.addAttribute("isLogin", false);
        }
        return "index";
    }
} 