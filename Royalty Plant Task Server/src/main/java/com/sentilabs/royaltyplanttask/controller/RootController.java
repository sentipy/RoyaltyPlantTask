package com.sentilabs.royaltyplanttask.controller;

import com.sentilabs.royaltyplanttask.dao.interfaces.AccountDAO;
import com.sentilabs.royaltyplanttask.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by sentipy on 05/07/15.
 */

@Controller
@RequestMapping(value = "/")
public class RootController {

    @Autowired
    private AccountDAO accountDAO;

    @RequestMapping(value = "/")
    public String index(Model model, HttpServletRequest httpServletRequest) {
        model.addAttribute("username", httpServletRequest.getUserPrincipal().getName());
        return "index";
    }
}
