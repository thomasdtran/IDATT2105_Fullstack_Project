package com.ntnu.idatt2105;

import com.ntnu.idatt2105.model.Role;
import com.ntnu.idatt2105.model.User;
import com.ntnu.idatt2105.model.UserDetails;
import com.ntnu.idatt2105.repo.RoleRepo;
import com.ntnu.idatt2105.repo.UserRepo;
import com.ntnu.idatt2105.service.SecurityService;
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

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private SecurityService securityService;

    private User user;
    private UserDetails userDetails;
    private Role role;
    private String token;

    @BeforeEach
    public void setUp() {
        role = new Role(1, "ADMIN");
        roleRepo.save(role);

        userDetails = new UserDetails("herman@aagaard.no", "passord");

        user = new User(1, 91557141, "Herman", "Aagaard",
                "herman@aagaard.no", "passord", role);

        userRepo.save(user);

        token = securityService.createToken(user.getId(), "ADMIN");

    }

    @Test
    @Transactional
    void testGetSubject() throws Exception {
        mockMvc.perform(get("/api/v1/sub")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
