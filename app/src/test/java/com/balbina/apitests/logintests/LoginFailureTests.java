package com.balbina.apitests.logintests;

import com.balbina.apitests.pojo.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.balbina.apitests.utils.TestUtils.assertValidJson;
import static com.balbina.apitests.utils.TestUtils.safeAssert;
import static io.restassured.RestAssured.given;

public class LoginFailureTests extends BaseTest {

    @DataProvider(name = "invalidLoginScenarios")
    public Object[][] invalidLoginScenarios()  {
        ObjectMapper mapper = new ObjectMapper();
        String path = System.getProperty("loginFailureDataPath",
                                         "src/test/resources/login-failure-scenarios.json"
        );
        try {
            LoginFailureScenario[] array = mapper.readValue(new File(path), LoginFailureScenario[].class);
            List<LoginFailureScenario> list = Arrays.asList(array);
            return list.stream()
                    .map(item -> new Object[]{item})
                    .toArray(size -> new Object[size][1]);
        } catch (IOException e) {
            System.err.println("JSON parsing failed: " + e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException("Failed to load login scenarios from file: " + path, e);
        }
    }

    @Test(dataProvider = "invalidLoginScenarios")
    public void testInvalidLoginData(LoginFailureScenario scenario) {
        Response response = given()
                .baseUri(baseUrl)
                .body(generateUserPassMap(scenario.username, scenario.password))
                .contentType(ContentType.JSON)
                .when()
                .post("/login");

        String responseBody = response.getBody().asString();
        ErrorResponse apiError = response.as(ErrorResponse.class);
        SoftAssert softAssert = new SoftAssert();

        assertValidJson(responseBody);
        Assert.assertNotNull(apiError.getError(), "ApiError should not be null");

        softAssert.assertEquals(response.statusCode(), scenario.expectedStatus);
        assertValidErrorType(scenario.expectedErrorType, softAssert, apiError);
        assertValidErrorMessage(scenario.expectedErrorMessage, softAssert, apiError);

        softAssert.assertAll();
    }

    private static void assertValidErrorMessage(String errorMessage, SoftAssert softAssert, ErrorResponse apiError) {
        safeAssert(
                softAssert,
                "error message should not be null",
                apiError.getError().getMessage(),
                message -> softAssert.assertEquals(message, errorMessage)
        );
    }

    private static void assertValidErrorType(String errorType, SoftAssert softAssert, ErrorResponse apiError) {
        safeAssert(
                softAssert,
                "error type should not be null",
                apiError.getError().getType(),
                type -> softAssert.assertEquals(type, errorType)
        );
    }

    private Map<Object, Object> generateUserPassMap(String username, String password) {
        Map<Object, Object> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        return body;
    }
}
