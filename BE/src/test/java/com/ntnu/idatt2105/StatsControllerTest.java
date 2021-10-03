package com.ntnu.idatt2105;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.idatt2105.model.*;
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
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureMockMvc
public class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private SectionRepo sectionRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SecurityService securityService;

    @Autowired
    ObjectMapper objectMapper;

    private Room room;
    private Section section;
    private Section section2;
    private User user;
    private Role role;
    private String token;
    private Set<Section> sections;

    @BeforeEach
    void setUp() {
        sections = new HashSet<>();
        sections.add(section);
        sections.add(section2);
        room = new Room(1, "test", sections);
        roomRepo.save(room);

        section = new Section("PC", 12, room);
        section2 = new Section("Pult", 5, room);
        sectionRepo.save(section);
        sectionRepo.save(section2);

        sections.clear();
        sections.add(section);
        sections.add(section2);
        room.setSections(sections);
        roomRepo.save(room);

        role = new Role(1, "ADMIN");
        roleRepo.save(role);
        user = new User(1, 12345678, "Herman", "Aagaard",
                "herman.aagaard@mail.no", "passord", role);
        userRepo.save(user);

        token = securityService.createToken(user.getId(), "ADMIN");
    }

    @Test
    @Transactional
    void testFindSectionAllTime() throws Exception {
        mockMvc.perform(get("/api/v1/stats/alltime/{roomID}", room.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testTotalMinutesReservedForRoomPastWeeks() throws Exception {
        mockMvc.perform(get("/api/v1/stats/{roomID}/{nrOfWeeks}", room.getId(), 2)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
