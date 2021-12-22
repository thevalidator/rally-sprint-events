/*
 * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.thekrechetofficial.entity;

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
import ru.thekrechetofficial.repository.StageRepository;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JpaConfig.class)
@Sql(scripts = "classpath:rse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class StageTest {
    
    private final StageRepository stageRepository;
    private final EventRepository eventRepository;
    
    @Autowired
    public StageTest(StageRepository stageRepository, EventRepository eventRepository) {
        this.stageRepository = stageRepository;
        this.eventRepository = eventRepository;
    }
    
    @Test
    public void getById(){
        Event event = eventRepository.findById(1L).get();
        Stage stage = event.getStages().get(1);
        Stage stageDB = stageRepository.findById(2L).get(); 
        assertEquals(stage.getStageName(), stageDB.getStageName());
    }
    
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testSomeMethod() {
    }
    
}
