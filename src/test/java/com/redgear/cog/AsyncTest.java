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

        Person jeremy = new Person("Jeremy", "Bond", 33);

        // In case there is dirty state from previous run.
        personRepo.deletePerson(jeremy);

        Assert.assertEquals(1, personRepo.insertPerson(jeremy));
        Assert.assertEquals(1, personRepo.setAge(jeremy, 34).get().intValue());
        Assert.assertEquals(34, personRepo.findOneByExample(jeremy).getAge());
        personRepo.deletePerson(jeremy);
    }



}
