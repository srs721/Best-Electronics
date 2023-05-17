package com.best.electronics.email;

import com.best.electronics.repository.PasswordRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.Mockito.when;
@SpringBootTest
public class ResetPasswordCombinationValidationHandlerTest {
    static ResetPasswordCombinationValidationHandler resetPasswordCombinationValidationHandler;

    @BeforeAll
    public static void init(){
        resetPasswordCombinationValidationHandler = new ResetPasswordCombinationValidationHandler();
    }

    @Test
    public void checkCombinationFailureAdminTest(){
        try (MockedConstruction<PasswordRepository> mocked = Mockito.mockConstruction(PasswordRepository.class,
                (mock, context) -> when(mock.checkCombination(123456, "admin@gmail.com","Admin")).thenReturn( new ArrayList<>()))) {
            boolean status = resetPasswordCombinationValidationHandler.checkCombination(123456,"admin@gmail.com","Admin");
            Assertions.assertEquals(false,status);
        }
    }

    @Test
    public void checkCombinationSuccessAdminTest(){
        ArrayList<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", "123456");
        result.add(map);
        try (MockedConstruction<PasswordRepository> mocked = Mockito.mockConstruction(PasswordRepository.class,
                (mock, context) -> when(mock.checkCombination(123456, "admin@gmail.com","Admin")).thenReturn(result))){
            boolean status = resetPasswordCombinationValidationHandler.checkCombination(123456,"admin@gmail.com","Admin");
            Assertions.assertEquals(true,status);
        }
    }

    @Test
    public void checkCombinationSuccessUserTest(){
        ArrayList<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("token", "123456");
        result.add(map);
        try (MockedConstruction<PasswordRepository> mocked = Mockito.mockConstruction(PasswordRepository.class,
                (mock, context) -> when(mock.checkCombination(123456, "user@gmail.com","User")).thenReturn(result))){
            boolean status = resetPasswordCombinationValidationHandler.checkCombination(123456,"user@gmail.com","User");
            Assertions.assertEquals(true,status);
        }
    }

    @Test
    public void checkCombinationFailureUserTest(){
        try (MockedConstruction<PasswordRepository> mocked = Mockito.mockConstruction(PasswordRepository.class,
                (mock, context) -> when(mock.checkCombination(123456, "user@gmail.com","User")).thenReturn( new ArrayList<>()))) {
            boolean status = resetPasswordCombinationValidationHandler.checkCombination(123456,"user@gmail.com","User");
            Assertions.assertEquals(false,status);
        }
    }

}
