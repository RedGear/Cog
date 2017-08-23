package com.redgear.cog.rest;

import com.redgear.cog.beans.ClientProvider;
import com.redgear.cog.beans.PersonRepository;
import com.redgear.cog.beans.PersonRepositoryRest;
import com.redgear.cog.repo.CogClient;
import org.junit.Test;

public class BasicTest {

    @Test
    public void basicTest() throws Exception {
        CogClient client = ClientProvider.getClient();
        CogServer server = ClientProvider.getServer();


        PersonRepositoryRest repo = client.buildRepository(PersonRepositoryRest.class);


        server.addController("/rest/*", repo);

        server.start();
    }


}
