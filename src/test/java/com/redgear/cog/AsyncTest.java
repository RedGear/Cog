package com.redgear.cog;

import com.redgear.cog.beans.ClientProvider;
import com.redgear.cog.beans.Person;
import com.redgear.cog.beans.PersonRepository;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class AsyncTest {


    @Test
    public void asyncTest() throws ExecutionException, InterruptedException {
        CogClient client = ClientProvider.getClient();

        PersonRepository personRepo = client.buildRepository(PersonRepository.class);

        Person jacob = new Person("Jacob", "Carson", 33);

        // In case there is dirty state from previous run.
        personRepo.deletePerson(jacob);

        Assert.assertEquals(1, personRepo.insertPerson(jacob));
        Assert.assertEquals(1, personRepo.setAge(jacob, 34).get().intValue());
        Assert.assertEquals(34, personRepo.findOneByExample(jacob).getAge());
        personRepo.deletePerson(jacob);
    }



}
