/*
 * The Krechet Software
 */
package ru.thekrechetofficial.entity;

import java.time.LocalDate;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.thekrechetofficial.config.JpaConfig;
import ru.thekrechetofficial.repository.EventRepository;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JpaConfig.class)
@Sql(scripts = "classpath:rse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class EventTest {
    
    private final EventRepository eventRepository;
    
    @Autowired
    public EventTest(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    @Test
    public void getById(){
        Event event = eventRepository.findById(2L).get(); 
        assertEquals("RALLY AUGUST", event.getEventName());
        
    }
    
    @Test
    void create(){
        Event event = new Event();
        event.setEventName("RALLY OCTOBER");
        event.setEventDate(LocalDate.now());
        

        eventRepository.save(event);

        assertEquals("RALLY OCTOBER", eventRepository.findById(3L).get().getEventName());
        assertEquals(3, eventRepository.count());
    }
    
    @Test
    void findByDtCreatedBetween(){
        LocalDate from =  LocalDate.now().minusMonths(2);
        LocalDate to =  LocalDate.now().minusDays(1);

        assertEquals(2, eventRepository.findByEventDateBetween(from, to).size());
    }
    
    @Test
    void update(){
        Event event = eventRepository.findById(1L).get();
        String changedName = "JULY JULY";
        event.setEventName(changedName);
        eventRepository.save(event);
        
        assertEquals(changedName, eventRepository.findById(1L).get().getEventName());
    }

//    @Test
//    void delete(){
//        
//        eventRepository.deleteById(2L);
//        assertEquals(1, eventRepository.count());
//    }

    @Test
    void findByNameLike(){
        String name = "%julY";
        Event event = eventRepository.findByEventNameLikeIgnoreCase(name).get(0);
        assertEquals("RALLY JULY", event.getEventName());
    }
    
//    @BeforeClass
//    public static void setUpClass() 
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }

    
}
