package com.example.webchat.userinterface;

import com.example.webchat.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserInterfaceController {

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        if (user == null) {
            return "redirect:login";
        }
        model.addAttribute("nickname", user.getUsername());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}