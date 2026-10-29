package repro;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.startsWith;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(ProfileBTest.Profile.class)
class ProfileBTest {
    public static class Profile implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of("repro.profile", "B");
        }
    }

    @Test
    void hello() {
        System.out.println(">>> TEST START ProfileBTest at " + java.time.LocalTime.now());
        get("/hello").then().statusCode(200).body(startsWith("web port from Compose dev service: "));
    }
}
