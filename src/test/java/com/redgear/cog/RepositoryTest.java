package com.redgear.cog;

import com.redgear.cog.beans.ClientProvider;
import com.redgear.cog.beans.Person;
import com.redgear.cog.beans.PersonRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RepositoryTest {



    @Test
    public void repoTest1() throws ExecutionException, InterruptedException, TimeoutException {
        CogClient client = ClientProvider.getClient();

        PersonRepository personRepo = client.buildRepository(PersonRepository.class);

        Person dave = new Person("Dave", "Smith", 35);
        Person mary = new Person("Mary", "Smith", 34);
        Person henry = new Person("Henry", "Robertson", 46);

        Assert.assertEquals(Arrays.asList(dave, mary, henry), personRepo.getAllPeople());
        Assert.assertEquals(Arrays.asList(dave, mary), personRepo.findByLastName("Smith"));
        Assert.assertEquals(henry, personRepo.findOneByFullName("Henry", "Robertson"));
        Assert.assertEquals(dave, personRepo.findOneFutureByFullName("Dave", "Smith").get(5, TimeUnit.SECONDS));
        Assert.assertEquals(Arrays.asList(dave, mary, henry), personRepo.getAllPeopleAsync().toList().get(5, TimeUnit.SECONDS));
        Assert.assertEquals(mary, personRepo.findOneByExample(mary));
        Assert.assertEquals(henry, personRepo.findOneByMap(MapBuilder.mapOf("firstName", "Henry").put("lastName", "Robertson").build()));
        Assert.assertEquals(19, personRepo.findAgeByExample(henry));
    }

}
