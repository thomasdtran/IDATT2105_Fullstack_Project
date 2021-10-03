package com.ntnu.idatt2105.service;

import com.ntnu.idatt2105.model.Room;
import com.ntnu.idatt2105.model.Section;
import com.ntnu.idatt2105.repo.SectionRepo;
import com.ntnu.idatt2105.repo.RoomRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class responsible for handling functionality for sections.
 */

@Service
public class SectionService {

    @Autowired
    private SectionRepo sectionRepo;
    @Autowired
    private RoomRepo roomRepo;

    Logger logger = LoggerFactory.getLogger(SectionService.class);

    /**
     * Method for finding all sections. calls method in repo class.
     * @return list with every section
     */
    public List<Section> findAllSections() {return sectionRepo.findAll();}

    /**
     * method to find a section given its id
     * @param id of the section
     * @return the section with given id, null if section isnt found
     */
    public Section findSectionById(long id) {return sectionRepo.findById(id);}

    /**
     * Method to create section
     * todo: check isadmin
     * @param section to be created
     * @return the section that was just created
     */
    public Section createSection(Section section){
        sectionRepo.save(section);
        logger.info("created new section with id: "+section.getId());
        return section;
    }

    /**
     * method to update a section
     * todo: sjekk at admin
     * @param newSectionData new info about the section to be updated
     * @param id of the section to be updated
     * @return the updated section
     */
    public Section updateSection(Section newSectionData, long id){

        Room newRoom = roomRepo.findById(newSectionData.getRoom().getId());
        if(newRoom == null){
            throw new IllegalArgumentException("room id does not exist");
        }

        Section current = sectionRepo.findById(id);

        if( !(current.getName().equals(newSectionData.getName())) && newSectionData.getName()!=null ){
            current.setName(newSectionData.getName());
        }

        if( current.getSuggestedAmountPeople() != newSectionData.getSuggestedAmountPeople()
            && newSectionData.getSuggestedAmountPeople() > 0)
        {
            current.setSuggestedAmountPeople(newSectionData.getSuggestedAmountPeople());
        }

        if( !(current.getRoom().equals(newSectionData.getRoom())) && newSectionData.getRoom()!=null){
            current.setRoom(current.getRoom());
        }
        return sectionRepo.save(current);
    }

    /**
     * method to delete a room
     * @param id of the section to be deleted
     */
    public void deleteSection(long id){
        sectionRepo.deleteById(id);
    }
}
