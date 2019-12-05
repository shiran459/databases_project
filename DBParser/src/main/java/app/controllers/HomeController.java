package app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String displayHomepage(Model model) {
        return "index.html";
    }

}

