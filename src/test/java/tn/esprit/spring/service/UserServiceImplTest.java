package tn.esprit.spring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private User buildUser(Long id) {
        User u = new User("Khabacha", "Tesnim", new Date());
        u.setId(id);
        return u;
    }

    @Test
    void addUser_shouldReturnSavedUser() {
        User toSave = new User("Khabacha", "Tesnim", new Date());
        when(userRepository.save(any(User.class))).thenReturn(buildUser(1L));

        User result = userService.addUser(toSave);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Khabacha", result.getNom());
        verify(userRepository).save(toSave);
    }

    @Test
    void retrieveUser_shouldReturnUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(buildUser(1L)));

        User result = userService.retrieveUser(1L);

        assertNotNull(result);
        assertEquals("Tesnim", result.getPrenom());
        verify(userRepository).findById(1L);
    }

    @Test
    void retrieveUser_shouldReturnNull_whenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(userService.retrieveUser(99L));
        verify(userRepository).findById(99L);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        User u = buildUser(1L);
        u.setNom("NouveauNom");
        when(userRepository.save(u)).thenReturn(u);

        User result = userService.updateUser(u);

        assertEquals("NouveauNom", result.getNom());
        verify(userRepository).save(u);
    }

    @Test
    void deleteUser_shouldCallRepository() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_shouldRethrowException_whenRepositoryFails() {
        doThrow(new EmptyResultDataAccessException(1)).when(userRepository).deleteById(99L);

        assertThrows(EmptyResultDataAccessException.class, () -> userService.deleteUser(99L));
    }
}