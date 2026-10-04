package tn.esprit.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tn.esprit.spring.entities.User;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    IUserService userService;

    private User buildUser() throws ParseException {
        Date d = new SimpleDateFormat("dd/MM/yyyy").parse("30/09/2000");
        return new User("Khabacha", "Tesnim", d);
    }

    @Test
    public void testAddUser() throws ParseException {
        User u = userService.addUser(buildUser());
        System.out.println("user ajouté, id = " + u.getId());
        assertNotNull(u.getId());
        assertTrue(u.getNom().length() > 0);
        userService.deleteUser(u.getId());
    }

    @Test
    public void testRetrieveUser() throws ParseException {
        User saved = userService.addUser(buildUser());
        User found = userService.retrieveUser(saved.getId());
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        userService.deleteUser(saved.getId());
    }

    @Test
    public void testUpdateUser() throws ParseException {
        User saved = userService.addUser(buildUser());
        saved.setNom("NouveauNom");
        User updated = userService.updateUser(saved);
        assertEquals("NouveauNom", updated.getNom());
        userService.deleteUser(saved.getId());
    }

    @Test
    public void testDeleteUser() throws ParseException {
        User saved = userService.addUser(buildUser());
        userService.deleteUser(saved.getId());
        assertNull(userService.retrieveUser(saved.getId()));
    }
}