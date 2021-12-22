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
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thekrechetofficial.dto.CrewDto;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Pilot;
import ru.thekrechetofficial.entity.Vehicle;
import ru.thekrechetofficial.repository.CrewRepository;
import ru.thekrechetofficial.repository.EventRepository;
import ru.thekrechetofficial.repository.PilotRepository;
import ru.thekrechetofficial.repository.StageRepository;
import ru.thekrechetofficial.repository.VehicleRepository;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class CrewServiceImpl implements CrewService {

    private final CrewRepository crewRepository;
    //private final StageRepository stageRepository;
    private final EventRepository eventRepository;
    private final PilotRepository pilotRepository;
    private final VehicleRepository vehicleRepository;
    private final CategoryService categoryService;

    @Autowired
    public CrewServiceImpl(CrewRepository crewRepository, 
            EventRepository eventRepository, 
            PilotRepository pilotRepository, 
            VehicleRepository vehicleRepository, 
            CategoryService categoryService) {
        
        this.crewRepository = crewRepository;
        this.eventRepository = eventRepository;
        this.pilotRepository = pilotRepository;
        this.vehicleRepository = vehicleRepository;
        this.categoryService = categoryService;
    }

//    @Override
//    public List<TotalResultCrewDto> getAllCompletedCrewsSortedByTotalTime(Long eventId) {
//        //List<TotalResultCrewDto> result = crewRepository.findAllCompletedAndSortedByTotalTime(eventId);
//        
//        //return result;
//        return null;
//    }
    @Override
    public List<Crew> getAllDidNotStarted(long eventId, long stageId) {
        List<Crew> dnsCrews = crewRepository.findAllDidNotStarted(eventId, stageId);
        return dnsCrews;
    }

    @Override
    public Crew getByStartNumber(long eventId, int crewStartNumber) {
        Crew crew = crewRepository.findByStartNumberAndEventId(eventId, crewStartNumber).orElseThrow();
        crew.getLaps().size();

        return crew;
    }

    @Override
    public int correctCrew(CrewDto dto) {
        Crew crew = crewRepository.getById(dto.getCrewId());
        Set<Integer> startNums = crewRepository.findAllEventStartNumbers(dto.getEventId());
        if (((int) crew.getStartNumber() != (int) dto.getStartNumber()) && startNums.contains(dto.getStartNumber())) {
            return 0;
        }

        crew.setStartNumber(dto.getStartNumber());
        Category category = categoryService.getById(dto.getCategoryId());
        crew.setCategory(category);
        crewRepository.save(crew);
        return 1;
    }

    @Override
    public Crew getById(long crewId) {
        Crew c = crewRepository.findById(crewId).orElseThrow();
        initCrew(c);
        return c;
    }

    @Override
    public Crew getByPilotId(long pilotId, long eventId) {
        Crew c = crewRepository.findByPilotId(pilotId, eventId).orElseThrow();
        initCrew(c);
        return c;
    }

    @Override
    public List<Crew> getAllActiveCrewsByEventId(long eventId) {
        List<Crew> crews = crewRepository.findAllActiveByEventId(eventId);
        initCrews(crews);
        return crews;
    }

    @Override
    public int getAllActiveCrewSizeByEventId(long eventId) {
        return crewRepository.findAllActiveByEventId(eventId).size();
    }

    @Override
    public Long getCrewIdIfExistsInActiveByPilot(long pilotId, long eventId) {
        return crewRepository.getCrewIdIfExistsInActiveByPilot(pilotId, eventId);
    }

    @Override
    public List<Crew> getAllCrewByEventId(long eventId) {
        List<Crew> crews = crewRepository.findAllByEventId(eventId);
        initCrews(crews);
        return crews;
    }

    @Override
    public void setIsPaid(boolean b, Long crewId) {
        crewRepository.setIsPaid(b, crewId);
    }

    @Override
    public void setIsApproved(boolean b, Long crewId) {
        crewRepository.setIsApproved(b, crewId);
    }

    @Override
    public void deleteCrewById(long crewId) {
        Crew c = getById(crewId);
        crewRepository.delete(c);
    }

    @Override
    public int registerForEvent(CrewDto dto) {
        Set<Integer> startNums = crewRepository.findAllEventStartNumbers(dto.getEventId());

        Crew c;
        if (isExists(dto.getPilotId(), dto.getEventId())) {
            c = getByPilotId(dto.getPilotId(), dto.getEventId());
            if ((int) c.getStartNumber() != (int)dto.getStartNumber() && startNums.contains(dto.getStartNumber())) {
                return 0;
            }
        } else {
            c = new Crew();
            if (startNums.contains(dto.getStartNumber())) {
                return 0;
            }
            Event e = eventRepository.getById(dto.getEventId());
            Pilot p = pilotRepository.getById(dto.getPilotId());
            Vehicle v = vehicleRepository.getById(dto.getVehicleId());

            c.setEvent(e);
            c.setPilot(p);
            c.setVehicle(v);
        }

        c.setStartNumber(dto.getStartNumber());
        Category category = categoryService.getById(dto.getCategoryId());
        c.setCategory(category);
        c.setIsActive(true);
        crewRepository.save(c);

        return 1;
    }

    @Override
    public void disactivate(long crewId, long eventId) {
        crewRepository.setIsActive(false, crewId, eventId);

    }

    @Override
    public void activate(long crewId, long eventId) {
        crewRepository.setIsActive(true, crewId, eventId);
    }

    @Override
    public boolean isExists(long pilotId, long eventId) {
        return crewRepository.isExistsByPilotId(pilotId, eventId);
    }

    @Override
    public boolean isExistsInActive(long pilotId, long eventId) {
        return crewRepository.isExistsInActiveByPilotId(pilotId, eventId);
    }

//    @Override
//    public void updateCrew(CrewDto crewDto, long crewId) {
////        Crew c = getById(crewId);
////        transferCrewDataWithOutEvent(crewDto, c);
////        saveFullCrew(c);
//    }

    @Override
    public void deleteAllByEvent(long eventId) {
        List<Crew> crews = crewRepository.findAllByEventId(eventId);
        if (!crews.isEmpty()) {
            for (Crew c : crews) {
                crewRepository.delete(c);
            }
        }

    }

    @Override
    public int getCrewLapsAmountByStage(long stageId, long crewId) {
        return crewRepository.getCrewLapsAmountByStage(stageId, crewId);

    }

    private void fullInitCrew(Crew c) {
        c.getPilot().getFirstName();
        c.getVehicle().getHp();
        c.getCategory();
        List<Lap> laps = c.getLaps();
    }

    private void initCrew(Crew c) {
        c.getPilot().getFirstName();
        c.getVehicle().getHp();
        c.getCategory().getName();
    }

    private void initCrews(List<Crew> crews) {
        for (Crew c : crews) {
            initCrew(c);
        }
    }

}
