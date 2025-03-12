import client.StellarburgersClient;
import com.github.javafaker.Faker;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.model.Status;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static helper.Environment.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;

public class LoginUserTest {
    StellarburgersClient stellarburgersClient;
    User user;
    String token;

    @Before
    @Step("Пререквезиты")
    public void setUp(){
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        user = new User(email, "password", name);
        stellarburgersClient = new StellarburgersClient(BASE_URL);
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user, 200);
        token = validatableResponse.extract().body().jsonPath().get("accessToken");
        Assert.assertNotNull("Токен сгенерирован", token);
        validatableResponse.assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя")
    public void loginUser(){
        ValidatableResponse response = stellarburgersClient.loginUser(user, 200);
        response.assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя с неверным email не возможен")
    public void loginUserWithInvalidEmail(){
        User userWithNotExistingEmail = new User("not_existing_email999877731@test.test", "pass", null);
        ValidatableResponse response = stellarburgersClient.loginUser(userWithNotExistingEmail, 401);
        response.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo( "email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин пользователя с неверным паролем не возможен")
    public void loginUserWithInvalidPassword(){
        User userWithIncorrectPassword = new User(user.getEmail(), "incorrectPass", null);
        ValidatableResponse response = stellarburgersClient.loginUser(userWithIncorrectPassword, 401);
        response.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo( "email or password are incorrect"));
    }

    @After
    @Step("Восстановление исходного состояния")
    public void tearDown(){
        if(token != null){
            ValidatableResponse response = stellarburgersClient.deleteUser(token, 202);
        } else {
            Allure.step("Удаление пользователя не возможно. Токен отсутствует, проверьте был ли он сгенерирован на предыдущих шагах", Status.BROKEN);
        }

    }
}
