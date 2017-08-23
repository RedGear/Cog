package com.redgear.cog.beans;

import com.redgear.cog.core.Param;
import com.redgear.cog.repo.Query;

import java.util.List;
import java.util.Map;

public interface PersonRepositoryRest {

    @Query("SELECT * FROM person")
    List<Person> getAllPeople();

    @Query("SELECT first_name, last_name, age FROM person WHERE last_name = :lastName")
    List<Person> findByLastName(@Param("lastName") String lastName);

    @Query("SELECT first_name, last_name, age FROM person WHERE first_name = :firstName AND last_name = :lastName")
    Person findOneByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);
    @Query("SELECT * FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Person findOneByMap(@Param("ex") Map<String, String> ex);

}
