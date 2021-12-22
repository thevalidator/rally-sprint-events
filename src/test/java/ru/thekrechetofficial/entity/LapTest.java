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

import java.util.List;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.thekrechetofficial.config.JpaConfig;
import ru.thekrechetofficial.repository.CrewRepository;
import ru.thekrechetofficial.repository.EventRepository;
import ru.thekrechetofficial.repository.LapRepository;
import ru.thekrechetofficial.repository.PilotRepository;
import ru.thekrechetofficial.repository.StageRepository;
import ru.thekrechetofficial.repository.VehicleRepository;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JpaConfig.class)
@Transactional
public class LapTest {
    
    private final EventRepository eventRepository;
    private final StageRepository stageRepository;
    private final VehicleRepository vehicleRepository;
    private final PilotRepository pilotRepository;
    private final CrewRepository crewRepository;
    private final LapRepository lapRepository;
    
    @Autowired
    public LapTest(EventRepository eventRepository, StageRepository stageRepository, 
            VehicleRepository vehicleRepository, PilotRepository pilotRepository, 
            CrewRepository crewRepository, LapRepository lapRepository) {
        this.eventRepository = eventRepository;
        this.stageRepository = stageRepository;
        this.vehicleRepository = vehicleRepository;
        this.pilotRepository = pilotRepository;
        this.crewRepository = crewRepository;
        this.lapRepository = lapRepository;
    }
    
    @org.junit.jupiter.api.Test
    public void getByEventId() {
        
        List<Lap> laps = lapRepository.findAllByEventId(1L);
        System.out.println(laps.size());
        
        for (Lap l: laps) {
            System.out.println(l.getLapTime());
        }
        
        assertEquals(10, laps.size());
        
    }
    
    
}
