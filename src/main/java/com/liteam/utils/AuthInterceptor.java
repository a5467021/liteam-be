package com.liteam.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liteam.entity.User;
import com.liteam.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final UserService userService;

    @Autowired
    public AuthInterceptor(UserService userService) {
        this.userService = userService;
    }

    /**
     * 验证Token，检查用户是否登入
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null) {
            unAuth(response);
            return false;
        }
        User user = userService.verifyToken(token);
        if (user == null) {
            unAuth(response);
            return false;
        } else {
            request.setAttribute("user", user);
            return true;
        }
    }

    /**
     * 设置response相应
     * @param response
     * @throws IOException
     */
    private void unAuth(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        Map<String, Object> msg = new HashMap<>();
        msg.put("status", 401);
        msg.put("message", "Unauthorized");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), msg);
    }
}
