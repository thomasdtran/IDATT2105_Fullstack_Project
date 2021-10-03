package com.ntnu.idatt2105.service;

import com.ntnu.idatt2105.model.*;
import com.ntnu.idatt2105.repo.ReservationHistoryRepo;
import com.ntnu.idatt2105.repo.ReservationRepo;
import com.ntnu.idatt2105.repo.RoomRepo;
import com.ntnu.idatt2105.repo.SectionRepo;
import com.ntnu.idatt2105.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Service class responsible for handling functionality for reservations.
 */
@Configuration
@EnableScheduling
@EnableAsync
@Service
public class ReservationService {

    @Autowired
    ReservationRepo reservationRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    SectionRepo sectionRepo;

    @Autowired
    RoomRepo roomRepo;

    @Autowired
    SecurityService securityService;

    @Autowired
    ReservationHistoryRepo reservationHistoryRepo;

    Logger logger = LoggerFactory.getLogger(SectionService.class);

    /**
     * Method for creating a reservation
     * @param reservation Reservation object from front-end
     * @param userId Identifier of user who is making the reservation
     * @return The reservation if no errors occur
     */
    public Reservation createReservation(Reservation reservation, long userId) throws ParseException {
        if (!hasInfo(reservation.getSections())){
            throw new IllegalArgumentException("Reservation has no sections");
        }

        if (!hasInfo(reservation.getDate()) || !hasInfo(reservation.getStartTime())
                || !hasInfo(reservation.getEndTime())){
            throw new IllegalArgumentException("Reservation date or time not found");
        }

        if (!hasInfo(userId)){
            throw new IllegalArgumentException("No userID connected to reservation");
        }

        if (!isValidDate(reservation.getDate(), reservation.getEndTime())){
            throw new IllegalArgumentException("Reservation date is invalid");
        }

        User user = userRepo.findById(userId);
        if(user == null){
            throw new IllegalArgumentException("User doesnt exist with id: " + userId);
        }
        reservation.setUser(user);

        if (!isValidTime(reservation.getStartTime(), reservation.getEndTime())){
            throw new IllegalArgumentException("Reservation time is invalid");
        }


        // Check if sections in reservation already are booked
        for (Section section : reservation.getSections()){
            if (areSectionsInReservationReserved(section, reservation.getDate(), reservation.getStartTime(), reservation.getEndTime())){
                throw new IllegalArgumentException("One or more sections in reservation is already reserved");
            }
        }

        logger.info("Nr of sections in reservation: " + reservation.getSections().size());
        reservation = reservationRepo.save(reservation);
        return reservationRepo.save(reservation);
    }



    /**
     * Method for deleting a reservation (admin privileges)
     * @param id ID of the reservation
     */
    public void deleteReservationAdmin(long id){
        Reservation reservation = reservationRepo.findReservationById(id);
        User user = reservation.getUser();

        if (!hasInfo(user)){
            throw new IllegalArgumentException("No user found correlated to reservation");
        }

        if (!hasInfo(reservation)){
            throw new IllegalArgumentException("No reservation with given id found");
        }
            reservationRepo.deleteById(id);
    }

    /**
     * Method for deleting reservation for user. User needs to be authenticated
     * @param id ID of the reservation
     */
    public void deleteReservation(long id){
        Reservation reservation = reservationRepo.findReservationById(id);
        User user = reservation.getUser();

        if (!hasInfo(user)){
            throw new IllegalArgumentException("No user found correlated to reservation");
        }

        if (!hasInfo(reservation)){
            throw new IllegalArgumentException("No reservation with given id found");
        }

        try {
            ServletRequestAttributes reqAttributes = (ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes();
            HttpServletRequest request = reqAttributes.getRequest();
            // checks for token in request header
            String token = request.getHeader("Authorization");
            try {
                if (token.startsWith("Bearer")) {
                    token = token.substring("Bearer ".length());
                }
            } catch (NullPointerException e) {
                System.out.println("Header is null");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Header is null");
            }
            String subject = securityService.getSubject(token);

            if (!subject.equals(String.valueOf(user.getId()))){
                throw new IllegalArgumentException("User does not have permission to delete reservation");
            }

            reservationRepo.deleteById(id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Method for finding all reservations. Calls method in Repo class.
     * @return List with all reservations.
     */
    public List<Reservation> findAllReservations() {
        List<Reservation> reservations = reservationRepo.findAll();
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getDate().isBefore(LocalDate.now())) {
                reservations.remove(reservations.get(i));
                i--;
            }
        }
        return reservations;
    }

    /**
     * Method for finding all current reservations by a user.
     * @param id Id of the user to retrieve reservations for.
     * @return List with user current reservations.
     */
    public List<Reservation> findAllReservationsUser(long id) {
        List<Reservation> reservations = reservationRepo.findAll();
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getUser().getId() != id || reservations.get(i).getDate().isBefore(LocalDate.now())) {
                reservations.remove(reservations.get(i));
                i--;
            }
        }
        return reservations;
    }

