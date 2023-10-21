package edu.cmu.cs.devops;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.apache.commons.codec.binary.Base64;

import java.util.Arrays;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample".
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                    name = "req",
                    methods = {HttpMethod.GET, HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter or body
        final String query = request.getQueryParameters().get("name");
        final String input = request.getBody().orElse(query);

        if (input == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a sentence in the query string or in the request body").build();
        } else {
            String decoded = decodeBase64(input);
            String sorted = sortWords(decoded);
            String encoded = encodeBase64(sorted);

            return request.createResponseBuilder(HttpStatus.OK).body(encoded).build();
        }
    }

    private String decodeBase64(String base64Input) {
        byte[] decodedBytes = Base64.decodeBase64(base64Input);
        return new String(decodedBytes);
    }

    private String sortWords(String input) {
        // Split the input into words
        String[] words = input.split("\\s+");

        // Sort the words
        Arrays.sort(words, (s1, s2) -> {
            int result = s1.compareToIgnoreCase(s2);
            if (result == 0) {
                result = s1.compareTo(s2);
            }
            return result;
        });

        // Join the sorted words with whitespace
        return String.join(" ", words);
    }

    private String encodeBase64(String input) {
        byte[] encodedBytes = Base64.encodeBase64(input.getBytes());
        return new String(encodedBytes);
    }
}
