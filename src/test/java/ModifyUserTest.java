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

public class ModifyUserTest {
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
    @DisplayName("Изменение имени пользователя")
    public void modifyUserName(){
        user.setName("newName");
        ValidatableResponse response = stellarburgersClient.modifyUser(user, token, 200);
        response.assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Изменение всех полей пользователя")
    public void modifyUserAllData(){
        user.setName("newName");
        user.setPassword("newPass");
        user.setEmail("new_mail123111@mail.test");
        ValidatableResponse response = stellarburgersClient.modifyUser(user, token, 200);
        response.assertThat()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Невозможно изменить пользователя без авторизации")
    public void modifyUser_notAuthorized(){
        user.setName("newName");
        user.setPassword("newPass");
        user.setEmail("new_mail123111@mail.test");
        ValidatableResponse response = stellarburgersClient.modifyUser(user, null, 401);
        response.assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
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
