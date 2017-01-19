package ru.fil7.examples.json.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class Validation {

    public static final String DEFAULT_URL = "http://dispatcher.netpoint-dc.com/sample.json";

    public static void main(String[] args) {
        JsonNode jsonNode = getJson(args);
        // use default url
        if (args.length < 2) {
            System.out.println(String.format(
                    "Is response code 200? %s", checkResponseCode(DEFAULT_URL, 200)));
        } else if (args.length >= 2 && "-url".equalsIgnoreCase(args[0]) && Objects.nonNull(jsonNode)) {
            System.out.println(String.format(
                    "Is response code 200? %s", checkResponseCode(args[1], 200)));
        }
        if (Objects.nonNull(jsonNode)) {
            isResponseJsonValid(jsonNode);
        }
    }

    public static boolean checkResponseCode(String targetUrl, int code) {
        System.out.println(String.format("Check response code from %s", targetUrl));
        HttpResponse<com.mashape.unirest.http.JsonNode> response = null;
        try {
            response = Unirest.get(targetUrl).asJson();
            int responseCode = response.getStatus();
            return responseCode == code;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isResponseJsonValid(JsonNode json) {
        try {
            System.out.println("Validation of json...");

            JsonNode schema = JsonLoader.fromURL(Validation.class.getResource("/schema.json"));
            JsonSchema jsonSchema = JsonSchemaFactory.byDefault().getJsonSchema(schema);
            ProcessingReport report = jsonSchema.validate(json);

            String result = report.isSuccess() ? "Result: Valid json" : "Result: Invalid json";
            System.out.println(result);
            return report.isSuccess();
        } catch (IOException | ProcessingException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static JsonNode getJsonByUrl(String url) {
        final String patternUrl = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        try {
            if (!url.matches(patternUrl)) {
                throw new MalformedURLException();
            }
            return JsonLoader.fromURL(new URL(url));
        } catch (IOException e) {
            System.out.println(String.format("Invalid URL %s.", url));
            return null;
        }
    }

    private static JsonNode getJson(String[] args) {
        if (args.length < 2) {
            System.out.println(String.format("Default url: %s", DEFAULT_URL));
            return getJsonByUrl(DEFAULT_URL);
        }
        if (args.length >= 2) {
            String key = args[0];
            String value = args[1];

            switch (key) {
                case "-url":
                    return getJsonByUrl(value);
                case "-file":
                    File file = new File(value);
                    if (file.exists()) {
                        System.out.println(String.format("File %s was found", value));
                        try {
                            return JsonLoader.fromURL(file.toURI().toURL());
                        } catch (IOException e) {
                            System.out.println("The file must be in JSON format");
                            break;
                        }
                    } else {
                        System.out.println(String.format("Failed to find the file %s", value));
                        break;
                    }

                default:
                    System.out.println(String.format(
                            "Invalid input key %s. Try -file or -url", key));

            }
        }
        return null;
    }


}
