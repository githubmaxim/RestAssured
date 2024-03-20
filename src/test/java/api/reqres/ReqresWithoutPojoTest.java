package api.reqres;

import api.spec.Specifications;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

// https://www.youtube.com/watch?v=z9Tvxh6uQzI - пример

public class ReqresWithoutPojoTest {
    private final static String URL = "https://reqres.in/";

    /**
     * 1. Получить список пользователей со второй страницы на сайте "https://reqres.in/";
     * 2. Убедиться, что "id" пользователей содержатся в их "avatar";
     * 3. Убедиться, что "email" пользователей имеет окончание "reqres.in".
     */

    //GET запрос
    @Test
    public void checkAvatarsNoPojoTest() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        //Дальше будет два типа проверок:
        Response response = given()
                .when()
                .get("api/users?page=2")
                .then().log().all()
        //1 тип проверок - сразу при создании объекта "response"
                //следующее !единственное в данных поле мы можем проверять на "equalTo()"
                .body("page", equalTo(2))
                //следующие !вложенные поля нельзя сравнивать по "equalTo()", т.к. в них будут содержаться не поле,
                // а массив со всеми "id" или 'email" или "first_name" и т.д. Такие поля проверяются на пустоту
                .body("data.id", notNullValue())
                .body("data.email", notNullValue())
                .body("data.first_name", notNullValue())
                .body("data.last_name", notNullValue())
                .body("data.avatar", notNullValue())
                .extract().response();

        //2 тип проверок - берем данные из "response". Позволяет проверять на значения вложенные поля.
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        List<Integer> ids = jsonPath.get("data.id");
        List<String> avatars = jsonPath.get("data.avatar");

        for(int i = 0; i < avatars.size(); i++){
            Assert.assertTrue(avatars.get(i).contains(ids.get(i).toString()));
        }

        Assert.assertTrue(emails.stream().allMatch(x->x.endsWith("@reqres.in")));
    }


    //POST запрос
    @Test
    public void  successUserRegTestPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        Map<String, String> user = new HashMap<>();
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");

//Дальше будет два типа проверок:
        //1 вариант - без создания "response"
        given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("id", equalTo(4))
                .body("token", equalTo("QpwL5tke4Pnpja7X4"));

        //2 вариант - c созданием "response"
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        int id = jsonPath.get("id");
        String token = jsonPath.get("token");
        Assert.assertEquals(4, id);
        Assert.assertEquals("QpwL5tke4Pnpja7X4", token);
    }


    //POST запрос
    @Test
    public void unsuccessUserRegNoPojo(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());
        Map<String, String> user = new HashMap<>();
        user.put("email", "sydney@life");
        //1 вариант - без создания "response"
        given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("error", equalTo("Missing password"));

        //2 вариант - c созданием "response"
        Response response = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        String error = jsonPath.get("error");
        Assert.assertEquals("Missing password", error);
    }

}
