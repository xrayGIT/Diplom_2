package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import model.User;

import static io.restassured.RestAssured.given;
import static validations.Validations.checkStatus;

public class StellarburgersClient {
    private String BASE_URI;
    private static final String REGISTER_USER_API = "/api/auth/register";
    private static final String USER_API = "/api/auth/user";
    private static final String LOGIN_USER_API = "/api/auth/login";
    private RequestSpecification requestSpec;

    public StellarburgersClient(String BASE_URI) {
        this.BASE_URI = BASE_URI;
        requestSpec = given()
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json");
    }

    // Создание пользователя POST https://stellarburgers.nomoreparties.site/api/auth/register
    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user, int statusER) {
         ValidatableResponse response = given().filter(new AllureRestAssured())
                .spec(requestSpec)
                .body(user)
                .post(REGISTER_USER_API)
                .then();
        checkStatus(response, statusER);
        return response;
    }

    // Удаление пользователя DELETE https://stellarburgers.nomoreparties.site/api/auth/user
    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String token, int statusER) {
        ValidatableResponse response = given().filter(new AllureRestAssured())
                .spec(requestSpec)
                .header("Authorization", token)
                .delete(USER_API)
                .then();
        checkStatus(response, statusER);
        return response;
    }

    // Логин пользователя POST https://stellarburgers.nomoreparties.site/api/auth/login
    @Step("Логин пользователя")
    public ValidatableResponse loginUser(User user, int statusER) {
        ValidatableResponse response =  given().filter(new AllureRestAssured())
                .spec(requestSpec)
                .body(user)
                .post(LOGIN_USER_API)
                .then();
        checkStatus(response, statusER);
        return response;
    }

    // Изменение пользователя PATCH https://stellarburgers.nomoreparties.site/api/auth/user
    @Step("Изменение пользователя")
    public ValidatableResponse modifyUser(User user, String token, int statusER) {
        RequestSpecification spec1 = given()
                .spec(requestSpec);
        if (token != null) {
            spec1.header("Authorization", token);
        }
        ValidatableResponse response =  given().filter(new AllureRestAssured())
                .spec(spec1)
                .body(user)
                .patch(USER_API)
                .then();
        checkStatus(response, statusER);
        return response;
    }

    // Создание заказа

    // Получить заказы
}
