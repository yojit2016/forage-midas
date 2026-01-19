package com.jpmc.midascore.repository;

import com.jpmc.midascore.entity.UserRecord;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserRecord, Long> {

    // Add this line! This tells Spring how to generate the SQL to find a user by name
    UserRecord findByName(String name);

    // Also ensure your findById returns a UserRecord directly
    UserRecord findById(long id);
}