package lk.dropti.ninewood.entity.config;

import org.glassfish.jersey.server.ResourceConfig;

public class AppConfig extends ResourceConfig {
    public AppConfig(){
        packages("lk.dropti.ninewood.controller");
        packages("lk.dropti.ninewood.middleware");
    }
}
