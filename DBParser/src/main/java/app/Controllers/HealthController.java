package app.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;

@Controller
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}