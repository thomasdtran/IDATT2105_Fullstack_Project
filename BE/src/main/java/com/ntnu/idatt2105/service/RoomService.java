package com.ntnu.idatt2105.service;

import com.ntnu.idatt2105.model.Room;
import com.ntnu.idatt2105.model.Section;
import com.ntnu.idatt2105.repo.RoomRepo;
import com.ntnu.idatt2105.repo.SectionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * Service class responsible for handling functionality for rooms.
 */

@Service
public class RoomService {

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private SectionRepo sectionRepo;

    Logger logger = LoggerFactory.getLogger(RoomService.class);

    /**
     * Method for finding all rooms. Calls method in Repo class.
     * @return List with all rooms.
     */
    public List<Room> findAllRooms() { return roomRepo.findAll(); }

    /**
     * Method to find a room by id
     * @param id of the room
     * @return the room with the given id, null if no such id is found
     */
    public Room findRoomById(long id) { return roomRepo.findById(id); }

    /**
     * Method to create room
     * todo: sjekk at bruker som lager rom er admin bruker
     * @param room to be created
     * @return the room that has been created
     */
    public Room createRoom(Room room){
        roomRepo.save(room);
        Iterator<Section> sectionIterator = room.getSections().iterator();

        while (sectionIterator.hasNext()) {
            Section newSection = sectionIterator.next();
            if (newSection.getName() != "" && newSection.getSuggestedAmountPeople() > 0) {
                newSection.setRoom(room);
                sectionRepo.save(newSection);
            }
        }
        return room;
    }

    /**
     * method to update roooms
     * todo: sjekk at admin og sjekk ny datas gyldighet, trenger nye sekjsoner?
     * @param newRoomData new info about the room to be updated
     * @param id of the room to be updated
     * @return the updated room
     */
    public Room updateRoom(Room newRoomData, long id){
        Room current = roomRepo.findById(id);

        if( !(current.getName().equals(newRoomData.getName())) && newRoomData.getName()!=null ){
            current.setName(newRoomData.getName());
        }
        //Set.equals(anotherSet)
        //returns true if the size of both the sets are equal and both contain the same elements
        if( !(current.getSections().equals(newRoomData.getSections())) && newRoomData.getSections()!=null){

            //Checks if new sections already exists, if not adds the section(s)
            boolean exists;
            for (Section newSection : newRoomData.getSections()) {
                exists = false;
                for (Section currentSection : current.getSections()){
                    if (currentSection.getName().equals(newSection.getName())
                            && currentSection.getSuggestedAmountPeople() == newSection.getSuggestedAmountPeople()){
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    newSection.setRoom(current);
                    sectionRepo.save(newSection);
                }
            }

            //Checks old sections still exists, if not deletes them
            for (Section currentSection : current.getSections()) {
                exists = false;
                for (Section newSection : newRoomData.getSections()) {
                    if (currentSection.getName().equals(newSection.getName())
                            && currentSection.getSuggestedAmountPeople() == newSection.getSuggestedAmountPeople()) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    logger.info(currentSection.getName());
                    sectionRepo.delete(currentSection);
                }
            }
            current.setSections(newRoomData.getSections());
        }
        return roomRepo.save(current);
    }

    /**
     * method to delete a room
     * todo: autentiser at bruker av metoden er admin
     * @param id of the room to be deleted
     */
    public void deleteRoom(long id){
        roomRepo.deleteById(id);
    }

    /**
     * method to add a new section to an existing room
     * section cannot exist without parent room
     * todo: burde denne ha return verdi? idk
     * @param roomId of the room to get a new section
     * @param newSectionName name of the new section
     * @param newSuggestAmount suggested amount of people in the new section
     */
    public void addNewSectionToRoom(long roomId, String newSectionName, int newSuggestAmount){
        Room currentRoom = roomRepo.findById(roomId);
        if( currentRoom == null
            || newSectionName.equals("")
            || newSuggestAmount < 1
        ) throw new IllegalArgumentException("invalid data given");
        Section newSection = new Section();
        newSection.setName(newSectionName);
        newSection.setSuggestedAmountPeople(newSuggestAmount);
        newSection.setRoom(currentRoom);
        currentRoom.getSections().add(newSection);
        sectionRepo.save(newSection);
    }

    /**
     * method to remove a section from an existing room
     * @param roomId
     * @param sectionId
     */
    public void removeSectionFromRoom(long roomId, long sectionId){
        Room room = roomRepo.findById(roomId);
        Section section = sectionRepo.findById(sectionId);
        if (room == null || section == null || !room.getSections().contains(section)){
            throw new IllegalArgumentException("invalid data");
        }
        room.getSections().remove(section);
        sectionRepo.delete(section);
    }


}
