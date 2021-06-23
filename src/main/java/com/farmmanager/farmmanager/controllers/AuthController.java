package com.farmmanager.farmmanager.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmmanager.farmmanager.models.Alert;
import com.farmmanager.farmmanager.models.User;
import com.farmmanager.farmmanager.repositories.UserRepository;

@Controller
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/sign-in")
    @PreAuthorize("! isAuthenticated()")
    public ModelAndView signIn(@RequestParam(required = false) boolean error, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("viewName", "auth/login");
        mv.addObject("fragmentName", "login");
        mv.addObject("title", "Login");
        mv.addObject("user", new User());
        
        if (error == true) {
        	mv.addObject("alert", new Alert("error", (String) request.getSession().getAttribute("errorMessage")));
        	request.getSession().removeAttribute("errorMessage");
        }
        
        return mv;
    }

    @GetMapping("/sign-up")
    @PreAuthorize("! isAuthenticated()")
    public ModelAndView signUp() {
        ModelAndView mv = new ModelAndView("index");
        mv.addObject("viewName", "auth/register");
        mv.addObject("fragmentName", "register");
        mv.addObject("title", "Register");
        mv.addObject("user", new User());

        return mv;
    }

    @PostMapping("/register")
    @PreAuthorize("! isAuthenticated()")
    public ModelAndView register(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("alert", new Alert("error", "User: " + user.getUsername() + " already exists"));

            return new ModelAndView("redirect:/sign-up");
        } else if (userRepository.findByEmail(user.getEmail()) != null) {
        	redirectAttributes.addFlashAttribute("alert", new Alert("error", "Email: " + user.getEmail() + " already exists"));

            return new ModelAndView("redirect:/sign-up");
        }

        String encodedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("alert", new Alert("success", "User: " + user.getUsername() + " successfully registered. Please log in to continue"));

        return new ModelAndView("redirect:/sign-in");
    }
}
