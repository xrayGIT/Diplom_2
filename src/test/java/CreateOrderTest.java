import client.StellarburgersClient;
import com.github.javafaker.Faker;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.model.Status;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static helper.Environment.BASE_URL;
import static org.hamcrest.CoreMatchers.*;

public class CreateOrderTest {
    StellarburgersClient stellarburgersClient;
    String token;
    User user;
    List<String> allIngredients;

    @Before
    @Step("Пререквезиты")
    public void setUp(){
        // новый пользователь для теста с авторизацией
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String name = faker.name().firstName();
        user = new User(email, "password", name);
        stellarburgersClient = new StellarburgersClient(BASE_URL);
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(user, 200);
        token = validatableResponse.extract().body().jsonPath().get("accessToken");
        Assert.assertNotNull("Токен сгенерирован", token);

        // подготовка списка доступных ингредиентов
        ValidatableResponse ingredientsResponse = stellarburgersClient.getIngredients(200);
        allIngredients = ingredientsResponse.extract()
                .jsonPath()
                .getList("data._id");

    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrder_noAuth(){
        Order order = new Order(new String[]{allIngredients.get(0), allIngredients.get(1)});
        ValidatableResponse response = stellarburgersClient.createOrder(order, 200, null);
        response.assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrder_withAuth(){
        Order order = new Order(new String[]{allIngredients.get(0), allIngredients.get(2)});
        ValidatableResponse response = stellarburgersClient.createOrder(order, 200, token);
        response.assertThat()
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов не возможно")
    public void createOrder_noIngredients(){
        Order order = new Order();
        ValidatableResponse response = stellarburgersClient.createOrder(order, 400, null);
        response.assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с не валидными ингредиентами не возможно")
    public void createOrder_invalidIngredients(){
        Order order = new Order(new String[]{"1234", "xwf2"});
        stellarburgersClient.createOrder(order, 500, null);
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
