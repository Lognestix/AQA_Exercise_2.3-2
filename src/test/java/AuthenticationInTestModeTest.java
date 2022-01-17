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