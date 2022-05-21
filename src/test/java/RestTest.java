import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestTest {
    private final String endPoint = "https://reqres.in";
    private final String newUser= "{\n" + "    \"name\": \"morpheus\",\n" + "    \"job\": \"leader\"\n" + "}";
    private final String updatedUserData = "{\n" + "    \"name\": \"morpheus\",\n" + "    \"job\": \"zion resident\"\n" + "}";
    private final String registrationData = "{\n" + "    \"email\": \"eve.holt@reqres.in\",\n" + "    \"password\": \"pistol\"\n" + "}";
    private final String loginData = "{\n" + "    \"email\": \"eve.holt@reqres.in\",\n" + "    \"password\": \"cityslicka\"\n" + "}";
    private final String emailForRegistration = "{\n" + "    \"email\": \"sydney@fife\"\n" + "}";
    private final String emailForLogin = "{\n" + "    \"email\": \"peter@klaven\"\n" + "}";




    @Test
    @DisplayName("Получение списка пользователей")
    public void getUsers(){
        given()
                .baseUri(endPoint)
                .basePath("api/users")
                .contentType(ContentType.JSON)
                .when().get()
                .then().statusCode(200)
                .body("data[0].email", equalTo("george.bluth@reqres.in"))
                .extract()
                .response()
                .prettyPrint();


    }


    @Test
    @DisplayName("Получение одного заданного пользователя")
    public void getSingleUserTest() {
        given()
                .baseUri(endPoint)
                .basePath("/api/users/2")
                .contentType(ContentType.JSON)
                .when().get()
                .then().statusCode(200)
                .body("data.last_name", equalTo("Weaver"));
    }


    @Test
    @DisplayName("Заданный пользователь не найден")
    public void userNotfound(){
        given()
                .baseUri(endPoint)
                .basePath("/api/users/23")
                .contentType(ContentType.JSON)
                .when().get()
                .then().assertThat()
                .statusCode(404);
    }


    @Test
    @DisplayName("Создание пользователя")
    public void createUser(){
        ValidatableResponse response = given()
                .baseUri(endPoint)
                .basePath("/api/users")
                .contentType(ContentType.JSON)
                .body(newUser)
                .when().post()
                .then().assertThat()
                .statusCode(201)
                .body("name", is("morpheus"));

        System.out.println("Response :" + response.extract().asPrettyString());
    }


    @Test
    @DisplayName("Обновление данных пользователя")
    void updateUser() {
        given()
                .baseUri(endPoint)
                .basePath("/api/users/2")
                .contentType(ContentType.JSON)
                .body(updatedUserData)
                .when().put()
                .then().statusCode(200)
                .body("name", is("morpheus"))
                .body("job", is("zion resident"));
        /*
    PATCH используется для частичного изменения ресурса.
    PUT создает новый ресурс или заменяет представление целевого ресурса, данными представленными в теле запроса.
    Иными словами, в PATCH вложенный объект содержит набор инструкций, описывающих, как ресурс, находящийся в данный момент на исходном сервере,
    должен быть модифицирован для создания новой версии. А в PUT содержится новая версия ресурса целиком.
        */
    }


    @Test
    @DisplayName("Удаление пользователя")
    public void deleteUser(){
        given()
                .baseUri(endPoint)
                .basePath("/api/users/2")
                .contentType(ContentType.JSON)
                .when().delete()
                .then().assertThat()
                .statusCode(204);

    }


    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    public void successfulRegistrationTest() {
        given()
                .baseUri(endPoint)
                .basePath("/api/register")
                .contentType(ContentType.JSON)
                .body(registrationData)
                .when().post()
                .then().statusCode(200)
                .body("id", is(4))
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }


    @Test
    @DisplayName("Неуспешная регистрация пользователя")
    public void unsuccessfulRegistration(){
       ValidatableResponse response = given()
                .baseUri(endPoint)
                .basePath("/api/register")
                .contentType(ContentType.JSON)
                .body(emailForRegistration)
                .when().post()
                .then().statusCode(400)
                .body("error", is("Missing password"));

        System.out.println("Response :" + response.extract().asPrettyString());
    }


    @Test
    @DisplayName("Успешный логин пользователя")
    public void successfulLoginTest() {
        given()
                .baseUri(endPoint)
                .basePath("/api/login")
                .contentType(ContentType.JSON)
                .body(loginData)
                .when().post()
                .then().statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }


    @Test
    @DisplayName("Неуспешная попытка логина")
     public void unsuccessfulLogin() {
        given()
                .baseUri(endPoint)
                .basePath("/api/login")
                .contentType(ContentType.JSON)
                .body(emailForLogin)
                .when().post()
                .then().statusCode(400)
                .body("error", is("Missing password"));
    }

    /**
     * Узнать время необходимое на выполнение запроса и сравнить с ожидаемым
     */
    @Test
    @DisplayName("Отсроченный запрос")
    public void delayedResponse(){
     RequestSpecification request =  RestAssured.given();
        request.contentType(ContentType.JSON);
        request.baseUri(endPoint);
        request.basePath("\n" + "/api/users?delay = 3");
        Response response = request.post();
        long responseTime = response.getTime();
        System.out.println("Response time in ms using getTime():"+responseTime);
        assertEquals(1000, responseTime);
    }

}
