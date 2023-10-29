package com.farneser.cloudfilestorage.controller;

import com.farneser.cloudfilestorage.dto.RegisterDto;
import com.farneser.cloudfilestorage.exception.UserRegistrationException;
import com.farneser.cloudfilestorage.models.User;
import com.farneser.cloudfilestorage.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testRegisterUser() throws Exception {
        var registerDto = new RegisterDto();

        registerDto.setUsername("testuser");
        registerDto.setPassword("testuser");
        registerDto.setConfirmPassword("testuser");

        var user = new User();

        user.setId(1L);

        when(userService.registerNewUser(registerDto)).thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("password", "testuser")
                        .param("confirmPassword", "testuser")
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testRegisterExistingUser() throws Exception {
        var registerDto = new RegisterDto();

        registerDto.setUsername("testuser");
        registerDto.setPassword("testuser");
        registerDto.setConfirmPassword("testuser");

        when(userService.registerNewUser(registerDto)).thenThrow(new UserRegistrationException("failed"));

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("password", "testuser")
                        .param("confirmPassword", "testuser")
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testRegisterPasswordsNotMatch() throws Exception {
        var registerDto = new RegisterDto();

        registerDto.setUsername("testuser");
        registerDto.setPassword("testuser");
        registerDto.setConfirmPassword("testuser1");

        when(userService.registerNewUser(registerDto)).thenThrow(new UserRegistrationException("failed"));

        mockMvc.perform(MockMvcRequestBuilders.post("/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "testuser")
                        .param("password", "testuser")
                        .param("confirmPassword", "testuser1")
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andDo(MockMvcResultHandlers.print());
    }
}

