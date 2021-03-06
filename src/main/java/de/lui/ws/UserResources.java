package de.lui.ws;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import de.lui.dto.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Path("/user")
public class UserResources {
    private static final Logger log = Logger.getLogger(UserResources.class.getName());

    @GET
    @Path("/list")
    @Produces("application/json; charset=UTF-8")
    public Response getAllUsers(@Context HttpServletRequest httpRequest) {

        log.info("getAllUsers()");

        // connect to datastore and read data from kind user
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query q = new Query("user");
        PreparedQuery pq = datastore.prepare(q);

        // iterate resultset
        List<User> l = new ArrayList<>();
        pq.asIterable().forEach(e -> {
            User u = new User(
                    (String) e.getProperty("firstname"),
                    (String) e.getProperty("name"));
            l.add(u);
        });

        log.info("I found the users " + l);

        // return data, the json format will be generated automatically for you
        return Response.ok(l).build();
    }

    @POST
    @Path("/insert")
    @Produces("application/json; charset=UTF-8")
    public Response insertUser(@Context HttpServletRequest httpRequest, User user) {

        // connect to datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        user.id = System.currentTimeMillis();

        // primary key
        Key key = KeyFactory.createKey("user", user.id);

        Entity e = new Entity(key);
        e.setProperty("firstname", user.firstname);
        e.setProperty("name", user.name);
        e.setProperty("id", user.id);

        datastore.put(e);

        return Response.ok(user).build();
    }
}
