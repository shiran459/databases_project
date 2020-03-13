package app.interceptors;

import app.lib.UserLib;
import app.utils.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GuestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        User user = (User) request.getAttribute("current_user");
        if (user == null){
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

}