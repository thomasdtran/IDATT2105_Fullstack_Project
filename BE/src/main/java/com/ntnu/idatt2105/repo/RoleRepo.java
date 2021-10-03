package com.ntnu.idatt2105.repo;

import org.springframework.stereotype.Repository;
import com.ntnu.idatt2105.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long>{
}
