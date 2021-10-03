package com.ntnu.idatt2105.controller;

import com.ntnu.idatt2105.aop.TokenRequired;
import com.ntnu.idatt2105.aop.TokenRequiredAdmin;
import com.ntnu.idatt2105.model.Reservation;
import com.ntnu.idatt2105.model.Room;
import com.ntnu.idatt2105.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

/**
 * controller for reservations
 */
@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin
public class ReservationController {
    @Autowired
    ReservationService reservationService;

    Logger logger = LoggerFactory.getLogger(RoomController.class);


    /**
     * Method for creating a new reservation
     * @param reservation Reservation object sent from front end
     * @param userID User id to the user wanting to create a reservation
     * @return The new reservation if successful
     */
    @PostMapping("/{userID}")
    @TokenRequired
    public ResponseEntity<?> createReservation(@RequestBody Reservation reservation, @PathVariable long userID){
        try {
            Reservation reservation1 = reservationService.createReservation(reservation, userID);
            if (reservation1 == null){
                logger.info("Reservation could not be created (ReservationController)");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            logger.info("New reservation was made");
            return new ResponseEntity<>(reservation1, HttpStatus.CREATED);
        } catch (Exception e){
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Method for deleting reservation
     * @param id Id of reservation
     * @return HTTP status
     */
    @DeleteMapping("/{id}")
    @TokenRequired
    public ResponseEntity<?> deleteReservationUser(@PathVariable long id) {
         try {
            reservationService.deleteReservation(id);
            logger.info("Deleted reservation with ID: " + id);
             return new ResponseEntity<>(HttpStatus.OK);
         } catch (Exception e) {
             e.printStackTrace();
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
         }
    }


    /**
     * Method for deleting reservation
     * @param id Id of reservation
     * @return HTTP status
     */
    @DeleteMapping("/admin/{id}")
    @TokenRequiredAdmin
    public ResponseEntity<?> deleteReservationAdmin(@PathVariable long id) {
        try {
            reservationService.deleteReservationAdmin(id);
            logger.info("Deleted reservation with ID: " + id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method for finding all reservations
     * @return List of reservations if successfull
     */
    @GetMapping
    @TokenRequiredAdmin
    public ResponseEntity<?> findAllReservations(){
        try{
            List<Reservation> reservations = reservationService.findAllReservations();
            logger.info("Successfully fetching reservations");
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        }catch(NullPointerException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method for finding all current reservations for user.
     * @param id Id of the user
     * @return List of all current reservations if successfull
     */
    @GetMapping("/user/{id}")
    @TokenRequired
    public ResponseEntity<?> findAllReservationsUser(@PathVariable long id) {
        try {
            List<Reservation> reservations = reservationService.findAllReservationsUser(id);
            logger.info("Successfully retrieved all users reservations");
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method for finding a reservation by given ID
     * @param id ID of a reservation
     * @return Returns the given reservation if found
     */
    @GetMapping("/{id}")
    @TokenRequiredAdmin
    public ResponseEntity<?> findReservationById(@PathVariable long id) {
        Reservation reservation = reservationService.findReservationById(id);
        if(reservation == null) {
            logger.info("unable not find reservation with id " + id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Found room with id: " + id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }


    /**
     * Method for finding all available rooms in a time frame
     * @param date Potential date for new reservation
     * @param fromTime Potential start time of new reservation
     * @param endTime Potential end time for new reservation
     * @return List of rooms
     */
    @TokenRequired
    @GetMapping("/search")
    public ResponseEntity<?> fetchAvailableRooms(@RequestParam String date, @RequestParam String fromTime, 
    @RequestParam String endTime, @RequestParam int noOfPeople, @RequestParam boolean sameSection,
    @RequestParam(required = false) String roomName){
        try{
            List<Room> rooms = reservationService.fetchAvailableRooms(date, fromTime, endTime, 
            noOfPeople, sameSection, roomName);
            logger.info("Successfully fetching available rooms");
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        } catch (Exception e){
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


}
