import client.StellarburgersClient;
import model.User;
import org.junit.Before;
import org.junit.Test;

public class LoginUserTest {
    StellarburgersClient stellarburgersClient;
    User user;

    @Before
    public void setUp(){
        user = new User("email121112@nn.pp", "password", "testName");
        stellarburgersClient = new StellarburgersClient("https://stellarburgers.nomoreparties.site");
    }

    @Test
    public void loginUser(){
        stellarburgersClient.loginUser(user).assertThat().statusCode(200);
    }
}
