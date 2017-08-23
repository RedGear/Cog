package com.redgear.cog.beans;

import com.redgear.cog.core.Param;
import com.redgear.cog.repo.Query;
import com.redgear.cog.core.ResultSource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface PersonRepository {


    @Query("SELECT * FROM person")
    List<Person> getAllPeople();

    @Query("SELECT first_name, last_name, age FROM person WHERE last_name = :lastName")
    List<Person> findByLastName(@Param("lastName") String lastName);

    @Query("SELECT first_name, last_name, age FROM person WHERE first_name = :firstName AND last_name = :lastName")
    Person findOneByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT * FROM person WHERE first_name = :firstName AND last_name = :lastName")
    Future<Person> findOneFutureByFullName(@Param("firstName") String firstName, @Param("lastName") String lastName);

    @Query("SELECT * FROM person")
    ResultSource<Person> getAllPeopleAsync();

    @Query("SELECT first_name, last_name, age FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Person findOneByExample(@Param("ex") Person ex);

    @Query("SELECT age FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    int findAgeByExample(@Param("ex") Person ex);

    @Query("SELECT * FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Person findOneByMap(@Param("ex") Map<String, String> ex);

    @Query("INSERT INTO person (first_name, last_name, age) VALUES (:p.firstName, :p.lastName, :p.age)")
    int insertPerson(@Param("p") Person p);

    @Query("DELETE FROM person WHERE first_name = :p.firstName AND last_name = :p.lastName")
    void deletePerson(@Param("p") Person p);

    @Query("UPDATE person SET age = :age WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Future<Integer> setAge(@Param("ex") Person ex, @Param("age") int age);

}
