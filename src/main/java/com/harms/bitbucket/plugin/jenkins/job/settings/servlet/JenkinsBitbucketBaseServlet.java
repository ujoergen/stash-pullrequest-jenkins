package com.harms.bitbucket.plugin.jenkins.job.settings.servlet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.sal.api.auth.LoginUriProvider;

public class JenkinsBitbucketBaseServlet extends HttpServlet {
	
    private static final long serialVersionUID = 49L;
    
    protected final LoginUriProvider loginUriProvider;
    protected final AuthenticationContext authContext;
    
    public JenkinsBitbucketBaseServlet(LoginUriProvider loginUriProvider, AuthenticationContext authContext) {
        this.loginUriProvider = loginUriProvider;
        this.authContext = authContext;
    }
    
    protected void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        URI uri;

        try {
       final String contentType = request.getContentType();
       if (contentType != null && contentType.contains("application/x-www-form-urlencoded")) {
           uri = new URI(request.getHeader("referer"));
           response.setStatus(HttpServletResponse.SC_OK);
           response.setContentType("application/json");
           response.getWriter().print("{ "+
                   "\"redirect\" : \""+loginUriProvider.getLoginUri(uri).toASCIIString()+
                   "\"}");
           response.getWriter().flush();
       } else {
               uri = new URI(request.getRequestURI());
          response.sendRedirect(loginUriProvider.getLoginUri(uri).toASCIIString());
       }
        } catch (URISyntaxException e) {
            throw new ServletException(e.getMessage());
        }
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!authContext.isAuthenticated()) {
            redirectToLogin(req, resp);
            return;
        }
        super.service(req, resp);
    }
}
