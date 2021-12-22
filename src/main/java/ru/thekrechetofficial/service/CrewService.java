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
package ru.thekrechetofficial.service;

import java.util.List;
import ru.thekrechetofficial.dto.CrewDto;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Pilot;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
public interface CrewService {

    Crew getById(long crewId);
    
    Crew getByPilotId(long pilotId, long eventId);

    void deleteAllByEvent(long eventId);

    List<Crew> getAllCrewByEventId(long eventId);
    
    List<Crew> getAllActiveCrewsByEventId(long eventId);
    
    List<Crew> getAllDidNotStarted(long eventId, long stageId);

    //void updateCrew(CrewDto crewDto, long crewId);

    void deleteCrewById(long crewId);

    int registerForEvent(CrewDto dto);

    void disactivate(long crewId, long eventId);
    
    void activate(long crewId, long eventId);;
    
    boolean isExists(long pilotId, long eventId);

    boolean isExistsInActive(long id, long eventId);

    int getAllActiveCrewSizeByEventId(long eventId);
    
    Long getCrewIdIfExistsInActiveByPilot(long pilotId, long eventId);
    
    void setIsPaid(boolean b, Long crewId);
   
    void setIsApproved(boolean b, Long crewId);
    
    int correctCrew(CrewDto dto);

    Crew getByStartNumber(long eventId, int crewStartNumber);

    //List<TotalResultCrewDto> getAllCompletedCrewsSortedByTotalTime(Long eventId);

    int getCrewLapsAmountByStage(long stageId, long crewId);
    
    
    
}
