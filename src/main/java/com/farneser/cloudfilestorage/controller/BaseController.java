package com.farneser.cloudfilestorage.controller;

import jakarta.servlet.http.HttpSession;

public class BaseController {

    protected String getCurrentPath(HttpSession session) {
        var path = session.getAttribute("path");

        if (path == null) {
            return "/";
        }

        return path.toString();
    }
}
