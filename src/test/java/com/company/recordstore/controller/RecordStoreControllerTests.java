package com.company.recordstore.controller;

import com.company.recordstore.models.Record;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(RecordStoreController.class)
public class RecordStoreControllerTests {

    // Wiring in the MockMvc object
    @Autowired
    private MockMvc mockMvc;

    // ObjectMapper used to convert Java objects to JSON and vice versa
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        // This is the standard set up method that runs before each test. It's typically used for instantiating test
        // objects. We don't have to do anything special for mockMvc since it's Autowired. We will however be using
        // setUp() in the future.
    }

    @Test
    public void shouldReturn422StatusCodeWithInvalidRequestBody() throws Exception {
        // ARRANGE
        Record inputRecord = new Record();
        inputRecord.setArtist("Bruce Springsteen");
        inputRecord.setAlbum("The River");

        // Convert Java Object to JSON.
        String inputJson = mapper.writeValueAsString(inputRecord);

        // ACT
        mockMvc.perform(
                post("/records")                                // Perform the POST request.
                        .content(inputJson)                               // Set the request body.
                        .contentType(MediaType.APPLICATION_JSON)          // Tell the server it's in JSON format.
        )
                .andDo(print())                                           // Print results to console.
                .andExpect(status().isUnprocessableEntity());             // ASSERT (status code is 422)
    }

    @Test
    public void shouldReturn404StatusCodeIfRecordNotFound() throws Exception {
        mockMvc.perform(get("/records/-1"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn422StatusCodeIfIdsDoNotMatch() throws Exception {
        Record inputRecord = new Record();
        inputRecord.setArtist("William Joel");
        inputRecord.setAlbum("The Stranger");
        inputRecord.setYear("1977");
        inputRecord.setId(2);

        String inputJson = mapper.writeValueAsString(inputRecord);

        mockMvc.perform(
                put("/records/5")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void shouldReturn422StatusCodeIfRequestBodyIsInvalid() throws Exception {
        Record inputRecord = new Record();
        inputRecord.setArtist("William Joel");
        inputRecord.setAlbum("The Stranger");
        inputRecord.setId(2);

        String inputJson = mapper.writeValueAsString(inputRecord);

        mockMvc.perform(
                put("/records/2")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    // testing GET record/{id}
    @Test
    public void shouldReturnRecordById() throws Exception {

        // ARRANGE
        Record outputRecord = new Record();
        outputRecord.setArtist("Billy Joel");
        outputRecord.setAlbum("The Stranger");
        outputRecord.setYear("1977");
        outputRecord.setId(2);

        String outputJson = mapper.writeValueAsString(outputRecord);

        // ACT
        mockMvc.perform(get("/records/2"))
                .andDo(print())
                .andExpect(status().isOk())                     // ASSERT that we got back 200 OK.
                .andExpect(content().json(outputJson));         // ASSERT that what we're expecting is what we got back.
    }

    // testing POST /records
    @Test
    public void shouldReturnNewRecordOnPostRequest() throws Exception {

        // ARRANGE
        Record inputRecord = new Record();
        inputRecord.setArtist("Bruce Springsteen");
        inputRecord.setAlbum("The River");
        inputRecord.setYear("1980");

        // Convert Java Object to JSON.
        String inputJson = mapper.writeValueAsString(inputRecord);

        Record outputRecord = new Record();
        outputRecord.setArtist("Bruce Springsteen");
        outputRecord.setAlbum("The River");
        outputRecord.setYear("1980");
        outputRecord.setId(6);

        String outputJson = mapper.writeValueAsString(outputRecord);

        // ACT
        mockMvc.perform(
                post("/records")                            // Perform the POST request.
                        .content(inputJson)                           // Set the request body.
                        .contentType(MediaType.APPLICATION_JSON)      // Tell the server it's in JSON format.
        )
                .andDo(print())                                // Print results to console.
                .andExpect(status().isCreated())               // ASSERT (status code is 201)
                .andExpect(content().json(outputJson));        // ASSERT that what we're expecting is what we got back.
    }

    // testing GET /records
    @Test
    public void shouldReturnAllRecordsInCollection() throws Exception {

        // ARRANGE and ACT
        mockMvc.perform(get("/records"))       // Perform the GET request.
                .andDo(print())                          // Print results to console.
                .andExpect(status().isOk())              // ASSERT (status code is 200)

                // ASSERT that the JSON array is present and not empty. We will test GET all endpoints deeper in the
                // future but this is good enough for now.
                .andExpect(jsonPath("$[0]").isNotEmpty());
    }

    // testing PUT /records/{id}
    @Test
    public void shouldUpdateByIdAndReturn204StatusCode() throws Exception {

        // ARRANGE
        Record inputRecord = new Record();
        inputRecord.setArtist("William Joel");
        inputRecord.setAlbum("The Stranger");
        inputRecord.setYear("1977");
        inputRecord.setId(2);

        String inputJson = mapper.writeValueAsString(inputRecord);

        // ACT
        mockMvc.perform(
                put("/records/2")
                        .content(inputJson)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isNoContent()); // ASSERT that we got back 204 NO CONTENT.

        // ACT
        mockMvc.perform(
                get("/records/2")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(content().json(inputJson)); // ASSERT that the record was updated successfully.
    }

    // testing DELETE /records/{id}
    @Test
    public void shouldDeleteByIdAndReturn204StatusCode() throws Exception {

        // This method returns nothing, so we're just checking for the correct status code.
        // In this case, code 204, which indicates No Content. We will test deletes deeper in the future.
        mockMvc.perform(delete("/records/5"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
