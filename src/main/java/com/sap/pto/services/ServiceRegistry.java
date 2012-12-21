package com.sap.pto.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.sap.pto.services.util.GsonMessageBodyHandler;

/**
 * This class is used for declaring the restful services that are being offered.
 */
public class ServiceRegistry extends Application {
    private Set<Object> singletons = new HashSet<Object>();

    public ServiceRegistry() {
        singletons.add(new GsonMessageBodyHandler<Object>());

        singletons.add(new AdminService());
        singletons.add(new AnonUserService());
        singletons.add(new FixtureService());
        singletons.add(new LeagueService());
        singletons.add(new PredictionService());
        singletons.add(new SystemService());
        singletons.add(new TeamService());
        singletons.add(new UserService());
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
