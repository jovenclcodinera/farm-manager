package com.farmmanager.farmmanager.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("viewName", "home");
        mv.addObject("fragmentName", "home");
        mv.addObject("title", "Home");

        return mv;
    }
}
