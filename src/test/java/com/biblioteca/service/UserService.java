package com.biblioteca.service;

import com.biblioteca.entity.Users;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);
        user.setName("Carlos Santana");
        user.setEmail("carlos@email.com");
        user.setCpf("111.222.333-44");
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void createUser_Success() {
        // Arrange
        when(userRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Users savedUser = userService.createUser(new Users());

        // Assert
        assertNotNull(savedUser);
        assertEquals("Carlos Santana", savedUser.getName());
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    @DisplayName("Deve buscar e retornar todos os usuários")
    void findAllUsers_Success() {

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<Users> result = userService.findAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void updateUser_Success() {
        // Arrange
        Users userDetails = new Users();
        userDetails.setName("Carlos Augusto Santana");
        userDetails.setEmail("carlos.augusto@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(Users.class))).thenReturn(user);

        // Act
        Users updatedUser = userService.updateUser(1L, userDetails);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("Carlos Augusto Santana", updatedUser.getName());
        assertEquals("carlos.augusto@email.com", updatedUser.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar um usuário que não existe")
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(99L, new Users());
        });

        verify(userRepository, never()).save(any(Users.class));
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        // O método deleteById retorna void, então não precisamos de 'thenReturn'
        // mas podemos garantir que ele não lança exceção com 'doNothing'
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        // Verifica que o método deleteById foi chamado exatamente 1 vez com o ID correto
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar um usuário que não existe")
    void deleteUser_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(99L);
        });

        verify(userRepository, never()).deleteById(anyLong());
    }
}
