package com.smartcampus.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/discovery")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response discover() {
        String json = "{"
            + "\"version\":\"1.0\","
            + "\"contact\":\"admin@smartcampus.com\","
            + "\"description\":\"Smart Campus Sensor & Room Management API\","
            + "\"resources\":{"
            + "\"rooms\":\"/api/v1/rooms\","
            + "\"sensors\":\"/api/v1/sensors\""
            + "}"
            + "}";
        return Response.ok(json).build();
    }
}