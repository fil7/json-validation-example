package ru.fil7.examples.json.validation;


import com.github.fge.jackson.JsonLoader;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.junit.Assert;
import org.junit.Test;

import static ru.fil7.examples.json.validation.Validation.getJsonByUrl;
import static ru.fil7.examples.json.validation.Validation.isResponseJsonValid;

public class ResponseValidationTest {

    public static final String TARGET_URL = "http://dispatcher.netpoint-dc.com/sample.json";

    @Test
    public void testCodeStatus200() throws Exception {
        HttpResponse<JsonNode> response = Unirest.get(TARGET_URL).asJson();
        int responseCode = response.getStatus();
        Assert.assertTrue(responseCode == 200);
    }

    @Test
    public void testThatJsonIsValidForResponse() throws Exception {
        com.fasterxml.jackson.databind.JsonNode json = getJsonByUrl(TARGET_URL);
        Assert.assertTrue(isResponseJsonValid(json));
    }

    @Test
    public void testThatJsonIsValidForEmptyResponse() throws Exception {
        com.fasterxml.jackson.databind.JsonNode json =
                JsonLoader.fromURL(ResponseValidationTest.class.getResource("/json/empty-json.json"));
        Assert.assertTrue(isResponseJsonValid(json));
    }

    @Test
    public void testThatJsonIsValidForArray() throws Exception {
        com.fasterxml.jackson.databind.JsonNode json =
                JsonLoader.fromURL(ResponseValidationTest.class.getResource("/json/json-array.json"));
        Assert.assertTrue(isResponseJsonValid(json));
    }

    @Test
    public void testForInvalidItemId() throws Exception {
        com.fasterxml.jackson.databind.JsonNode json =
                JsonLoader.fromURL(ResponseValidationTest.class.getResource("/json/invalid-id.json"));
        Assert.assertFalse(isResponseJsonValid(json));
    }


}
