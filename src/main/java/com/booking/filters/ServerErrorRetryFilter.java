package com.booking.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * RestAssured filter that retries requests when the server responds with a 5xx status code.
 * <p>
 * Useful for handling transient server errors in CI environments.
 */
public class ServerErrorRetryFilter implements Filter {

    private final int maxRetries;
    private final long delayMillis;

    public ServerErrorRetryFilter() {
        this(3, 1000);
    }

    public ServerErrorRetryFilter(int maxRetries, long delayMillis) {
        this.maxRetries = maxRetries;
        this.delayMillis = delayMillis;
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        Response response = ctx.next(requestSpec, responseSpec);
        int attempt = 0;

        while (isServerError(response.getStatusCode()) && attempt < maxRetries) {
            attempt++;
            System.out.printf("Received %d from %s %s. Retrying attempt %d/%d...%n",
                    response.getStatusCode(),
                    requestSpec.getMethod(),
                    requestSpec.getURI(),
                    attempt,
                    maxRetries);

            sleep(delayMillis);
            response = ctx.next(requestSpec, responseSpec);
        }

        return response;
    }

    private boolean isServerError(int statusCode) {
        return statusCode >= 500 && statusCode < 600;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
