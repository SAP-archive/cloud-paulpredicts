package com.sap.pto.services;

import java.util.Calendar;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;

import com.google.gson.JsonObject;
import com.sap.pto.util.Consts;
import com.sap.pto.util.UserUtil;

/**
 * This class is used for returning information about the application.
 */
@Path("/systemservice")
public class SystemService extends BasicService {
    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSytemInfo() {
        JsonObject info = new JsonObject();
        info.addProperty("currenttime", DateTime.now().toString(Consts.formatter));
        info.addProperty("defaulttimezone", Calendar.getInstance().getTimeZone().getID());
        info.addProperty("dateformat", Consts.DATEFORMAT);
        info.addProperty("version", Consts.VERSION);
        info.addProperty("paulid", UserUtil.getPaul().getId());
        info.addProperty("adminmode", userUtil.isAdmin(request));
        info.addProperty("shiromode", UserUtil.isShiroActive());

        return info.toString();
    }

}
