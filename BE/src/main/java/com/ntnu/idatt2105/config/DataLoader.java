package com.ntnu.idatt2105.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ntnu.idatt2105.model.*;
import com.ntnu.idatt2105.repo.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to load up necessary data on start
 */
@Component
public class DataLoader implements ApplicationRunner{
    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    RoomRepo roomRepo;

    @Autowired
    SectionRepo sectionRepo;
    
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    ReservationHistoryRepo reservationHistoryRepo;

    @Autowired
    ReservationRepo reservationRepo;

    Logger logger = LoggerFactory.getLogger(DataLoader.class);
    
    public void run(ApplicationArguments args) {
       setUpRoles();
       setUpAdmin();
       setUpUser();
       setupRooms();
       setupSections();
       setUpReservationHistory();
       
    }
    public void setUpRoles(){
        Role admin = new Role(1, "ADMIN");
        Role user = new Role(2, "USER");
        roleRepo.save(admin);
        roleRepo.save(user);
    }
    public void setUpAdmin(){
        try{
            String email = "admin@gmail.com";
            if(!userRepo.existsByEmail(email)){
                long phoneNumber = 12345678;
                String firstname = "admin";
                String lastname = "admin";
                String password = encoder.encode("admin");
                Role role = new Role(1, "ADMIN");

                User admin = new User(phoneNumber, firstname, lastname, email, password, role);
                admin.setValid(true);
                userRepo.save(admin);
                logger.info("Sucessfully created admin");
            }
        }catch(Exception e){
            logger.warn("Admin already made");
            return;
        }
    }
    public void setUpUser(){
        try {
            String email = "user@gmail.com";
            if(!userRepo.existsByEmail(email)){
                long phoneNumber = 12345678;
                String firstname = "user";
                String lastname = "user";
                String password = encoder.encode("user");
                Role role = new Role(2, "USER");

                User user = new User(phoneNumber, firstname, lastname, email, password, role);
                user.setValid(true);
                userRepo.save(user);
                logger.info("Sucessfully created User");
            }
        } catch (Exception e) {
            logger.warn("User already made");
            return;
        }
    }
    public void setupRooms(){
        if(roomRepo.count() == 0){
            // Room 1
            Room room1 = new Room("SKM201");

            // Room 2
            Set<Section> sections2 = new HashSet<Section>();
            Room room2 = new Room("R201");
            Section section4 = new Section("Arbeidsplass 1", 30, room2);
            Section section5 = new Section("Leseplass 1", 34, room2);
            Section section6 = new Section("PC med stor skjerm", 5, room2);
            Section section7 = new Section("Musikk annlegg", 8, room2);
            Section section8 = new Section("Arbeidsplass 2", 25, room2);

            sections2.add(section4);
            sections2.add(section5);
            sections2.add(section6);
            sections2.add(section7);
            sections2.add(section8);
            room2.setSections(sections2);

            // Room 3
            Set<Section> sections3 = new HashSet<Section>();
            Room room3 = new Room("SF111");
            Section section9 = new Section("PS5 med sofa", 4, room3);
            Section section10 = new Section("Leseplass 10", 60, room3);
            sections3.add(section9);
            sections3.add(section10);
            room3.setSections(sections3);

            roomRepo.save(room1);
            roomRepo.save(room2);
            roomRepo.save(room3);

            logger.info("Created rooms");
        }
    }

    public void setupSections(){
        if(sectionRepo.count() == 0){
            List<Room> rooms = roomRepo.findAll();
            // Room 1
            Room room1 = rooms.get(0);
            Section section1 = new Section("HTC Vive med PC", 10, room1);
            Section section2 = new Section("Kjøkken med TV", 19, room1);
            Section section3 = new Section("Nintendo Switch med TV", 5, room1);

            sectionRepo.save(section1);
            sectionRepo.save(section2);
            sectionRepo.save(section3);
            // Room 2
            Room room2 = rooms.get(1);
            Section section4 = new Section("Arbeidsplass 1", 30, room2);
            Section section5 = new Section("Leseplass 1", 34, room2);
            Section section6 = new Section("PC med stor skjerm", 5, room2);
            Section section7 = new Section("Musikk annlegg", 8, room2);
            Section section8 = new Section("Arbeidsplass 2", 25, room2);

            sectionRepo.save(section4);
            sectionRepo.save(section5);
            sectionRepo.save(section6);
            sectionRepo.save(section7);
            sectionRepo.save(section8);

            // Room 3
            Room room3 = rooms.get(2);
            Section section9 = new Section("PS5 med sofa", 4, room3);
            Section section10 = new Section("Leseplass 10", 60, room3);

            sectionRepo.save(section9);
            sectionRepo.save(section10);
        }
    }

    public void setUpReservationHistory(){
        List<Section> sections = sectionRepo.findAll();
        Section section4 = sections.get(0);
        Section section5 = sections.get(1);
        Section section6 = sections.get(2);

        Section section7 = sections.get(6);
        Section section8 = sections.get(9);
        Section section9 = sections.get(8);
        Section section10 = sections.get(3);

        if (reservationHistoryRepo.count() == 0){
            ReservationHistory reservationHistory = new ReservationHistory("12:00", "13:00", LocalDate.now().minusDays(12), section4);
            ReservationHistory reservationHistory1 = new ReservationHistory("08:00", "10:00", LocalDate.now().minusWeeks(1), section4);
            ReservationHistory reservationHistory2 = new ReservationHistory("05:00", "06:00", LocalDate.now().minusDays(9), section5);
            ReservationHistory reservationHistory3 = new ReservationHistory("20:00", "23:00", LocalDate.now().minusWeeks(2), section6);

            ReservationHistory reservationHistory4 = new ReservationHistory("09:00", "16:15", LocalDate.now().minusWeeks(6), section7);
            ReservationHistory reservationHistory5 = new ReservationHistory("09:00", "12:00", LocalDate.now().minusWeeks(4), section8);
            ReservationHistory reservationHistory6 = new ReservationHistory("09:00", "10:30", LocalDate.now().minusWeeks(8), section9);
            ReservationHistory reservationHistory7 = new ReservationHistory("09:00", "10:30", LocalDate.now().minusDays(2), section9);
            ReservationHistory reservationHistory8 = new ReservationHistory("09:00", "10:30", LocalDate.now().minusDays(2), section10);


            reservationHistoryRepo.save(reservationHistory);
            reservationHistoryRepo.save(reservationHistory1);
            reservationHistoryRepo.save(reservationHistory2);
            reservationHistoryRepo.save(reservationHistory3);
            reservationHistoryRepo.save(reservationHistory4);
            reservationHistoryRepo.save(reservationHistory5);
            reservationHistoryRepo.save(reservationHistory6);
            reservationHistoryRepo.save(reservationHistory7);
            reservationHistoryRepo.save(reservationHistory8);
        }
    }
}
