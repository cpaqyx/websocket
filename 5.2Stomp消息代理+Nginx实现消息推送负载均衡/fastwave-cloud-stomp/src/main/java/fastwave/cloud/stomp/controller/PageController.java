package fastwave.cloud.stomp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/show")
    public String show()
    {
        return "/show.html";
    }
}
