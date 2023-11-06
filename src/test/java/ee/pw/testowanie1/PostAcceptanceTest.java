package ee.pw.testowanie1;

import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@SpringBootTest
@Transactional
@Rollback
public class PostAcceptanceTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void deletePostEndpointShouldReturnOkStatus() {

        UUID id = UUID.randomUUID();

        given()
                .when()
                .delete("/api/posts/" + id)
                .then()
                .statusCode(200);
    }

    @Test
    public void deletePostEndpointShouldReturnBadRequestStatus() {

        given()
                .when()
                .get("/api/posts/1")
                .then()
                .statusCode(400);
    }

}
