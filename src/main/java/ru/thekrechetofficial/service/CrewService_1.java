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
//import java.io.IOException;
//import java.util.List;
//import java.util.Set;
//import org.springframework.web.multipart.MultipartFile;
//import ru.thekrechetofficial.dto.CrewDto;
//import ru.thekrechetofficial.entity.Crew;
//import ru.thekrechetofficial.entity.Pilot;
//
///**
// *
// * @author theValidator <the.validator@yandex.ru>
// */
//public interface CrewService_1 {
//
//    Crew getById(Long crewId);
//    
//    Crew getByPilotId(Long pilotId, Long eventId);
//
//    void deleteAllByEvent(long eventId);
//
//    //Set<Crew> getCrewSetByEventId(long id);
//
//    List<Crew> getAllCrewByEventId(long eventId);
//    
//    List<Crew> getAllActiveCrewByEventId(long eventId);
//
//    //List<Crew> getAllCrewByStageId(long stageId);
//
//    //void quickAddCrewFromFile(MultipartFile file, long eventId) throws IOException;
//
//    //boolean isPresent(long eventId, int crewStartNum);
//
//    void updateCrew(CrewDto crewDto, Long crewId);
//
//    void deleteCrewById(Long crewId);
//
//    int registerForEvent(CrewDto dto);
//
//    void disactivate(Long crewId, Long eventId);
//    
//    void activate(Long crewId, Long eventId);;
//    
//    boolean isExists(long pilotId, long eventId);
//}
