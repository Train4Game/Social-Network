package com.train4game.social.service;

import com.train4game.social.AbstractTest;
import com.train4game.social.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.train4game.social.data.UserTestData.*;

class UserServiceTest extends AbstractTest {
    @Autowired
    private UserService service;

    @Test
    void create() {
        User user = createNewUser();
        User created = service.create(user);
        user.setId(created.getId());
        assertMatch(service.getAll(), ADMIN, USER, NEW_USER, user);
    }

    @Test
    void deleteById() {
        service.deleteById(ADMIN_ID);
        assertMatch(service.getAll(), USER, NEW_USER);
    }

    @Test
    void delete() {
        service.delete(ADMIN);
        assertMatch(service.getAll(), USER, NEW_USER);
    }

    @Test
    void get() {
        assertMatch(service.get(ADMIN_ID), ADMIN);
    }

    @Test
    void getByEmail() {
        assertMatch(service.getByEmail(ADMIN_EMAIL), ADMIN);
    }

    @Test
    void getAll() {
        assertMatch(service.getAll(), ADMIN, USER, NEW_USER);
    }

    @Test
    void update() {
        User updated = new User(ADMIN);
        updated.setName("New Name");
        updated.setEmail("newemail@email.com");

        service.update(updated);
        assertMatch(service.getAll(), USER, NEW_USER, updated);
    }

    @Test
    void updateLocale() {
        User expected = new User(ADMIN);
        expected.setLocale("ru");
        service.updateLocale(ADMIN_ID, "ru");
        assertMatch(service.get(ADMIN_ID), expected);
    }

    @Test
    void enable() {
        User expected = new User(NEW_USER);
        expected.setEnabled(true);
        service.enable(NEW_USER);
        assertMatch(service.get(NEW_USER.getId()), expected);
    }
}