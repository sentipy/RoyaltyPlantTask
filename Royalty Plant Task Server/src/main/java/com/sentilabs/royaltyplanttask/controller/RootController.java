package com.sentilabs.royaltyplanttask.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sentipy on 05/07/15.
 */

@Controller
@RequestMapping(value = "/")
public class RootController {

    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("message", "HELLO!");
        return "index";
    }
}
