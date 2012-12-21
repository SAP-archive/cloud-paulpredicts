package com.sap.pto.startup;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.sap.pto.services.util.GsonMessageBodyHandler;
import com.sap.pto.util.Consts;
import com.sap.pto.util.UserUtil;
import com.sap.pto.util.configuration.ConfigUtil;

@SuppressWarnings("nls")
public class UserFilter implements Filter {
    public UserFilter() {
    }

    @Override
    public void destroy() {
        return;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        saveHostInfo(httpRequest);
        GsonMessageBodyHandler.setSkipDataProtection(false);

        // pass the request along the filter chain
        chain.doFilter(request, response);

        UserUtil.cleanUp();
    }

    private void saveHostInfo(HttpServletRequest httpRequest) {
        String url = httpRequest.getRequestURL().toString();
        if (url.indexOf("/", 10) > 0) {
            url = url.substring(0, url.indexOf("/", 10));
        }
        ConfigUtil.setTempProperty(Consts.SERVERNAME_PROPERTY_KEY, url);
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
        return;
    }

}
