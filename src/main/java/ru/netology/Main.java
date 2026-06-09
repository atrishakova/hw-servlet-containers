package ru.netology;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        var context = tomcat.addContext("", null);

        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(ru.netology.config.AppConfig.class);

        DispatcherServlet servlet = new DispatcherServlet(appContext);

        Tomcat.addServlet(context, "dispatcher", servlet);
        context.addServletMappingDecoded("/", "dispatcher");

        tomcat.start();
        System.out.println("Server started at http://localhost:8080");
        tomcat.getServer().await();
    }
}
