package com.redgear.cog.repo;


import com.redgear.cog.beans.ClientProvider;
import com.redgear.cog.beans.Person;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.redgear.cog.core.MapBuilder.mapOf;

public class BasicTest {

    private static final Logger log = LoggerFactory.getLogger(BasicTest.class);

    @Test
    public void fullTest1() {

        CogClient client = ClientProvider.getClient();

        CogStatement<Person> getAllPeople = client.prepareStatement("select first_name, last_name, age from person where last_name = :lastName", Person.class);


        getAllPeople.query(mapOf("lastName", "Smith").build())
                .forEach(person -> log.info("Person: {}", person));

    }

}
