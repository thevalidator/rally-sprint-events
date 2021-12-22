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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.transaction.Transactional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
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
@Sql(scripts = "classpath:rse.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class PilotTest {

    private final EventRepository eventRepository;
    private final StageRepository stageRepository;
    private final VehicleRepository vehicleRepository;
    private final PilotRepository pilotRepository;
    private final CrewRepository crewRepository;
    private final LapRepository lapRepository;
   

    @Autowired
    public PilotTest(EventRepository eventRepository, StageRepository stageRepository,
            VehicleRepository vehicleRepository, PilotRepository pilotRepository, 
            CrewRepository crewRepository, LapRepository lapRepository) {
        this.eventRepository = eventRepository;
        this.stageRepository = stageRepository;
        this.vehicleRepository = vehicleRepository;
        this.pilotRepository = pilotRepository;
        this.crewRepository = crewRepository;
        this.lapRepository = lapRepository;
    }

//    public PilotTest(CrewRepository crewRepository) {
//        this.crewRepository = crewRepository;
//    }
//    public PilotTest(VehicleRepository vehicleRepository, PilotRepository pilotRepository) {
//        this.vehicleRepository = vehicleRepository;
//        this.pilotRepository = pilotRepository;
//    }
    @org.junit.jupiter.api.Test
    public void getById() {

        Lap lap = lapRepository.findById(1L).get();
        Penalty p = lap.getPenalty();
        Crew c = lap.getCrew();
        Pilot p1 = c.getPilot();
        Stage s = lap.getStage();
        Event e = s.getEvent();
        Event e1 = c.getEvent();
        Vehicle v = c.getVehicle();
        long t = lap.getLapTime() + lap.getPenalty().getPenaltyTime();

        System.out.printf("%s %s %d %s: %s (%s %s) %s %s\n",
                e.getEventName(), s.getStageName(), c.getStartNumber(), t, lap.getFormattedLapTime(),
                lap.getPenalty().getFormattedPenaltyTime(), p.getDescription(), p1.getLastName(), v.getModel());

        Event ev2 = new Event();
        ev2.setEventName("RALLY SEPT");
        ev2.setEventDate(LocalDate.now());
        Stage st1 = new Stage();
        Stage st2 = new Stage();
        st1.setEvent(ev2);
        st2.setEvent(ev2);
        st1.setStageName("SEP_PRA");
        st2.setStageName("SEP_RACE");

        Pilot pi1 = new Pilot();
        Vehicle ve1 = new Vehicle();
        ve1.setMake("Nissan");
        ve1.setModel("Note");
        ve1.setHp(115);
        ve1.setYear(2003);
        ve1.setPlateNumber("A444HT90");
        pi1.setFirstName("pilot 1");
        pi1.setLastName("first");
        pi1.getVehicles().add(ve1);

        Pilot pi2 = new Pilot();
        Vehicle ve2 = new Vehicle();
        ve2.setMake("Seat");
        ve2.setModel("Leon Cupra");
        ve2.setHp(155);
        ve2.setYear(2011);
        ve2.setPlateNumber("B344OT77");
        pi2.setFirstName("pilot 2");
        pi2.setLastName("second");
        pi2.getVehicles().add(ve2);

        Crew cr1 = new Crew();
        Crew cr2 = new Crew();
        cr1.setStartNumber(222);
        cr2.setStartNumber(333);
        cr1.setEvent(ev2);
        cr2.setEvent(ev2);
        cr1.setPilot(pi1);
        cr2.setPilot(pi2);
        cr1.setVehicle(ve1);
        cr2.setVehicle(ve2);

        List<Lap> laps = new ArrayList<>();
        
        final Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Lap l = new Lap();
            long lapTime = 50_000;
            lapTime = lapTime + random.nextInt(5) * 1_000;
            lapTime = lapTime + random.nextInt(999);
            int vehicleHp = cr1.getVehicle().getHp();
            if (vehicleHp > 200) {
                lapTime = lapTime - random.nextInt(800);
            } else if (vehicleHp > 140) {
                lapTime = lapTime - random.nextInt(500);
            }
            l.setCrew(cr1);
            l.setLapNumber(i+1);
            l.setStage(st1);
            l.setLapTime(lapTime);
            l.setPenalty(new Penalty());
            
            laps.add(l);
        }
        for (int i = 0; i < 10; i++) {
            Lap l = new Lap();
            long lapTime = 50_000;
            lapTime = lapTime + random.nextInt(5) * 1_000;
            lapTime = lapTime + random.nextInt(999);
            int vehicleHp = cr1.getVehicle().getHp();
            if (vehicleHp > 200) {
                lapTime = lapTime - random.nextInt(800);
            } else if (vehicleHp > 140) {
                lapTime = lapTime - random.nextInt(500);
            }
            l.setCrew(cr2);
            l.setLapNumber(i+1);
            l.setStage(st1);
            l.setLapTime(lapTime);
            l.setPenalty(new Penalty());
            
            laps.add(l);
        }
        
        for (int i = 0; i < 2; i++) {
            Lap l = new Lap();
            long lapTime = 50_000;
            lapTime = lapTime + random.nextInt(5) * 1_000;
            lapTime = lapTime + random.nextInt(999);
            int vehicleHp = cr1.getVehicle().getHp();
            if (vehicleHp > 200) {
                lapTime = lapTime - random.nextInt(800);
            } else if (vehicleHp > 140) {
                lapTime = lapTime - random.nextInt(500);
            }
            l.setCrew(cr1);
            l.setLapNumber(i+1);
            l.setStage(st2);
            l.setLapTime(lapTime);
            Penalty pen =  new Penalty();
            pen.setPenaltyTime(5L);
            l.setPenalty(pen);
            laps.add(l);
        }
        for (int i = 0; i < 2; i++) {
            Lap l = new Lap();
            long lapTime = 50_000;
            lapTime = lapTime + random.nextInt(5) * 1_000;
            lapTime = lapTime + random.nextInt(999);
            int vehicleHp = cr1.getVehicle().getHp();
            if (vehicleHp > 200) {
                lapTime = lapTime - random.nextInt(800);
            } else if (vehicleHp > 140) {
                lapTime = lapTime - random.nextInt(500);
            }
            l.setCrew(cr2);
            l.setLapNumber(i+1);
            l.setStage(st2);
            l.setLapTime(lapTime);
            Penalty pen =  new Penalty();
            pen.setPenaltyTime(3L);
            l.setPenalty(pen);
            laps.add(l);
        }
        
        
        eventRepository.save(ev2);
        stageRepository.save(st1);
        stageRepository.save(st2);
        vehicleRepository.save(ve1);
        vehicleRepository.save(ve2);
        pilotRepository.save(pi1);
        pilotRepository.save(pi2);
        crewRepository.save(cr1);
        crewRepository.save(cr2);
        for (Lap l: laps) {
            lapRepository.saveAndFlush(l);
        }
//        
//        
//        
        List<Lap> laps2 = lapRepository.findAllByEventName("RALLY SEPT");
//        
        for (Lap l: laps2) {
            System.out.printf("%s %s: [%d] %d %s, %s %s %s %s\n", l.getStage().getEvent().getEventName(),
                    l.getStage().getStageName(), l.getCrew().getStartNumber(),
                    l.getLapNumber(), l.getFormattedLapTime(), l.getPenalty().getFormattedPenaltyTime(),
                    l.getCrew().getPilot().getFirstName(), l.getCrew().getVehicle().getMake(),
                    l.getCrew().getCategory().getName());
        }

        //check event from crew and stage
        //Crew crew1 = crewRepository.findById(1L).get();
        //Crew crew2 = crewRepository.findById(4L).get();
        //Pilot p1 = crew1.getPilot();
//        System.out.printf("Pilot: %s %s\n", p1.getFirstName(), p1.getLastName());
//        Set<Vehicle> p1Vehicles = p1.getVehicles();
//        for (Vehicle v: p1Vehicles) {
//            System.out.printf("\t- %s %s %s %d %d %s\n", 
//                    v.getMake(), v.getModel(), v.getPlateNumber(),
//                    v.getYear(), v.getHp(), v.getCategory().getOption());
//        }
        //System.out.println(crew1.toString());
        //System.out.println(crew2.toString());
        System.out.println(e == e1);
        assertEquals("Chainik", p1.getFirstName());
        assertEquals(e1, e);

    }

}
