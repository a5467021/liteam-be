package com.liteam.utils;

import com.liteam.entity.User;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

public class BaseController {
    @Resource
    protected HttpServletRequest request;

    protected User getUser() {
        return (User) this.request.getAttribute("user");
    }
}
