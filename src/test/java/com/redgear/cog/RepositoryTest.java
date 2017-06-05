package com.redgear.cog;

import com.redgear.cog.beans.ClientProvider;
import com.redgear.cog.beans.Person;
import com.redgear.cog.beans.PersonRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RepositoryTest {



    @Test
    public void repoTest1() throws ExecutionException, InterruptedException {
        CogClient client = ClientProvider.getClient();

        PersonRepository personRepo = client.buildRepository(PersonRepository.class);

        Person dillon = new Person("Dillon", "Callis", 25);
        Person malcolm = new Person("Malcolm", "Callis", 22);
        Person jenny = new Person("Jenny", "Diesel", 19);

        Assert.assertEquals(Arrays.asList(dillon, malcolm, jenny), personRepo.getAllPeople());
        Assert.assertEquals(Arrays.asList(dillon, malcolm), personRepo.findByLastName("Callis"));
        Assert.assertEquals(jenny, personRepo.findOneByFullName("Jenny", "Diesel"));
        Assert.assertEquals(dillon, personRepo.findOneFutureByFullName("Dillon", "Callis").get());
    }

}
