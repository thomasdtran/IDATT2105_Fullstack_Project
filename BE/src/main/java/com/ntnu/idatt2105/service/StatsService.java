package com.ntnu.idatt2105.service;

import com.ntnu.idatt2105.model.Reservation;
import com.ntnu.idatt2105.model.ReservationHistory;
import com.ntnu.idatt2105.model.Room;
import com.ntnu.idatt2105.model.Section;
import com.ntnu.idatt2105.repo.ReservationHistoryRepo;
import com.ntnu.idatt2105.repo.ReservationRepo;
import com.ntnu.idatt2105.repo.RoomRepo;
import com.ntnu.idatt2105.repo.SectionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling functionality for stats.
 */
@Service
public class StatsService {

    @Autowired
    RoomRepo roomRepo;

    @Autowired
    ReservationHistoryRepo reservationHistoryRepo;
    Logger logger = LoggerFactory.getLogger(StatsService.class);


    /**
     * Method for processing amount of time a room has been reserved
     * @param roomID ID for the room
     * @param nrOfWeeks Weeks back in time stats will be calculated for
     * @return Hashmap of the rooms name and amount of minutes reserved
     */
    public HashMap<String, Integer> totalMinutesForReservedForRoom(long roomID, int nrOfWeeks){

        Room room = roomRepo.findById(roomID);

        if (!hasInfo(room)){
            throw new IllegalArgumentException("No room found with id: " + roomID);
        }

        if (nrOfWeeks <= 0){
            throw new IllegalArgumentException("Weeks cannot be equal or less than 0");
        }

        HashMap<String, Integer> totalMinutesReservedInRoom = new HashMap<>();
        for (Section section : room.getSections()){
            Integer totalReservedTime = 0;
            for (ReservationHistory reservationHistory : findReservationHistoryToSection(section.getId())){
                logger.info("Date for reservation" + reservationHistory.getId() + " is "+ reservationHistory.getDate().toString());
                LocalDate futureDate = LocalDate.now().minusWeeks(nrOfWeeks);
                logger.info("Checking for dates back to " + futureDate.toString());

                if ((reservationHistory.getDate().isAfter(futureDate) || reservationHistory.getDate().isEqual(futureDate))
                        && (reservationHistory.getDate().isBefore(LocalDate.now()) || reservationHistory.getDate().isEqual(LocalDate.now()))){
                    totalReservedTime += reservationHistory.getReservationLength().getHour()*60;
                    totalReservedTime += reservationHistory.getReservationLength().getMinute();
                    totalMinutesReservedInRoom.put(room.getName(), totalReservedTime);
                }
            }
        }
        return totalMinutesReservedInRoom;
    }


    /**
     * Method to find number of reservations for sections in a room
     * @param roomID id of room to be analyzed
     * @return hashmap with section id and number of occurrences
     */
    public HashMap<Long, Integer> findNumberOfReservationsForSectionsInRoom(long roomID){
        Room room = roomRepo.findById(roomID);

        if (!hasInfo(room)){
            throw new IllegalArgumentException("Room not found with id: " + roomID);
        }

        Collection<ReservationHistory> reservationHistories = new ArrayList<>();
        for (Section section : room.getSections()){
            reservationHistories.addAll(findReservationHistoryToSection(section.getId()));
        }

        HashMap<Long, Integer> sectionStats = new HashMap<>();
        reservationHistories.forEach(reservationHistory ->{
            if (!sectionStats.containsKey(reservationHistory.getSection().getId())){
                sectionStats.put(reservationHistory.getSection().getId(), 1);
            } else {
                int val = sectionStats.get(reservationHistory.getSection().getId()) + 1;
                sectionStats.put(reservationHistory.getSection().getId(), val);
            }
        });

        return sectionStats;
    }


    /**
     * Method for finding all reservations to a given section
     * @param sectionID ID to a section
     * @return A list of ReservationHistory object
     */
    private List<ReservationHistory> findReservationHistoryToSection(long sectionID){
        List<ReservationHistory> reservationHistories = new ArrayList<>();

        List<ReservationHistory> allReservationHistories = reservationHistoryRepo.findAll();
        for (ReservationHistory reservationHistory : allReservationHistories){
            if (reservationHistory.getSection().getId() == sectionID){
                reservationHistories.add(reservationHistory);
            }
        }
        return reservationHistories;
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


}
