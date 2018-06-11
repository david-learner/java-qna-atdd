package com.hardlearner.javaqnaatdd.domain;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/users/form")
    public String userSignUp() {
        return "/user/form";
    }
}
