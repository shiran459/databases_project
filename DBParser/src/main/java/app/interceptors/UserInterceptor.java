package app.interceptors;

import app.lib.UserLib;
import app.utils.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Cookie cookie = WebUtils.getCookie(request, "session_token");
        User user = null;
        if (cookie != null)
            user = UserLib.currentUser(cookie.getValue());

        request.setAttribute("current_user", user);
        return true;
    }

}