    /**
     * Method to find a reservation by id
     * @param id of a reservation
     * @return the reservation with the given id, null if no such id is found
     */
    public Reservation findReservationById(long id) { return reservationRepo.findReservationById(id); }

    /**
     * Method for finding all available rooms given a timeframe. 
     * After finding the rooms and the belonging sections, it also checks if rooms/sections
     * can compensate for the desired amount of people.
     * @param date Potential date for new reservation
     * @param fromTime Potential start time of new reservation
     * @param endTime Potential end time for new reservation
     * @param noOfPeople Desired amount of people using the room/sections. 
     * @param sameSection A boolean saying if the search based on amount of people should be based
     * on a single section, or the whole room. 
     * @param roomName A none required field to search based on room name
     * @return List of available rooms
     */
    public List<Room> fetchAvailableRooms(String date, String fromTime, String endTime,
    int noOfPeople, boolean sameSection, String roomName){

        LocalDate resDate = LocalDate.parse(date);
    
        List<Room> availableRooms = new ArrayList<>();
        List<Room> allRooms = roomRepo.findAll();

        if(!isValidDate(resDate, endTime)){
            return availableRooms;
        }
        //Loops through all the rooms
        for (Room room : allRooms) {
            if((roomName != null) && !(room.getName().toLowerCase().contains(roomName.toLowerCase()))){
                continue;
            }
            int notAvailableCounter = 0;
            Set<Section> sections = room.getSections();
            // A copy to remove sections which is not valid for the search
            Set<Section> sectionsCopy = new HashSet<>(sections);
            //Saves the original amount of sections a room has
            int orignalSectionSize = sections.size();
            room.setNoOfSections(orignalSectionSize);

            /*
            Loops through each section in a room, and each seactions
            connection to already made reservations. 
            */
            for (Section section: sections) {
                Collection<Reservation> reservations = section.getReservations();
                for (Reservation reservation : reservations) {
                    LocalTime previousResStartTime = LocalTime.parse(reservation.getStartTime());
                    LocalTime previousResEndTime = LocalTime.parse(reservation.getEndTime());
                    LocalTime reqFromTime = LocalTime.parse(fromTime);
                    LocalTime reqEndTime = LocalTime.parse(endTime);
                    
                    /* 
                    Uses laws of propositional logic to check if any of the reservations regarding a specific section
                    overlaps with the desired date and time. 
                     */
                    if (reservation.getDate().equals(resDate)) {
                        //Overlaps if true
                        if ((reqFromTime.isBefore(previousResEndTime) || reqFromTime.equals(previousResEndTime))
                            && (reqEndTime.isAfter(previousResStartTime)
                            || reqEndTime.equals(previousResStartTime))) {
                            notAvailableCounter++;
                            sectionsCopy.remove(section);
                            break;
                        }
                    }
                
                }
            }
            //Checks if there are any valid sections left
            if(notAvailableCounter < orignalSectionSize){
                if(sameSection){
                    //Makes a new array for the sections which complies to the need of space in the same section
                    Set<Section> validSections = new HashSet<>();
                    for (Section section : sectionsCopy) {
                        if(section.getSuggestedAmountPeople() >= noOfPeople){
                            validSections.add(section);
                        }
                    }
                    if(validSections.size() > 0){
                        room.setSections(validSections);
                        availableRooms.add(room);
                    }
                }else{
                    /*
                    Instead at looking at a single section, checks if
                    the room as a whole complies with the desired amount of people
                    */
                    int space = 0;
                    for (Section section : sectionsCopy) {
                        space += section.getSuggestedAmountPeople();
                    }
                    if(space >= noOfPeople){
                        room.setSections(sectionsCopy);
                        availableRooms.add(room);
                    } 
                }
            }
        }
        return availableRooms;
    }

