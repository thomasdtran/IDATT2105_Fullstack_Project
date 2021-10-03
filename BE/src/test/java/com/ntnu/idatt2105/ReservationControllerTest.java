package com.ntnu.idatt2105;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.idatt2105.model.Reservation;
import com.ntnu.idatt2105.model.Role;
import com.ntnu.idatt2105.model.User;
import com.ntnu.idatt2105.repo.*;
import com.ntnu.idatt2105.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;

import javax.transaction.Transactional;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ReservationRepo reservationRepo;

    @Autowired
    private SecurityService securityService;

    @Autowired
    ObjectMapper objectMapper;

    private Reservation reservation;
    private Role role;
    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        LocalDate date = LocalDate.of(2021, 6, 1);
        Reservation  reservation = new Reservation(1, "10:00", "15:00", date, "Romnavn");
        reservationRepo.save(reservation);

        role = new Role(1, "ADMIN");
        roleRepo.save(role);
        user = new User(0, 12345678, "Herman", "Aagaard",
                "herman.aagaard@mail.no", "passord", role);
        userRepo.save(user);

        token = securityService.createToken(user.getId(), "ADMIN");
    }

    @Test
    @Transactional
    void testCreateReservation() throws Exception {
        LocalDate date = LocalDate.now().plusWeeks(2);
        Reservation  newReservation = new Reservation(2, "10:00", "15:00", date, "Romnavn");


        mockMvc.perform(post("/api/v1/reservations/{userID}", user.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReservation)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.startTime", is("10:00")));
    }

    @Test
    @Transactional
    void testDeleteReservation() throws Exception {
        LocalDate date = LocalDate.of(2021, 6, 2);
        Reservation newReservation = new Reservation(3, "10:00", "15:00", date, "Romnavn");

        mockMvc.perform(post("/api/v1/reservations/{userID}", user.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReservation)));

        mockMvc.perform(delete("/api/v1/reservations/{id}", newReservation.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testFindAllReservations() throws Exception {
        mockMvc.perform(get("/api/v1/reservations")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].roomName", is("Romnavn")));
    }

    @Test
    @Transactional
    void testFindReservationById() throws Exception {
        LocalDate date = LocalDate.of(2021, 6, 2);
        Reservation  newReservation = new Reservation(1, "10:00", "15:00", date, "Romnavn");

        mockMvc.perform(post("/api/v1/reservations/{userID}", user.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newReservation)));

        mockMvc.perform(get("/api/v1/reservations/{id}", newReservation.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roomName", is("Romnavn")));
    }
}
