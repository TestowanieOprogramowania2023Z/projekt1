package ee.pw.testowanie1;

import ee.pw.testowanie1.models.UserCreateDTO;
import ee.pw.testowanie1.services.UserService;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import static org.hamcrest.Matchers.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@SpringBootTest
@Transactional
@Rollback
public class UserAcceptanceTest {

    @Autowired
    private UserService userService;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void shouldReturnOkStatus() {

        UUID id = UUID.randomUUID();

        given()
                .when()
                .delete("/api/users/" + id)
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldReturnBadRequestStatus() {

        given()
                .when()
                .get("/api/users/1")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldGetUsersWithValidPagination() {
        given()
                .param("page", 0)
                .param("size", 5)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void shouldHandleInvalidPagination() {
        given()
                .param("page", -1)
                .param("size", 10) // Invalid page, but valid size
                .when()
                .get("/api/users")
                .then()
                .statusCode(400);
    }

    @Test
    public void shouldReturnUsers() {

        UserCreateDTO user = UserCreateDTO.builder()
                .username("user")
                .email("user@user")
                .build();

        //Act
        UUID id = userService.createUser(user);
        System.out.println(userService.getUserById(id.toString()));

        Response response = given()
                .param("page", 0)
                .param("size", 5)
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .body("find { it.id == '" + id + "' }.username", equalTo("user"))
                .extract()
                .response();

        ResponseBody responseBody = response.getBody();
        String responseString = responseBody.asString();
        System.out.println("result");
        System.out.println(responseString);
    }
}
