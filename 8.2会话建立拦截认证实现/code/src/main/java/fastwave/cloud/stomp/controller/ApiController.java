package fastwave.cloud.stomp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @PostMapping("/user/list")
    public String getUserList()
    {
        return "user list";
    }

    @GetMapping("/user/add")
    public String getUserAdd()
    {
        return "user add";
    }
}
