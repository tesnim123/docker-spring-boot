package tn.esprit.spring.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.service.IUserService;

@ExtendWith(MockitoExtension.class)
class UserRestControlImplTest {


    @Mock
    IUserService userService;

    @InjectMocks
    UserRestControlImpl controller;

    private User buildUser(Long id) {
        User u = new User("Khabacha", "Tesnim", new Date());
        u.setId(id);
        return u;
    }

    @Test
    void welcome_shouldReturnWelcomeMessage() {
        assertTrue(controller.welcome().contains("Bienvenue"));
    }

    @Test
    void addUser_shouldReturnCreatedUser() {
        when(userService.addUser(any(User.class))).thenReturn(buildUser(1L));

        User result = controller.addUser(new User("Khabacha", "Tesnim", new Date()));

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService).addUser(any(User.class));
    }

    @Test
    void getUser_shouldReturnUser_whenExists() {
        when(userService.retrieveUser(1L)).thenReturn(buildUser(1L));

        User result = controller.getUser(1L);

        assertEquals("Tesnim", result.getPrenom());
        verify(userService).retrieveUser(1L);
    }

    @Test
    void getUser_shouldThrow404_whenNotFound() {
        when(userService.retrieveUser(99L)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.getUser(99L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        User u = buildUser(1L);
        u.setNom("NouveauNom");
        when(userService.updateUser(u)).thenReturn(u);

        User result = controller.updateUser(u);

        assertEquals("NouveauNom", result.getNom());
        verify(userService).updateUser(u);
    }

    @Test
    void deleteUser_shouldCallService() {
        controller.deleteUser(1L);

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUser_shouldPropagateException_whenServiceFails() {
        doThrow(new IllegalStateException("db error")).when(userService).deleteUser(99L);

        assertThrows(IllegalStateException.class, () -> controller.deleteUser(99L));
    }
}