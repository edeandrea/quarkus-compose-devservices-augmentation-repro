package repro;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/hello")
public class HelloResource {
    @ConfigProperty(name = "repro.web.port", defaultValue = "unset")
    String webPort;

    @GET
    public String hello() {
        return "web port from Compose dev service: " + webPort;
    }
}
