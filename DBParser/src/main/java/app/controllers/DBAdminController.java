package app.controllers;

import app.lib.UserLib;
import app.utils.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class DBAdminController {
    /**
     * Display article search page.
     * @param model
     * @return Article search page
     */
    @GetMapping("/db_admin")
    public String displayDBAdminPage(Model model, HttpServletRequest request) {
        User user = UserLib.extractUser(request);
        model.addAttribute("user", user);

        return "dbAdmin/db_admin";
    }

}
