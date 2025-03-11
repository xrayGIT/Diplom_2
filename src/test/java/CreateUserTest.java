import client.StellarburgersClient;
import com.github.javafaker.Faker;
import io.qameta.allure.Allure;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.model.Status;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static validations.Validations.checkStatus;

public class CreateUserTest {
    StellarburgersClient stellarburgersClient;
    User user;
    String token;
    boolean isUserDeletionRequired = true;

    @Before
    public void setUp(){
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        user = new User(email, "password", name);
        stellarburgersClient = new StellarburgersClient("https://stellarburgers.nomoreparties.site");
    }

    @Test
    @DisplayName("Создание пользователя")
    public void createUser(){
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user);
        checkStatus(validatableResponse, 200);
        token = validatableResponse.extract().body().jsonPath().get("accessToken");
        Assert.assertNotNull("Токен сгенерирован", token);
        validatableResponse.assertThat()
                .body("success", equalTo(true));


    }

    @Test
    @DisplayName("Создание пользователя с тем же email не возможна")
    public void createUserWithSameEmail(){
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user);
//        Allure.step("Проверка статуса кода через assertThat", () -> {
//            validatableResponse.assertThat().statusCode(200);
//        });
        checkStatus(validatableResponse, 200);
        token = validatableResponse.extract().body().jsonPath().get("accessToken");
        validatableResponse = stellarburgersClient.createUser(user);
        checkStatus(validatableResponse, 403);
        validatableResponse.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email не возможно")
    public void createUserWithNoEmail(){
        user.setEmail(null);
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user);
        checkStatus(validatableResponse, 403);
        validatableResponse.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля не возможно")
    public void createUserWithNoPassword(){
        user.setPassword(null);
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user);
        checkStatus(validatableResponse, 403);
        validatableResponse.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени не возможно")
    public void createUserWithNoName(){
        user.setName(null);
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user);
        checkStatus(validatableResponse, 403);
        validatableResponse.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown(){
        if(!isUserDeletionRequired){
            Allure.step("Шаг удаления пользователя пропущен", Status.SKIPPED);
        }
        else if(token != null){
            ValidatableResponse response = stellarburgersClient.deleteUser(user, token);
            checkStatus(response, 202);
        } else {
            Allure.step("Удаление пользователя не возможно. Токен отсутствует, проверьте был ли он сгенерирован на предыдущих шагах", Status.BROKEN);
        }

    }
}
