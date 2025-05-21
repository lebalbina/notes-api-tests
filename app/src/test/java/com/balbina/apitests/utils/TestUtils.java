package com.balbina.apitests.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.function.Consumer;

public class TestUtils {

    public static <T> void safeAssert(SoftAssert softAssert, String failMessage, T value, Consumer<T> assertionLogic) {
        if (value == null) {
            softAssert.fail(failMessage);
        } else {
            assertionLogic.accept(value);
        }
    }

    public static void assertValidJson(String body) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readTree(body);
        } catch (JsonProcessingException e) {
            Assert.fail("Invalid JSON response " + e.getMessage());
        }
    }
}
