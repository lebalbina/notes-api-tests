package com.balbina.apitests.logintests;

import com.balbina.apitests.pojo.LoginRequest;
import com.balbina.apitests.pojo.LoginResponse;
import com.balbina.apitests.pojo.User;
import com.balbina.apitests.utils.TestConfig;
import com.balbina.apitests.utils.TestUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.OffsetDateTime;

import static io.restassured.RestAssured.given;

public class LoginSuccessTest extends BaseTest {

    private static final String TOKEN_REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9._]+$";

    private Response response;

    @BeforeClass
    public void loginCall() {
        LoginRequest successLoginRequest = new LoginRequest();
        successLoginRequest.setPassword(TestConfig.getPassword());
        successLoginRequest.setUsername(TestConfig.getUsername());
        response = given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(successLoginRequest)
                .when()
                .post("/login");
    }

    @Test
    public void validateJsonResponse() {
        TestUtils.assertValidJson(response.getBody().asString());
    }

    @Test(dependsOnMethods = {"validateJsonResponse"})
    public void correctLoginDataShouldReturn200() {
        LoginResponse loginResponse =
                response.then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract()
                        .as(LoginResponse.class);

        SoftAssert softAssert = new SoftAssert();

        assertValidExpiration(softAssert, loginResponse.getExpiresAt());
        assertValidToken(softAssert, loginResponse.getToken());
        assertValidUser(softAssert, loginResponse.getUser());

        softAssert.assertAll();
    }

    private void assertValidExpiration(SoftAssert softAssert, String expiration) {
        TestUtils.safeAssert(
                softAssert,
                "Expiration date should not be null",
                expiration,
                value -> {
                    OffsetDateTime expirationDateTime = OffsetDateTime.parse(value);
                    OffsetDateTime now = OffsetDateTime.now();
                    softAssert.assertTrue(expirationDateTime.isAfter(now));
                }
        );
    }

    private void assertValidToken(SoftAssert softAssert, String token) {
        TestUtils.safeAssert(
                softAssert,
                "Token should not be null",
                token,
                value -> softAssert.assertTrue(value.matches(TOKEN_REGEX))
        );
    }

    private void assertValidUser(SoftAssert softAssert, User user) {
        Assert.assertNotNull(user, "User object should not be null");

        //TODO maybe wrapper for int?
        TestUtils.safeAssert(
                softAssert,
                "User ID should be positive value",
                user.getId(),
                id -> softAssert.assertTrue(id > 0)
        );

        TestUtils.safeAssert(
                softAssert,
                "Username should not be null",
                user.getUsername(),
                username -> softAssert.assertFalse(username.isBlank())
        );

        TestUtils.safeAssert(
                softAssert,
                "Email should not be null",
                user.getEmail(),
                email -> softAssert.assertTrue(email.matches(EMAIL_REGEX))
        );
    }
}
