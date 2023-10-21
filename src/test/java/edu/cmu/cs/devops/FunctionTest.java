package edu.cmu.cs.devops;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


/**
 * Unit test for Function class.
 */
public class FunctionTest {

    private final Base64 base64 = new Base64();

    private static final String PROPER_REQUEST_INPUT = "Hello from DevOps! Hi from Azure! 1sn't th1$ fun? 4";
    private static final String PROPER_REQUEST_OUTPUT = "1sn't 4 Azure! DevOps! from from fun? Hello Hi th1$";

    /**
     * Unit test for HttpTriggerJava method.
     */
    @Test
    public void testHttpTriggerJavaForProperInput() {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final String input = "Hello from DevOps! Hi from Azure! 1sn't th1$ fun? 4";
        final String encodedInput = BASE64.encodeAsString(input.getBytes());
        final Optional<String> queryBody = Optional.of(encodedInput);
        doReturn(queryBody).when(req).getBody();

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.OK);
        // Calculate the expected output based on the input in descending order
        String expectedOutput = "is fun DevOps Cloud awesome! and";
        String encodedExpectedOutput = BASE64.encodeAsString(expectedOutput.getBytes());
        assertEquals(ret.getBody().orElse(null), encodedExpectedOutput);
    }

    @Test
    public void testHttpTriggerJavaForImproperInput() {
        // Setup
        @SuppressWarnings("unchecked")
        final HttpRequestMessage<Optional<String>> req = mock(HttpRequestMessage.class);

        final Optional<String> queryBody = Optional.empty();
        doReturn(queryBody).when(req).getBody();

        final ExecutionContext context = mock(ExecutionContext.class);
        doReturn(Logger.getGlobal()).when(context).getLogger();

        // Invoke
        final HttpResponseMessage ret = new Function().run(req, context);

        // Verify
        assertEquals(ret.getStatus(), HttpStatus.BAD_REQUEST);
    }
}
