package com.company.recordstore.controller;


import com.company.recordstore.exceptions.NotFoundException;
import com.company.recordstore.models.Record;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RecordStoreController {

    private static int idCounter = 1;

    private static List<Record> recordList = new ArrayList<>(Arrays.asList(
            new Record("The Beach Boys", "Pet Sounds", "1966", idCounter++),
            new Record("Billy Joel", "The Stranger", "1977", idCounter++),
            new Record("Billy Joel", "The Stranger", "1978", idCounter++),
            new Record("The Beatles", "Revolver", "1966", idCounter++),
            new Record("Kanye West", "My Beautiful Dark Twisted Fantasy", "2010", idCounter++),
            new Record("Sturgill Simpson", "Metamodern Sounds in Country Music", "2014", idCounter++)
    ));

    @RequestMapping(value = "/records", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public Record createRecord(@RequestBody @Valid Record record) {

        record.setId(idCounter++);
        recordList.add(record);

        return record;
    }

    @RequestMapping(value = "/records", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public List<Record> getAllRecords(@RequestParam(required = false) String artist, @RequestParam(required = false) String year) {
        System.out.println("The value of artist is: " + artist);
        List<Record> returnVal = recordList;

        if (artist != null) {
            returnVal = returnVal
                    .stream()
                    .filter(v -> v.getArtist().contains(artist))
                    .collect(Collectors.toList());
        }

        if (year != null) {
            returnVal = returnVal
                    .stream()
                    .filter(v -> v.getYear().equals(year))
                    .collect(Collectors.toList());
        }

        return returnVal;
    }

    @RequestMapping(value = "/records/{id}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public Record getRecordById(@PathVariable int id) {
        Record foundRecord = null;

        for(Record record : recordList) {
            if(record.getId() == id) {
                foundRecord = record;
                break;
            }
        }

        if (foundRecord == null) {
            throw new NotFoundException("Record not found in collection");
        }

        return foundRecord;
    }

    @RequestMapping(value = "/records/{id}", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updateRecordById(@PathVariable int id, @RequestBody @Valid Record record) {

        if( record.getId() == 0 ) {
            record.setId(id);
        }

        if( record.getId() != id) {
            throw new IllegalArgumentException("Id in parameter must match the ID in the request body");
        }

        int index = -1;

        for(int i = 0; i < recordList.size(); i++) {
            if(recordList.get(i).getId() == id) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            recordList.set(index, record);
        }
    }

    @RequestMapping(value = "/records/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteRecordById(@PathVariable int id) {
        int index = -1;

        for(int i = 0; i < recordList.size(); i++) {
            if(recordList.get(i).getId() == id) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            recordList.remove(index);
        }
        else throw new NotFoundException("Record not found.");
    }
}
