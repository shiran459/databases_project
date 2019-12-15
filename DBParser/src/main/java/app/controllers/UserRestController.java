package app.controllers;

import app.lib.UserLib;
import app.utils.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Controls pages belonging to the "Words" section of the NavBar.
 */
@RestController
public class UserRestController {

    @RequestMapping("/users/register")
    public User register(@RequestParam(required = true, name = "username") String username,
                           @RequestParam(required = true, name = "password") String password,
                           HttpServletResponse response) {
        try {
            User user = UserLib.register(username, password);
            if (user != null){
                Cookie cookie = new Cookie("session_token", user.token);
                response.addCookie(cookie);
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/users/login")
    public User login(@RequestParam(required = true, name = "username") String username,
                         @RequestParam(required = true, name = "password") String password,
                         HttpServletResponse response) {
        try {
            User user = UserLib.login(username, password);
            if (user != null){
                Cookie cookie = new Cookie("session_token", user.token);
                response.addCookie(cookie);
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
