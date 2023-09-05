package com.farmmanager.farmmanager.utilities;

import javax.servlet.http.HttpServletRequest;

import com.farmmanager.farmmanager.models.Alert;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Controller
public class MyExceptionsHandler {
    
    @ExceptionHandler(AccessDeniedException.class)
    public final ModelAndView handleAccessDeniedException(Exception ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        switch (url.substring(StringUtils.ordinalIndexOf(url, "/", 3) + 1)) {
            case "sign-in":
            case "sign-up":
                redirectAttributes.addFlashAttribute("alert", new Alert("info", "You are already authenticated!"));
                break;
            default:
                redirectAttributes.addFlashAttribute("alert", new Alert("error", "You are not authorized to proceed with this request!"));
                break;
        }

        return new ModelAndView("redirect:/");
    }
}
