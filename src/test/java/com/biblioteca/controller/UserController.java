package com.biblioteca.controller;

import com.biblioteca.entity.Users;
import com.biblioteca.exception.ResourceNotFoundException;
import com.biblioteca.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("Deve retornar status 200 e um usuário ao buscar por ID existente")
    void getUserById_WithExistingId_ShouldReturnOk() throws Exception {
        Users mockUser = new Users();
        mockUser.setId(1L);
        mockUser.setName("Carlos Drummond");
        given(userService.findUserById(1L)).willReturn(Optional.of(mockUser));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Drummond"));
    }

    @Test
    @DisplayName("Deve retornar status 200 e uma lista vazia quando não há usuários")
    void getAllUsers_ShouldReturnOkAndEmptyList() throws Exception {
        given(userService.findAllUsers()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve criar um usuário e retornar status 201")
    void createUser_WithValidData_ShouldReturnCreated() throws Exception {
        Users newUser = new Users();
        newUser.setId(1L);
        newUser.setName("Carlos Drummond");
        newUser.setEmail("ca2rlos.drummond@example.com");
        newUser.setCpf("222.333.444-52");
        given(userService.createUser(any(Users.class))).willReturn(newUser);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Carlos Drummond"));
    }

    @Test
    @DisplayName("Deve atualizar um usuário existente e retornar status 200")
    void updateUser_WithExistingId_ShouldReturnOk() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setName("Carlos Drummond 2");
        updatedUser.setEmail("ca2rlos.drummond@example.com");
        updatedUser.setCpf("222.333.444-52");
        given(userService.updateUser(anyLong(), any(Users.class))).willReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Carlos Drummond 2"));
    }

    @Test
    @DisplayName("Deve lançar status 404 ao tentar atualizar um usuário que não existe")
    void updateUser_WhenUserNotFound_ShouldReturnNotFound() throws Exception {
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setName("Carlos Drummond 2");
        updatedUser.setEmail("ca2rlos.drummond@example.com");
        updatedUser.setCpf("222.333.444-52");

        given(userService.updateUser(eq(99L), any(Users.class)))
                .willThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um usuário existente e retornar status 204")
    void deleteUser_WithExistingId_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 404 ao tentar deletar um usuário que não existe")
    void deleteUser_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Livro não encontrado")).when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }
}