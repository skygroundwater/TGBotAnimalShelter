package com.telegrambotanimalshelter.repositories;


import com.telegrambotanimalshelter.models.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteersRepository extends JpaRepository<Volunteer, Long> {

    List<Volunteer> findVolunteersByIsFreeTrue();

    Volunteer findByUserName(String userName);

}
