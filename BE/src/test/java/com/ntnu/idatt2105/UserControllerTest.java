package com.ntnu.idatt2105;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.idatt2105.model.Role;
import com.ntnu.idatt2105.model.User;
import com.ntnu.idatt2105.model.UserDetails;
import com.ntnu.idatt2105.repo.RoleRepo;
import com.ntnu.idatt2105.repo.UserRepo;
import com.ntnu.idatt2105.service.SecurityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Role role;
    private String token;

    @BeforeEach
    public void setUp() {
        role = new Role(1, "ADMIN");
        roleRepo.save(role);

        user = new User(1, 91557141, "Herman", "Aagaard",
                "herman@aagaard.no", "passord", role);

        userRepo.save(user);

        token = securityService.createToken(user.getId(), "ADMIN");

    }

    @AfterEach
    public void tearDown(){
        //userRepo.deleteAll();
    }

    @Test
    @Transactional
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstname", is("Herman")));
    }
/*
    @Test
    @Transactional
    void testCreateUser() throws Exception {
        User user2 = new User(2, 91557141, "Lise", "Lotte",
                "lise@lotte.no", "passord", role);

        mockMvc.perform(post("/api/v1/users")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(2)));

    }

 */

    @Test
    @Transactional
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", user.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
