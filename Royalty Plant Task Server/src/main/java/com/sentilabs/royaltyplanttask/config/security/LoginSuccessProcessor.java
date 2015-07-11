package com.sentilabs.royaltyplanttask.config.security;

import com.sentilabs.royaltyplanttask.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sentipy on 05/07/15.
 */

@Component
public class LoginSuccessProcessor extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , Authentication authentication) throws IOException, ServletException {
        httpServletRequest.getSession().setAttribute(
                Constants.SESSION_ATTR_NAME_WITH_CLIENT_ID
                , ((BankUser)authentication.getPrincipal()).getClientId());
        super.setDefaultTargetUrl("/");
        super.setAlwaysUseDefaultTargetUrl(true);
        super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
        /*httpServletRequest.getSession().setAttribute("_csrf"
                , httpServletRequest.getParameter("_csrf"));*/
        //httpServletResponse.sendRedirect("/welcome");
    }
}
