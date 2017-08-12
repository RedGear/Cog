package com.redgear.cog.beans;

import com.redgear.cog.CogParam;
import com.redgear.cog.CogQuery;
import com.redgear.cog.ResultSource;

import java.util.List;
import java.util.concurrent.Future;

public interface PersonRepository {


    @CogQuery("SELECT * FROM person")
    List<Person> getAllPeople();

    @CogQuery("SELECT first_name, last_name, age FROM person WHERE last_name = :lastName")
    List<Person> findByLastName(@CogParam("lastName") String lastName);

    @CogQuery("SELECT first_name, last_name, age FROM person WHERE first_name = :firstName AND last_name = :lastName")
    Person findOneByFullName(@CogParam("firstName") String firstName, @CogParam("lastName") String lastName);


    @CogQuery("SELECT * FROM person WHERE first_name = :firstName AND last_name = :lastName")
    Future<Person> findOneFutureByFullName(@CogParam("firstName") String firstName, @CogParam("lastName") String lastName);

    @CogQuery("SELECT * FROM person")
    ResultSource<Person> getAllPeopleAsync();

}
