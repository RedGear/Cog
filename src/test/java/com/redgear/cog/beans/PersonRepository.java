package com.redgear.cog.beans;

import com.redgear.cog.CogParam;
import com.redgear.cog.CogQuery;

import java.util.List;
import java.util.concurrent.Future;

public interface PersonRepository {


    @CogQuery("select * from person")
    List<Person> getAllPeople();

    @CogQuery("select first_name, last_name, age from person where last_name = :lastName")
    List<Person> findByLastName(@CogParam("lastName") String lastName);

    @CogQuery("select first_name, last_name, age from person where first_name = :firstName and last_name = :lastName")
    Person findOneByFullName(@CogParam("firstName") String firstName, @CogParam("lastName") String lastName);


    @CogQuery("select * from person where first_name = :firstName and last_name = :lastName")
    Future<Person> findOneFutureByFullName(String firstName, String lastName);

}
