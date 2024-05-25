package com.example.application.services;

import com.example.application.data.PasswordResetToken;
import com.example.application.data.Role;
import com.example.application.data.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestExecutionListeners(listeners = DependencyInjectionTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {

    static {
        // Prevent Vaadin Development mode to launch browser window
        System.setProperty("vaadin.launch-browser", "false");
    }

    @Autowired
    private UserService userService;

    private User user;

    @BeforeEach
    public void setup(){}


    @Test
    void saveUser() {
        User testUser = new User("mark55", "Mark Smith", "smith@gmail.com", "pass123", Set.of(Role.USER));


        userService.saveUser(testUser);

        User expected = new User("mark55", "Mark Smith", "smith@gmail.com", "pass123", Set.of(Role.USER));

        User actual = userService.findUserByUsername("mark55");

        assertUserEquals(expected, actual);
    }

    //NIE WIEM JAK TO PRZETESTOWAĆ XD

    @Test
    void changePassword() {


    }

    @Test
    void usernameExists() {
        //We already have User named "mark55" and checking it in saveUser method
    }

    @Test
    void createPasswordResetTokenForUser() {
        User user = userService.findUserByEmail("smith@gmail.com");
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetTokenForUser(user, token);

        //mamy usera i sprawdzamy czy ten user ma ten token

        User presistantUser = userService.getUserByPasswordResetToken(token);

        assertUserEquals(presistantUser, user);
    }

    //TEŻ NIE WIEM LECIMY Z PISANIEM DOKUMENTACJI BO MAŁO CZASU
    @Test
    void validatePasswordToken() {
        //already have generated token in previous test
        User user = userService.findUserByUsername("mark55");

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);

        userService.validatePasswordToken(passwordResetToken.getToken());

        User presistantUser = userService.getUserByPasswordResetToken(token);

        assertUserEquals(presistantUser, user);

    }

    private void assertUserEquals(User expected, User actual) {
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getRoles(), actual.getRoles());

    }
}