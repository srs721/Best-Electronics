package com.best.electronics.register;

import com.best.electronics.database.IDatabasePersistence;
import com.best.electronics.database.UserMockDatabasePersistence;
import com.best.electronics.model.User;
import com.best.electronics.state.State;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRegisterHandlerTest {

    private static IDatabasePersistence mockDatabasePersistence;

    @BeforeAll
    public static void init() {
        mockDatabasePersistence = new UserMockDatabasePersistence();
    }

    @Test
    public void registerSuccessTest() throws Exception {
        RegisterAuthHandler registerHandler = new EmailRegisterValidation("{call get_user_emailAddress()}", mockDatabasePersistence);
        registerHandler.setNextHandler(new UserNameRegisterValidation()).setNextHandler(new PasswordRegisterValidation());

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmailAddress("test@gmail.com");
        user.setPassword("Newuser@123");
        user.setConfirmPassword("Newuser@123");
        user.setDateOfBirth("12/12/1992");
        user.setGender("Female");
        user.setAddress("Abc Street, Canada");

        State registerState = registerHandler.validate(user, "user");
        Assertions.assertEquals(registerState.getNextPage(), "userRegisterSuccess");
        Assertions.assertEquals(registerState.getStatus(), "Registered Successfully");
    }

    @Test
    public void registerFailTestExistingEmail() throws Exception {
        RegisterAuthHandler registerHandler = new EmailRegisterValidation("{call get_user_emailAddress()}", mockDatabasePersistence);
        registerHandler.setNextHandler(new UserNameRegisterValidation()).setNextHandler(new PasswordRegisterValidation());

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmailAddress("p@gmail.com");
        user.setPassword("Newuser@123");
        user.setConfirmPassword("Newuser@123");
        user.setDateOfBirth("12/12/1992");
        user.setGender("Female");
        user.setAddress("Abc Street, Canada");

        State registerState = registerHandler.validate(user, "user");
        Assertions.assertEquals(registerState.getNextPage(), "userRegistrationForm");
        Assertions.assertEquals(registerState.getStatus(), "Email address already Exists!");
    }

    @Test
    public void registerFailTestWrongFirstName() throws Exception {
        RegisterAuthHandler registerHandler = new EmailRegisterValidation("{call get_user_emailAddress()}", mockDatabasePersistence);
        registerHandler.setNextHandler(new UserNameRegisterValidation()).setNextHandler(new PasswordRegisterValidation());

        User user = new User();
        user.setFirstName("t1");
        user.setLastName("User");
        user.setEmailAddress("test@gmail.com");
        user.setPassword("Newuser@123");
        user.setConfirmPassword("Newuser@123");
        user.setDateOfBirth("12/12/1992");
        user.setGender("Female");
        user.setAddress("Abc Street, Canada");

        State registerState = registerHandler.validate(user, "user");
        Assertions.assertEquals(registerState.getNextPage(), "userRegistrationForm");
        Assertions.assertEquals(registerState.getStatus(), "Either firstName or lastName are not in correct format!");
    }

    @Test
    public void registerFailTestWrongLastName() throws Exception {
        RegisterAuthHandler registerHandler = new EmailRegisterValidation("{call get_user_emailAddress()}", mockDatabasePersistence);
        registerHandler.setNextHandler(new UserNameRegisterValidation()).setNextHandler(new PasswordRegisterValidation());

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("oj0_");
        user.setEmailAddress("test@gmail.com");
        user.setPassword("Newuser@123");
        user.setConfirmPassword("Newuser@123");
        user.setDateOfBirth("12/12/1992");
        user.setGender("Female");
        user.setAddress("Abc Street, Canada");

        State registerState = registerHandler.validate(user, "user");
        Assertions.assertEquals(registerState.getNextPage(), "userRegistrationForm");
        Assertions.assertEquals(registerState.getStatus(), "Either firstName or lastName are not in correct format!");
    }

    @Test
    public void registerFailInvalidPassword() throws Exception {
        RegisterAuthHandler registerHandler = new EmailRegisterValidation("{call get_user_emailAddress()}", mockDatabasePersistence);
        registerHandler.setNextHandler(new UserNameRegisterValidation()).setNextHandler(new PasswordRegisterValidation());

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmailAddress("test@gmail.com");
        user.setPassword("admin@123");
        user.setConfirmPassword("admin@123");
        user.setDateOfBirth("12/12/1992");
        user.setGender("Female");
        user.setAddress("Abc Street, Canada");

        State registerState = registerHandler.validate(user, "user");
        Assertions.assertEquals(registerState.getNextPage(), "userRegistrationForm");
        Assertions.assertEquals(registerState.getStatus(), "Password must contain at least one " +
                "uppercase, lowercase, number and special character. Min 8 length!");
    }

    @Test
    public void registerFailMismatchPassword() throws Exception {
        RegisterAuthHandler registerHandler = new EmailRegisterValidation("{call get_user_emailAddress()}", mockDatabasePersistence);
        registerHandler.setNextHandler(new UserNameRegisterValidation()).setNextHandler(new PasswordRegisterValidation());

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmailAddress("test@gmail.com");
        user.setPassword("Newuser@123");
        user.setConfirmPassword("Newuser@125");
        user.setDateOfBirth("12/12/1992");
        user.setGender("Female");
        user.setAddress("Abc Street, Canada");

        State registerState = registerHandler.validate(user, "user");
        Assertions.assertEquals(registerState.getNextPage(), "userRegistrationForm");
        Assertions.assertEquals(registerState.getStatus(), "The re-entered password and password are not matching!");
    }

}
