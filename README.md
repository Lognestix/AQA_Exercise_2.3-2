## `Статус сборки` [![Build status](https://ci.appveyor.com/api/projects/status/rdegan0naj2f4tje?svg=true)](https://ci.appveyor.com/project/Lognestix/aqa-exercise-2-3-2)
## В build.gradle добавленна поддержка JUnit-Jupiter, Selenide и headless-режим, JavaFaker, Lombok, Rest-Assured, Gson.
```gradle
plugins {
    id 'java'
}

group 'ru.netology'
version '1.0-SNAPSHOT'

sourceCompatibility = 11

//Кодировка файлов (если используется русский язык в файлах)
compileJava.options.encoding = "UTF-8"
compileTestJava.options.encoding = "UTF-8"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly 'org.projectlombok:lombok:1.18.22'
    annotationProcessor 'org.projectlombok:lombok:1.18.22'

    testImplementation 'org.junit.jupiter:junit-jupiter:5.8.2'
    testImplementation 'com.codeborne:selenide:6.2.0'
    testImplementation 'com.github.javafaker:javafaker:1.0.2'
    testImplementation 'io.rest-assured:rest-assured:4.4.0'
    testImplementation 'com.google.code.gson:gson:2.8.9'

    testCompileOnly 'org.projectlombok:lombok:1.18.22'
    testAnnotationProcessor 'org.projectlombok:lombok:1.18.22'
}

test {
    useJUnitPlatform()
    //В тестах, при вызове `gradlew test -Dselenide.headless=true` будет передаватся этот параметр в JVM (где его подтянет Selenide)
    systemProperty 'selenide.headless', System.getProperty('selenide.headless')
}
```
## Код Java для оптимизации авто-тестов.
```Java
package manager;

import com.github.javafaker.Faker;
import domain.RegistrationUserData;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private static final Faker faker = new Faker(new Locale("en"));

    private DataGenerator() {
    }

    private static void sendRequest(RegistrationUserData user) {
        //Запрос
        given() //"дано"
                .spec(requestSpec) //Указывается, какая спецификация используется
                .body(new RegistrationUserData( //Передача в теле объекта, который будет преобразован в JSON,
                        user.getLogin(),        //собственно логин,
                        user.getPassword(),     //пароль
                        user.getStatus()))      //и статус.
                .when()                         //"когда"
                .post("/api/system/users") //На какой путь, относительно BaseUri отправляется запрос
                .then()                         //"тогда ожидаем"
                .statusCode(200);               //Код 200, все хорошо
    }

    public static String generateLogin() { return faker.name().firstName(); }

    public static String generatePassword() { return faker.internet().password(); }

    public static class Registration {
        private Registration() {
        }

        public static RegistrationUserData generateUser(String status) {
            return new RegistrationUserData(generateLogin(), generatePassword(), status);
        }

        public static RegistrationUserData registerUser(String status) {
            RegistrationUserData registerUser = generateUser(status);
            sendRequest(registerUser);
            return registerUser;
        }
    }
}
```
```Java
package domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegistrationUserData {
    private final String login;
    private final String password;
    private final String status;
}
/*  Если не использовать аннотацию Data и RequiredArgsConstructor lombok, то:
  public class UserData {
    private final String login;
    private final String password;
    private final String status;

    public UserData (String login, String password, String status) {
       this.login = login;
       this.password = password;
       this.status = status;
    }

    public String getLogin() { return login; }

    public String getPassword() { return password; }

    public String getStatus() { return status; }
  }
 */
```
## Авто-тесты находящиеся в этом репозитории.
```Java
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static manager.DataGenerator.Registration.generateUser;
import static manager.DataGenerator.Registration.registerUser;

import static manager.DataGenerator.generateLogin;
import static manager.DataGenerator.generatePassword;

class AuthenticationInTestModeTest {

    @BeforeEach
    public void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with active registered user")
    public void shouldSuccessfulLoginIfRegisteredActiveUser() {
        var registeredUser = registerUser("active");
        $("[data-test-id=login] input").setValue(registeredUser.getLogin());
        $("[data-test-id=password] input").setValue(registeredUser.getPassword());
        $(".button").shouldHave(Condition.text("Продолжить")).click();
        $(".heading").shouldBe(Condition.text("Личный кабинет"));
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        var notRegisteredUser = generateUser("active");
        $("[data-test-id=login] input").setValue(notRegisteredUser.getLogin());
        $("[data-test-id=password] input").setValue(notRegisteredUser.getPassword());
        $(".button").shouldHave(Condition.text("Продолжить")).click();
        $("[data-test-id=error-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка" +
                        " Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var blockedUser = registerUser("blocked");
        $("[data-test-id=login] input").setValue(blockedUser.getLogin());
        $("[data-test-id=password] input").setValue(blockedUser.getPassword());
        $(".button").shouldHave(Condition.text("Продолжить")).click();
        $("[data-test-id=error-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка" +
                        " Ошибка! Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var registeredUser = registerUser("active");
        var wrongLogin = generateLogin();
        $("[data-test-id=login] input").setValue(wrongLogin);
        $("[data-test-id=password] input").setValue(registeredUser.getPassword());
        $(".button").shouldHave(Condition.text("Продолжить")).click();
        $("[data-test-id=error-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка" +
                        " Ошибка! Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var registeredUser = registerUser("active");
        var wrongPassword = generatePassword();
        $("[data-test-id=login] input").setValue(registeredUser.getLogin());
        $("[data-test-id=password] input").setValue(wrongPassword);
        $(".button").shouldHave(Condition.text("Продолжить")).click();
        $("[data-test-id=error-notification]").shouldBe(Condition.visible)
                .shouldHave(Condition.text("Ошибка" +
                        " Ошибка! Неверно указан логин или пароль"));
    }
}
```