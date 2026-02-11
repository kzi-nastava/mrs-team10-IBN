package com.example.UberComp.S3.repository;

import com.example.UberComp.model.Coordinate;
import com.example.UberComp.repository.CoordinateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class StopRideRepositoryTest {
    @Autowired
    private CoordinateRepository coordinateRepository;

    @Test
    public void testFindByAddress(){
        Coordinate c = coordinateRepository.findByAddress("Random Address 1").get();
        assertNotNull(c);
        assertEquals("Random Address 1", c.getAddress());
        assertEquals(15.0, c.getLat());
        assertEquals(25.0, c.getLon());

    }

    @Test
    public void testFindByCoordinates(){
        Coordinate c = coordinateRepository.findByLatAndLon(15.0, 25.0).get();
        assertNotNull(c);
        assertEquals("Random Address 1", c.getAddress());
        assertEquals(15.0, c.getLat());
        assertEquals(25.0, c.getLon());
    }
}
