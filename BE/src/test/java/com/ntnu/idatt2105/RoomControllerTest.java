package com.ntnu.idatt2105;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntnu.idatt2105.model.Role;
import com.ntnu.idatt2105.model.Room;
import com.ntnu.idatt2105.model.Section;
import com.ntnu.idatt2105.model.User;
import com.ntnu.idatt2105.repo.RoleRepo;
import com.ntnu.idatt2105.repo.RoomRepo;
import com.ntnu.idatt2105.repo.SectionRepo;
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
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RoomControllerTest {

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

    @AfterEach
    void tearDown() {
        /*sectionRepo.deleteAll();
        roomRepo.deleteAll();
        userRepo.deleteAll();
        roleRepo.deleteAll();*/
    }

    @Test
    @Transactional
    void testGetAllRooms() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name", is("test")));
    }

    @Test
    @Transactional
    void testGetSpecificRoom() throws Exception {
        mockMvc.perform(get("/api/v1/rooms/{id}", room.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("test")));
    }

    @Test
    @Transactional
    void testCreatingRoom() throws Exception {
        Room room2 = new Room( "test2", sections);

        mockMvc.perform(post("/api/v1/rooms")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(room2)))
                .andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(room2.getName())));
    }

    @Test
    @Transactional
    void testUpdateRoom() throws Exception {
        room.setName("nytt navn");

        mockMvc.perform(put("/api/v1/rooms/{id}", room.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(room)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("nytt navn")));
    }

    @Test
    @Transactional
    void testDeleteRoom() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{id}", room.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testAddNewSectionToRoom() throws Exception {
        Section newSection = new Section("Pulter", 10, room);

        mockMvc.perform(post("/api/v1/rooms/{id}/sections", room.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newSection)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testUpdateSectionInRoom() throws Exception {
        section.setName("nytt navn");

        mockMvc.perform(put("/api/v1/rooms/{roomId}/{sectionId}", room.getId(), section.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(section)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    void testRemoveSectionFromRoom() throws Exception {
        mockMvc.perform(delete("/api/v1/rooms/{roomId}/{sectionId}", room.getId(), section.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
