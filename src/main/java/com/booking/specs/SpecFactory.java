package com.booking.specs;

import com.booking.clients.AuthClient;
import com.booking.utils.ConfigReader;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class SpecFactory {
    static String token = AuthClient.getSessionToken();
    public static RequestSpecification getGeneralRequestSpecification() {
        return new RequestSpecBuilder().setBaseUri(ConfigReader.getProperty("base.url")).
                                            setBasePath("api").addCookie("token",token).setContentType(ContentType.JSON).
                                                    log(LogDetail.ALL).build();
    }
}
