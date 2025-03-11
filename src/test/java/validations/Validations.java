package validations;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;

public class Validations {
    @Step("Checking status code is {1}")
    public static void checkStatus(ValidatableResponse response, int statueER){
        Assert.assertEquals("Code valid",statueER, response.extract().statusCode());
    }
}
