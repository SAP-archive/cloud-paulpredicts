package com.sap.pto.services;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sap.pto.util.SecurityUtil;
import com.sap.pto.util.UserUtil;

/**
 * Contains helper functionalities, used by its subclasses for communication
 * between the server and the client through restful services.
 */
public abstract class BasicService {
    private static final Logger logger = LoggerFactory.getLogger(BasicService.class);
    protected static final Response RESPONSE_OK = Response.ok("ok").build();
    protected static final Response RESPONSE_BAD = Response.status(Status.BAD_REQUEST).build();

    protected Gson gson = new Gson();
    protected UserUtil userUtil = UserUtil.getInstance();

    @Resource
    protected HttpServletRequest request;

    @Resource
    protected HttpServletResponse response;

    @Context
    protected ServletContext context;

    protected String sanitize(String text) {
        return SecurityUtil.textOnly(StringUtils.trim(text));
    }

    protected void throwNotFound() {
        throwNotFound("Resource could not be found.");
    }

    protected void throwNotFound(String text) {
        throwError(Status.NOT_FOUND, text);
    }

    protected void throwUnauthorized() {
        throwUnauthorized("You do not have the permissions to perform this action.");
    }

    protected void throwUnauthorized(String text) {
        throwError(Status.UNAUTHORIZED, text);
    }

    protected void throwBadRequest() {
        throwBadRequest("The request could not be handled. Please check the parameters.");
    }

    protected void throwBadRequest(String text) {
        throwError(Status.BAD_REQUEST, text);
    }

    protected void throwError(Status status, String text) {
        Response response = Response.status(status).entity(text).type(MediaType.TEXT_PLAIN).build();

        throw new WebApplicationException(new IOException(text), response);
    }

    protected void alert(String alertText) {
        request.getSession().setAttribute("message", alertText);
        try {
            response.sendRedirect("/server/showAlert.jsp");
        } catch (IOException e) {
            logger.error("Alert message cannot be displayed.", e);
        }
    }

}
