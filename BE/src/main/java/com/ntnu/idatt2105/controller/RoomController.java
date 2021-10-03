package com.ntnu.idatt2105.controller;

import com.ntnu.idatt2105.aop.TokenRequired;
import com.ntnu.idatt2105.aop.TokenRequiredAdmin;
import com.ntnu.idatt2105.model.Room;
import com.ntnu.idatt2105.model.Section;
import com.ntnu.idatt2105.service.RoomService;
import com.ntnu.idatt2105.service.SectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * controller for rooms
 */
@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    SectionService sectionService;

    Logger logger = LoggerFactory.getLogger(RoomController.class);

    /**
     * Getmapping for finding all rooms
     * @return list of all rooms
     */
    @GetMapping
    @TokenRequired
    public ResponseEntity<?> findAllRooms(){
        List<Room> allRooms = roomService.findAllRooms();
        logger.info("All rooms shown");
        return new ResponseEntity<>(allRooms, HttpStatus.OK);
    }

    /**
     * GetMapping for finding room by id
     * @param id PathVariable from client
     * @return room and HttpStatus.OK or HttpStatus.BAD_REQUEST
     */
    @GetMapping("/{id}")
    @TokenRequired
    public ResponseEntity<?> findRoomById(@PathVariable long id) {
        Room room = roomService.findRoomById(id);
        if(room == null) {
            logger.info("unable not find room with id "+id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("Found room with id: " + id);
        return new ResponseEntity<>(room, HttpStatus.OK);
    }

    /**
     * Postmapping for creating a new room
     * todo: authenticate isadmin, try catch for accessexception
     * todo: check new room data validity, validity not defined yet
     * @param room requestbody form client
     * @return activity and HttpStatus.CREATED or HttpStatus.BAD_REQUEST
     */
    @PostMapping
    @TokenRequiredAdmin
    public ResponseEntity<?> createRoom(@RequestBody Room room){

            Room room1 = roomService.createRoom(room);
            if (room1 == null){
                //ingen sjekker i roomservice createroom ennå så burde ikke komme hit
                logger.info("Wrong format was given in creating of the room");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            logger.info("New room created");
            return new ResponseEntity<>(room1, HttpStatus.CREATED);
    }

    /**
     * putmapping for updating a room
     * todo: try catch for user authority acessexception
     * @param room reqbody with the new room info
     * @return room and httpstatus
     */
    @PutMapping("/{id}")
    @TokenRequiredAdmin
    public ResponseEntity<?> updateRoom(@RequestBody Room room, @PathVariable long id){
        Room updatedRoom = roomService.updateRoom(room, id);

        if(updatedRoom == null){
            logger.info("Failed to update room with id: " + id+", room not found");
            return new ResponseEntity<>(updatedRoom, HttpStatus.NOT_FOUND);
        }

        logger.info("Updating room with id:" + id);
        return new ResponseEntity<>(updatedRoom, HttpStatus.OK);
    }

    /**
     * Deletemapping for deleting a room
     * todo: try catch for accessexception
     * @param id of room to be deleted
     * @return httpstatus
     */
    @DeleteMapping("/{id}")
    @TokenRequiredAdmin
    public ResponseEntity<?> deleteRoom(@PathVariable long id){
        try {
            roomService.deleteRoom(id);
            logger.info("Deleting room with id: "+id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.info("failed to delete room with id: "+id+", room does not exist");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Method to add a new section to an existing room
     * todo: validate user authority
     * @param id of room to recieve new section
     * @param section to be added
     * @return httpstatus ok or bad request, depending on whether a room with the id was found
     */
    @PostMapping("/{id}/sections")
    @TokenRequiredAdmin
    public ResponseEntity<?> addNewSectionToRoom(@RequestBody Section section, @PathVariable long id){
        try{
            roomService.addNewSectionToRoom(id, section.getName(), section.getSuggestedAmountPeople());
            logger.info("adding section with name: "+ section.getName() + ", to room with id: "+id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            logger.info("invalid data given in attempt to add section to room with id "+id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * method to update a section in an existing room
     * todo: test user authority
     * @param section the new section data
     * @param roomId id of the room the section is in
     * @param sectionId id of the section to update
     * @return httpresponse based on the validity of the data
     */
    @PutMapping("/{roomId}/{sectionId}")
    @TokenRequiredAdmin
    public ResponseEntity<?> updateSectionInRoom(@RequestBody Section section, @PathVariable long roomId, @PathVariable long sectionId){
        section.setRoom(roomService.findRoomById(roomId));
        try{
            sectionService.updateSection(section, sectionId);
            logger.info("updating section (id: "+sectionId+") in room with id: "+roomId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            logger.info("error occurred when updating section (id: "+sectionId+") in room with id: "+roomId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * method for deleting a section in an existing room
     * todo: user authentication
     * @param roomId room with the section to be deleted
     * @param sectionId section to be deleted
     * @return httpresponse based on the validity of the data
     */
    @DeleteMapping("/{roomId}/{sectionId}")
    @TokenRequiredAdmin
    public ResponseEntity<?> removeSectionFromRoom(@PathVariable long roomId, @PathVariable long sectionId){
        try{
            roomService.removeSectionFromRoom(roomId, sectionId);
            logger.info("removing section with id: "+ sectionId + " from room with id: "+roomId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            logger.info("error occurred when trying to remove section with id: "+ sectionId + " from room with id: "+roomId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }





}
