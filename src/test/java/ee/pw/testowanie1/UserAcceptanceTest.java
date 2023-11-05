package ee.pw.testowanie1;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@SpringBootTest
public class UserAcceptanceTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void CheckTheEndpointWithCorrectIdTest() {

        UUID id = UUID.randomUUID();

        // Delete the post
        given()
                .when()
                .delete("/api/users/" + id)
                .then()
                .statusCode(200);
    }

    @Test
    public void CheckTheEndpointWithWrongIdTest() {

        given()
                .when()
                .get("/api/users/1")
                .then()
                .statusCode(400);
    }
}
