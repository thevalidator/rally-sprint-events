///*
// * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package ru.thekrechetofficial.service;
//
//import java.util.List;
//import java.util.Set;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.thekrechetofficial.dto.CrewDto;
//import ru.thekrechetofficial.entity.Category;
//import ru.thekrechetofficial.entity.Crew;
//import ru.thekrechetofficial.entity.Event;
//import ru.thekrechetofficial.entity.Pilot;
//import ru.thekrechetofficial.entity.Vehicle;
//import ru.thekrechetofficial.repository.CrewRepository;
//import ru.thekrechetofficial.repository.EventRepository;
//import ru.thekrechetofficial.repository.PilotRepository;
//import ru.thekrechetofficial.repository.StageRepository;
//import ru.thekrechetofficial.repository.VehicleRepository;
//
///**
// * @author theValidator <the.validator@yandex.ru>
// */
//@Service
//@Transactional
//public class CrewServiceImpl_1 implements CrewService {
//
//    private final CrewRepository crewRepository;
//    private final StageRepository stageRepository;
//    private final EventRepository eventRepository;
//    private final PilotRepository pilotRepository;
//    private final VehicleRepository vehicleRepository;
//
//    @Autowired
//    public CrewServiceImpl_1(CrewRepository crewRepository,
//            StageRepository stageRepository,
//            EventRepository eventRepository,
//            PilotRepository pilotRepository,
//            VehicleRepository vehicleRepository
//   ) {
//        this.crewRepository = crewRepository;
//        this.stageRepository = stageRepository;
//        this.eventRepository = eventRepository;
//        this.pilotRepository = pilotRepository;
//        this.vehicleRepository = vehicleRepository;
//    }
//
//    @Override
//    public Crew getById(Long crewId) {
//        Crew c = crewRepository.findById(crewId).orElseThrow();
//        initCrew(c);
//        
//        return c;
//    }
//
//    @Override
//    public Crew getByPilotId(Long pilotId, Long eventId) {
//        Crew c = crewRepository.findByPilotId(pilotId, eventId).orElseThrow();
//        initCrew(c);
//        
//        return c;
//    }
//
//    @Override
//    public List<Crew> getAllActiveCrewByEventId(long eventId) {
//        List<Crew> crews = crewRepository.findAllActiveByEventId(eventId);
//        initCrews(crews);
//        return crews;
//    }
//    
//    
//    
//    
//    @Override
//    public List<Crew> getAllCrewByEventId(long eventId) {
//        List<Crew> crews = crewRepository.findAllByEventId(eventId);
//        initCrews(crews);
//        return crews;
//    }
//    
////    @Override
////    public List<Crew> getAllCrewByStageId(long stageId) {
////        long eventId = stageRepository.getEventIdByStageId(stageId);
////        List<Crew> crews = getAllCrewByEventId(eventId);
////        
////        return crews;
////    }
//
//    @Override
//    public void deleteCrewById(Long crewId) {
//        Crew c = getById(crewId);
//        crewRepository.delete(c);
//    }
//
//    @Override
//    public int registerForEvent(CrewDto dto) {
//        Set<Integer> startNums = crewRepository.findAllEventStartNumbers(dto.getEventId());
//        if (startNums.contains(dto.getStartNumber())) {
//            return 0;
//        }
//                
//        Crew c = new Crew();
//        Event e = eventRepository.getById(dto.getEventId());
//        Pilot p = pilotRepository.getById(dto.getPilotId());
//        Vehicle v = vehicleRepository.getById(dto.getVehicleId());
//        
//        c.setEvent(e);
//        c.setPilot(p);
//        c.setVehicle(v);
//        c.setStartNumber(dto.getStartNumber());
//        c.setCategory(Category.valueOf(dto.getCategory()));
//        c.setIsActive(true);
//        crewRepository.save(c);
//        
//        return 1;
//    }
//
//    @Override
//    public void disactivate(Long crewId, Long eventId) {
//        crewRepository.setIsActive(false, crewId, eventId);
//        
//    }
//
//    @Override
//    public void activate(Long crewId, Long eventId) {
//        System.out.println("yes 3 : crew/event " + crewId + " " + eventId);
//        crewRepository.setIsActive(true, crewId, eventId);
//    }
//
//    @Override
//    public boolean isExists(long pilotId, long eventId) {
//        return crewRepository.isExistsByPilotId(pilotId, eventId);
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    @Override
//    public void updateCrew(CrewDto crewDto, Long crewId) {
////        Crew c = getById(crewId);
////        transferCrewDataWithOutEvent(crewDto, c);
////        saveFullCrew(c);
//    }
//
//    @Override
//    public void deleteAllByEvent(long eventId) {
//        List<Crew> crews = crewRepository.findAllByEventId(eventId);
//        if (!crews.isEmpty()) {
//            for (Crew c : crews) {
//                crewRepository.delete(c);
//            }
//        }
//
//    }
//
//    private void initCrew(Crew c) {
//        c.getPilot().getFirstName();
//        c.getVehicle().getHp();
//    }
//
//    private void initCrews(List<Crew> crews) {
//        for (Crew c : crews) {
//            initCrew(c);
//        }
//    }
//
//}
