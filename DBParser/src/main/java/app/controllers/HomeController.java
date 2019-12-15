package app.controllers;

import app.lib.UserLib;
import app.utils.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controls the homepage.
 */
@Controller
public class HomeController {

    /**
     * Displays the homepage.
     *
     * @param model
     * @return
     */
    @GetMapping("/")
    public String displayHomepage(HttpServletRequest request, Model model) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

        return "index.html";
    }

}

