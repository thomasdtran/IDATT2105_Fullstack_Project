package com.ntnu.idatt2105.repo;

import com.ntnu.idatt2105.model.Room;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoomRepo extends JpaRepository<Room, Long>{
    Room findById(long id);
}
