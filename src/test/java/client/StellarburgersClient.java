package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.ValidatableResponse;
import lombok.AllArgsConstructor;
import model.User;

import static io.restassured.RestAssured.given;

@AllArgsConstructor
public class StellarburgersClient {
    private String BASE_URI;
    private static final String USER_API = "/api/auth/register";
    private  static final String DELETE_USER_API = "/api/auth/user";
    private  static final String LOGIN_USER_API = "/api/auth/login";

    // Создание пользователя POST https://stellarburgers.nomoreparties.site/api/auth/register
    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given().filter(new AllureRestAssured())
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .body(user)
                .post(USER_API)
                .then();
    }

    // Удаление пользователя POST DELETE https://stellarburgers.nomoreparties.site/api/auth/user
    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(User user, String token) {
        return given().filter(new AllureRestAssured())
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .body(user)
                .delete(DELETE_USER_API)
                .then();
    }

    // Логин пользователя POST https://stellarburgers.nomoreparties.site/api/auth/login
    @Step("Логин пользователя")
    public ValidatableResponse loginUser(User user) {
        return given().filter(new AllureRestAssured())
                .baseUri(BASE_URI)
                .header("Content-Type", "application/json")
                .body(user)
                .post(LOGIN_USER_API)
                .then();
    }

    // Изменение пользователя

    // Создание заказа

    // Получить заказы
}
