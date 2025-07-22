package com.qqdemo.demos.controller;

import com.qqdemo.demos.utils.QQConnectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/qq")
@Slf4j
public class QQLoginController {

    @Autowired
    private QQConnectUtils qqConnectUtils;

    /**
     * QQ登录授权
     */
    @GetMapping("/login")
    public String qqLogin() {
        String authorizeUrl = qqConnectUtils.getAuthorizeUrl();
        log.info("QQ授权地址: {}", authorizeUrl);
        return "redirect:" + authorizeUrl;
    }

    /**
     * QQ登录回调处理
     */
    @GetMapping("/callback")
    public String qqCallback(@RequestParam("code") String code, 
                            @RequestParam(value = "state", required = false) String state,
                            HttpSession session, Model model) {
        try {
            log.info("QQ登录回调，授权码: {}", code);
            
            // 1. 通过授权码获取access_token
            String accessToken = qqConnectUtils.getAccessToken(code);
            log.info("获取到access_token: {}", accessToken);
            
            // 2. 通过access_token获取openid
            String openid = qqConnectUtils.getOpenId(accessToken);
            log.info("获取到openid: {}", openid);
            
            // 3. 通过access_token和openid获取用户信息
            Map<String, Object> userInfo = qqConnectUtils.getUserInfo(accessToken, openid);
            log.info("获取到用户信息: {}", userInfo);
            
            // 4. 保存用户信息到session
            session.setAttribute("qqUser", userInfo);
            session.setAttribute("accessToken", accessToken);
            session.setAttribute("openid", openid);
            
            // 5. 跳转到成功页面
            model.addAttribute("userInfo", userInfo);
            return "success";
            
        } catch (Exception e) {
            log.error("QQ登录回调处理失败", e);
            model.addAttribute("error", "登录失败: " + e.getMessage());
            return "error";
        }
    }

    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
} 