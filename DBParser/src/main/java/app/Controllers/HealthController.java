package app.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controls server testing.
 */
@Controller
public class HealthController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
