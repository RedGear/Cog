package com.redgear.cog.beans;

import com.redgear.cog.CogParam;
import com.redgear.cog.CogQuery;
import com.redgear.cog.ResultSource;

import java.util.List;
import java.util.Map;
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

    @CogQuery("SELECT first_name, last_name, age FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Person findOneByExample(@CogParam("ex") Person ex);

    @CogQuery("SELECT age FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    int findAgeByExample(@CogParam("ex") Person ex);

    @CogQuery("SELECT * FROM person WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Person findOneByMap(@CogParam("ex") Map<String, String> ex);

    @CogQuery("INSERT INTO person (first_name, last_name, age) VALUES (:p.firstName, :p.lastName, :p.age)")
    int insertPerson(@CogParam("p") Person p);

    @CogQuery("DELETE FROM person WHERE first_name = :p.firstName AND last_name = :p.lastName")
    void deletePerson(@CogParam("p") Person p);

    @CogQuery("UPDATE person SET age = :age WHERE first_name = :ex.firstName AND last_name = :ex.lastName")
    Future<Integer> setAge(@CogParam("ex") Person ex, @CogParam("age") int age);

}
