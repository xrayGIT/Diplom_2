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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class GetOrdersTest {
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

        // добавление заказов под пользователем
        Order order = new Order(new String[]{allIngredients.get(0), allIngredients.get(2)});
        stellarburgersClient.createOrder(order, 200, token);
        order.setIngredients(new String[]{allIngredients.get(0), allIngredients.get(1), allIngredients.get(3)});
        stellarburgersClient.createOrder(order, 200, token);
        order.setIngredients(new String[]{allIngredients.get(0), allIngredients.get(2), allIngredients.get(3)});
        stellarburgersClient.createOrder(order, 200, token);

    }

    @Test
    @DisplayName("Получение заказов пользователя")
    public void getUserOrders(){
        ValidatableResponse response = stellarburgersClient.getUserOrders(token,200);
        response.assertThat()
                .body("success", equalTo(true))
                .body("orders.size()", equalTo(3))
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации не возможна")
    public void getUserOrders_noAuth(){
        ValidatableResponse response = stellarburgersClient.getUserOrders(null,401);
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
