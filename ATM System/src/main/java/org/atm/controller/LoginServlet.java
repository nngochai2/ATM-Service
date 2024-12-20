package org.atm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login/*")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if ("/user".equals(pathInfo)) {
            request.getRequestDispatcher("/WEB-INF/views/user/login.jsp").forward(request, response);
        } else if ("/admin".equals(pathInfo)) {
            request.getRequestDispatcher("/WEB-INF/views/admin/login.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath());
        }
    }
}
