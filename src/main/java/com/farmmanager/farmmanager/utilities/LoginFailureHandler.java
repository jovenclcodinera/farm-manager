package com.farmmanager.farmmanager.utilities;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        String error = exception.getMessage();
        System.out.println("Login attempt failed with email: " + email + ". Reason: " + error);
        request.getSession().setAttribute("errorMessage", error);
        
        super.setDefaultFailureUrl("/sign-in?error=true");
        super.onAuthenticationFailure(request, response, exception);
    }
    
}
