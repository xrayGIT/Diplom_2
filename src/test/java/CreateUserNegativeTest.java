import client.StellarburgersClient;
import io.restassured.response.ValidatableResponse;
import model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static helper.Environment.BASE_URL;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static validations.Validations.checkStatus;

@RunWith(Parameterized.class)
public class CreateUserNegativeTest {
    String caseName;
    String email;
    String password;
    String name;

    public CreateUserNegativeTest(String caseName, String email, String password, String name) {
        this.caseName = caseName;
        this.email = email;
        this.password = password;
        this.name = name;
    }


    @Parameterized.Parameters(name = "{0}")
    public static Object[][] testData(){
        return new Object[][]{
                {"Создание пользователя без email не возможно", null, "pass", "name"},
                {"Создание пользователя без пароля не возможно", "testmail986081@mail.test", null, "name"},
                {"Создание пользователя без имени не возможно", "testmail986081@mail.test", "pass", null}

        };
    }

    @Test
    public void createUserWithNoParam(){
        StellarburgersClient stellarburgersClient = new StellarburgersClient(BASE_URL);
        User userWithNoEmail = new User(email, password, name);
        ValidatableResponse validatableResponse = stellarburgersClient.createUser(userWithNoEmail);
        checkStatus(validatableResponse, 403);
        validatableResponse.assertThat()
                .body("accessToken", nullValue())
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
