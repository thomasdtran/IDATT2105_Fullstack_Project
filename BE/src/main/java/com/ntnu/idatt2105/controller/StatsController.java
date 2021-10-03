package com.ntnu.idatt2105.controller;

import com.ntnu.idatt2105.aop.TokenRequired;
import com.ntnu.idatt2105.service.RoomService;
import com.ntnu.idatt2105.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;

/**
 * Controller to retreive statistics regarding the rooms and sections
 */
@RestController
@RequestMapping("/api/v1/stats")
@CrossOrigin
public class StatsController {

    @Autowired
    StatsService statsService;

    @Autowired
    RoomService roomService;

    Logger logger = LoggerFactory.getLogger(StatsController.class);

    
    /**
     * Method to find alltime stats for a room. 
     * @param roomID
     * @return Response depending on the outcome, if it managed to retrieve the 
     * stats or not
     */
    @GetMapping("/alltime/{roomID}")
    @TokenRequired
    public ResponseEntity<?> findSectionStatsAllTime(@PathVariable long roomID) {
        try {
            HashMap<Long, Integer> stats = statsService.findNumberOfReservationsForSectionsInRoom(roomID);
            logger.info("Succesfully retrieved stats from room with id " + roomID);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Failed to retrieve stats from room with id " + roomID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Method to find the stats on the usage of rooms in minutes
     * @param roomID
     * @param nrOfWeeks
     * @return
     */
    @GetMapping("/{roomID}/{nrOfWeeks}")
    @TokenRequired
    public ResponseEntity<?> totalMinutesReservedForRoomPastWeeks(@PathVariable long roomID, @PathVariable int nrOfWeeks){
        try {
            HashMap<String, Integer> stats = statsService.totalMinutesForReservedForRoom(roomID, nrOfWeeks);
            logger.info("Succesfully retrieved stats from room with id " + roomID);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e){
            logger.info("Failed to retrieve total minutes from room with id " + roomID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
