package api.reqres;

import api.reqres.pojoUsers.*;
import api.spec.Specifications;
import io.cucumber.java.sl.In;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.util.*;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class RequresPojoTest {
    private final static String URL = "https://reqres.in/";

    /**
     *   !!! В API-тестах обязательно нужно проверять каждую строку!!!
     * 1. Задачи для этого класса смотри в файле DescriptionTasks.jpg
     * 2. Один из сайтов для перевода Json to POJO - https://json2csharp.com/code-converters/json-to-pojo
     */

    @Test
    public void checkAvatarContainsIdTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());

        List<UserData> users = given()
                .when()
                .get("api/users?page=2")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", UserData.class); //если будет необходимо получить не вложенные в "data" данные, а данные находящиеся вместе с "data" на верхнем уровне, то мы вместо "data" напишем "." (что будет означать из корня проекта)

    //1 способ - сравниваем значения напрямую из экземпляров класса
        //проверка аватара на содержание "id"
        users.forEach(x-> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
        //проверка почты на окончание "reqres.in"
        Assert.assertTrue(users.stream().allMatch(x->x.getEmail().endsWith("@reqres.in")));

    //2 способ - сравниваем значения через полученные списки
        //список с аватарками
        List<String> realPeopleAvatars = users.stream()
                .map(UserData::getAvatar)
                .collect(Collectors.toList());
        //список с айди
        List<String> realPeopleId = users.stream()
                .map(x->x.getId().toString())
                .collect(Collectors.toList());
        //проверка через сравнение двух списков
        for (int i = 0; i < realPeopleAvatars.size(); i++) {
            Assert.assertTrue(realPeopleAvatars.get(i).contains(realPeopleId.get(i)));
        }
    }

    @Test
    public void successRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());

        Integer id = 4;
        String tocken = "QpwL5tke4Pnpja7X4";
        RegisterRequest user = new RegisterRequest("eve.holt@reqres.in", "pistol");

        RegisterSuccessResponse successResponse = given()
                .when()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(RegisterSuccessResponse.class);
        //вначале обязательная проверка на "notNull"
        Assert.assertNotNull(successResponse.getId());
        Assert.assertNotNull(successResponse.getToken());
        //потом уже все остальные проверки
        Assert.assertEquals(id, successResponse.getId());
        Assert.assertEquals(tocken, successResponse.getToken());

    }

    @Test
    public void unsuccessRegTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecError400());

        String error = "Missing password";
        RegisterRequest user = new RegisterRequest("sydney@fife", "");

        RegisterUnsuccessResponse unsuccessResponse = given()
                .when()
                .body(user)
                .post("api/register")
                .then().log().all()
                .extract().as(RegisterUnsuccessResponse.class);
        Assert.assertEquals(error, unsuccessResponse.getError());
    }

    @Test
    public void sortedYearsTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());

        List<ResourceData> resources = given()
                .when()
                .get("api/unknown")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", ResourceData.class);

        List<Integer> nosortYears = resources.stream().map(x->x.getYear()).collect(Collectors.toList());
        List<Integer> sortYears = nosortYears.stream().sorted().collect(Collectors.toList());
        Assert.assertEquals(sortYears, nosortYears);
    }


    //При Удалении мы должны просто проверить код ответа от сервера
    @Test
    public void deleteUserTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecUnique(204));
        given()
                .when()
                .delete("api/users/2")
                .then().log().all();
    }

    @Test
    public void TimeTest(){
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpecOK200());
        TimeRequest timeRequest = new TimeRequest("morpheus", "zion resident");
        TimeResponse timeResponse = given()
                .body(timeRequest)
                .when()
                .put("api/users/2")
                .then().log().all()
                .extract().as(TimeResponse.class);

        String currentTimeServer = (timeResponse.getUpdatedAt()).substring(11, 19);
        String currentTimeMyComp = (Clock.systemUTC().instant().toString()).substring(11, 19);
        System.out.println("currentTimeServer-" + currentTimeServer + ", " + "currentTimeMyComp-" + currentTimeMyComp);

        Assert.assertNotEquals(currentTimeServer, currentTimeMyComp);
    }
}
