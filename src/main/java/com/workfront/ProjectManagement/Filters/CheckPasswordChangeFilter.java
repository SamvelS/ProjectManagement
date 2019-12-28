package com.workfront.ProjectManagement.Filters;

import com.workfront.ProjectManagement.domain.ProjectUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CheckPasswordChangeFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // if user is authenticated
        if(principal instanceof ProjectUserDetails) {
            ProjectUserDetails userDetails = (ProjectUserDetails) principal;
            HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
            String changePasswordURI = "/users/changePassword";

            if(!httpRequest.getRequestURI().endsWith(".css")
                    && !httpRequest.getRequestURI().endsWith(".js")
                    && userDetails.forceToChangePassword() && !httpRequest.getRequestURI().equalsIgnoreCase(changePasswordURI)) {
                HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
                httpResponse.sendRedirect(changePasswordURI);
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
