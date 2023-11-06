package ee.pw.testowanie1.accceptanceTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.pw.testowanie1.models.PostCreateDTO;
import ee.pw.testowanie1.services.PostService;
import io.restassured.RestAssured;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
