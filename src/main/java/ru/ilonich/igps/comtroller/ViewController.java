package ru.ilonich.igps.comtroller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ViewController {

    //Angular routes
    @RequestMapping({
            "/",
            "/profile",
            "/confirm-email/{token:\\w+}",
            "/confirm-reset/{token:\\w+}",
            "/users",
            "/users/{id:\\d+}",
            "/404",
            "/profile"
    })
    public String index() {
        return "forward:/index.html";
    }
}
