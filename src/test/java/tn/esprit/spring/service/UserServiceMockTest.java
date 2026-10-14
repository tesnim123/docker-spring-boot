package tn.esprit.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;

import tn.esprit.spring.entities.User;
import tn.esprit.spring.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    User user = new User("Khabacha", "Tesnim", new Date());

    @Test
    public void testAddUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.addUser(user);

        assertNotNull(result);
        assertEquals("Khabacha", result.getNom());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testRetrieveUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.retrieveUser(1L);

        assertNotNull(result);
        assertEquals("Tesnim", result.getPrenom());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testRetrieveUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.retrieveUser(99L);

        assertNull(result);
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    public void testUpdateUser() {
        user.setNom("NouveauNom");
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(user);

        assertEquals("NouveauNom", result.getNom());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserError() {
        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(99L);

        assertThrows(EmptyResultDataAccessException.class, () -> userService.deleteUser(99L));
        verify(userRepository, times(1)).deleteById(99L);
    }
}