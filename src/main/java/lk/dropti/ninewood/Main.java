package lk.dropti.ninewood;

import lk.dropti.ninewood.config.AppConfig;
//import lk.dropti.ninewood.listener.ContextPathListener;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.glassfish.jersey.servlet.ServletContainer;

import java.io.File;

public class Main {

    private static final int SERVER_PORT = 8081;
    private static final String CONTEXT_PATH = "/ninewood";

    public static void main(String[] args) {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(SERVER_PORT);
            tomcat.getConnector();

            Context context = tomcat.addWebapp("/ninewood", new File("src/main/webapp/").getAbsolutePath());
            Tomcat.addServlet(context, "JerseyServlet", new ServletContainer(new AppConfig()));
            context.addServletMappingDecoded("/api/*", "JerseyServlet");

//          context.addApplicationListener(ContextPathListener.class.getName());

            tomcat.start();
            System.out.println("App URL: http://localhost:" + "8081" + "/ninewood");
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            throw new RuntimeException("Tomcat Embedded Server loading failed: " + e.getMessage());
        }
    }
}
