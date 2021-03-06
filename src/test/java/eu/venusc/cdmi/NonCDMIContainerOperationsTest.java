package eu.venusc.cdmi;

import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_CREATED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_DELETED;
import static eu.venusc.cdmi.CDMIResponseStatus.REQUEST_NOT_FOUND;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class NonCDMIContainerOperationsTest extends CDMIConnectionWrapper {


    NonCDMIContainerOperations cops;
    static String containerName;
    static String baseContainer;
    static Random random = new Random();

    public NonCDMIContainerOperationsTest(String name) throws Exception {
        super(name);
        cops = cdmiConnection.getNonCdmiContainerProxy();
    }


    @Before
    public void setUp() throws Exception {
        baseContainer = "/";
        if (baseContainer.charAt(baseContainer.length()-1)!='/')
            baseContainer = baseContainer + "/";

        containerName = "noncdmi-container" + random.nextInt();
    }
    @After
    public void tearDown() throws IOException, ParseException,
            CDMIOperationException, URISyntaxException {

        String[] children = cops.getChildren(baseContainer, false);

        for (int i = 0; i < children.length; i++) {
            String url = baseContainer + children[i];
            HttpResponse response = cops.delete(url);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode == REQUEST_NOT_FOUND
                    || responseCode == REQUEST_DELETED)
                continue;
            else
                fail("Container " + url + " could not be cleaned up." + responseCode);

        }
    }

    @Test
    public void testCreate() throws ClientProtocolException, IOException,
            CDMIOperationException, URISyntaxException {
        HttpResponse response = cops.create(baseContainer + containerName,
                parameters);
        int responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Creating container failed: " + baseContainer + containerName
                + "/", REQUEST_CREATED, responseCode);
    }

    @Test
    public void testGetChildren() throws ClientProtocolException, IOException,
            CDMIOperationException, ParseException, URISyntaxException {

        Set<String> newContainers = new HashSet<String>();

        // create containers
        for (int i = 0; i < 3; i++) {
            String container_name = containerName + "_" + i;
            HttpResponse response = cops.create(baseContainer + container_name, parameters);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode != REQUEST_CREATED)
                fail("Could not create  the container: " + baseContainer + container_name);
            newContainers.add(container_name + "/");
        }

        String[] children = cops.getChildren(baseContainer, false);

        Set<String> childSet = new HashSet<String>();
        for (int i = 0; i < children.length; i++) {
            childSet.add(children[i]);
        }

        assertTrue("Checking created container children failed: ", childSet.containsAll(newContainers));
        childSet.clear();

    }

    @Test
    public void testDelete() throws ClientProtocolException, IOException,
            CDMIOperationException, URISyntaxException {

        // Create a container
        HttpResponse response = cops.create(baseContainer + containerName, parameters);
        int responseCode = response.getStatusLine().getStatusCode();

        if (responseCode != REQUEST_CREATED)
            fail("Could not create the container: " + baseContainer
                    + containerName);
        // delete the container
        response = cops.delete(baseContainer + containerName);
        responseCode = response.getStatusLine().getStatusCode();
        assertEquals("Container could not be deleted: " + baseContainer + containerName,
                REQUEST_DELETED, responseCode);
    }
}
