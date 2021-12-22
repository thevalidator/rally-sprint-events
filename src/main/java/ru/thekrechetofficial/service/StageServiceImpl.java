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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thekrechetofficial.dto.StageDto;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Stage;
import ru.thekrechetofficial.entity.StageType;
import ru.thekrechetofficial.repository.EventRepository;
import ru.thekrechetofficial.repository.LapRepository;
import ru.thekrechetofficial.repository.StageRepository;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class StageServiceImpl implements StageService {

    private final StageRepository stageRepository;
    private final EventRepository eventRepository;
    private final LapService lapservice;
    private final LapRepository lapRepository;

    @Autowired
    public StageServiceImpl(StageRepository stageRepository,
            EventRepository eventRepository, LapService lapservice,
            LapRepository lapRepository) {
        this.stageRepository = stageRepository;
        this.eventRepository = eventRepository;
        this.lapservice = lapservice;
        this.lapRepository = lapRepository;
    }

    @Override
    public List<Stage> getAllStagesByTypeFromEvent(Long eventId, String type) {
        return stageRepository.findAllSessionsByTypeFromEvent(eventId, type);
    }

    @Override
    public void confirmStageLaps(Long stageId) {
        List<Lap> laps = lapservice.getAllByStageId(stageId);
        for (Lap l : laps) {
            lapRepository.setIsConfirmed(true, l.getId());
            lapservice.save(l);
        }
    }

    @Override
    public void deleteAllByEvent(long eventId) {
        List<Stage> stages = stageRepository.findAllByEventId(eventId);
        if (!stages.isEmpty()) {
            for (Stage s : stages) {
                stageRepository.delete(s);
            }
        }
    }

//    @Override
//    public void createStage(StageDto stageDto) {
//        Stage stage = new Stage();
//        stage.setStageName(stageDto.getStageName());
//        stage.setEvent(eventRepository
//                .findById((stageDto.getEventId()))
//                .orElseThrow());
//        stageRepository.save(stage);
//    }
    @Override
    public void addStage(StageDto dto) {
        Stage stage = new Stage();
        stage.setStageName(dto.getStageName());
        stage.setEvent(eventRepository
                .findById((dto.getEventId()))
                .orElseThrow());
        stage.setType(StageType.valueOf(dto.getStageType()));
        stageRepository.save(stage);
    }

    @Override
    public Stage getStageById(long stageId) {
        Stage stage = stageRepository.findById(stageId).orElseThrow();
        return stage;
    }

    @Override
    public Stage getStageWithEventById(long stageId) {
        Stage stage = getStageById(stageId);
        stage.getEvent().getEventName();
        return stage;
    }

    @Override
    public List<Stage> getAllByEventId(long eventId) {
        return stageRepository.findAllByEventId(eventId);
    }

    @Override
    public List<Stage> getAllSortedStagesByEventId(long eventId) {
        List<Stage> stages = stageRepository.getAllSortedStagesByEventId(eventId);
        for (Stage s : stages) {
            s.getLaps().size();
        }
        return stages;
    }

    @Override
    public void deleteById(Long stageId) {
        List<Lap> laps = lapservice.getAllByStageId(stageId);
        lapRepository.deleteAll(laps);
        stageRepository.deleteById(stageId);
    }

    @Override
    public void save(Stage stage) {
        stageRepository.saveAndFlush(stage);
    }

    @Override
    public void deleteSS(Long eventId) {
        Event e = eventRepository.findById(eventId).orElseThrow();
        List<Stage> stages = e.getStages();
        int size = stages.size();
        if (size >= 2) {
            int newSSAmount = size - 1;
            String name = "СУ-" + size;
            Stage s = stageRepository.findBystageNameAndEventId(name, eventId);

            stageRepository.delete(s);
            eventRepository.updateSSAmount(newSSAmount, eventId);
        }

    }

    @Override
    public void adSS(Long eventId) {
        Event e = eventRepository.findById(eventId).orElseThrow();
        List<Stage> stages = e.getStages();
        int newSSAmount = stages.size() + 1;
        Stage s = new Stage();
        s.setEvent(e);
        s.setType(StageType.SS);
        s.setStageName("СУ-" + newSSAmount);
        
        save(s);
        eventRepository.updateSSAmount(newSSAmount, eventId);
    }

}