    /**
     * Method to automatically delete reservations and add to history of reservations
     */
    @Async
    @Scheduled(fixedRate = 30000) // 15 min
    public void deleteReservationsScheduler(){
        logger.info("Scheduled cleanse");
        List<Reservation> reservations = reservationRepo.findAll();
        for (Reservation reservation : reservations){
            LocalDate nowDate = LocalDate.now();
            if (!reservation.getDate().isBefore(nowDate)){
                // checks if activity day is equal to today, and if activity (in hours) is less than "now-time"
                if (nowDate.equals(reservation.getDate()) && LocalTime.parse(reservation.getEndTime()).isBefore(LocalTime.now())){
                    logger.info("Deleting reservation: " + reservation.toString());
                    createReservationHistory(reservation);
                    reservationRepo.deleteById(reservation.getId());
                }
            }
            else {
                createReservationHistory(reservation);
                reservationRepo.deleteById(reservation.getId());
            }
        }
    }


    /**
     * Method to create a ReservationHistory object
     * @param res Reservation object that will be saved
     */
    private void createReservationHistory(Reservation res){
        Collection<Section> sectionsInReservation = res.getSections();
        for (Section section : sectionsInReservation) {
            ReservationHistory reservationHistory = new ReservationHistory(res, section);
            reservationHistoryRepo.save(reservationHistory);
        }

    }

    /**
     * Method for checking if the object has info stored
     * todo: meant for title, location, intensity and sport. These object may change and will need different checks.
     * @param object object to be checked
     * @return boolean confirming if the object has info
     */
    public boolean hasInfo(Object object){
        return object != null;
    }

    /**
     * Method for processing if reservation date is valid
     * @param reservationDate The date of the reservation
     * @return Returns false if reservationDate is either before today, or after 2 weeks from now
     */
    public boolean isValidDate(LocalDate reservationDate, String endTime){

        LocalDate nowDate = LocalDate.now();
        // Setting a max time frame of 2 weeks where you can reserve a room
        LocalDate futureDate = LocalDate.now().plusWeeks(2);

        if (nowDate.isAfter(reservationDate) || reservationDate.isAfter(futureDate)){
            return false;
        } else if (nowDate.equals(reservationDate) && LocalTime.parse((endTime)).isBefore(LocalTime.now())){
            return false;
        }

        return true;
    }


    /**
     * Method of processing if reservation time (in hours) are valid
     * @param startTime Start time of a reservation
     * @param endTime End time of a reservation
     * @return Returns true if start time is before end time, false otherwise
     */
    public boolean isValidTime(String startTime, String endTime){
        if (LocalTime.parse(startTime).isAfter(LocalTime.parse(endTime))) return false;
        return true;
    }

    /**
     * Method for finding out if sections in a new reservation are already reserved in the same time slot
     * @param section One section of the reservation
     * @param resFromTime Start time of reservation
     * @param resEndTime End time of reservation
     * @return Returns true if a section in the new reservation is booked, false otherwise
     */
    public boolean areSectionsInReservationReserved(Section section, LocalDate newReservationDate, String resFromTime, String resEndTime) throws ParseException {
        for (Reservation res : reservationRepo.findAll()){
            for (Section sec : res.getSections()){
                if (section.getId() == sec.getId()){

                    LocalTime newResStartTime = LocalTime.parse(resFromTime);
                    LocalTime newResEndTime = LocalTime.parse(resEndTime);
                    LocalTime previousResStartTime = LocalTime.parse(res.getStartTime());
                    LocalTime previousResEndTime = LocalTime.parse(res.getEndTime());

                    if (res.getDate().equals(newReservationDate)){
                        if ((newResStartTime.isBefore(previousResEndTime) || newResStartTime.equals(previousResEndTime))
                                && (newResEndTime.isAfter(previousResStartTime) || newResEndTime.equals(previousResStartTime))){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
