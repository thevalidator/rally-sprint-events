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
import org.springframework.ui.ModelMap;
import ru.thekrechetofficial.dto.EventDto;
import ru.thekrechetofficial.dto.GeneralResultDto;
import ru.thekrechetofficial.dto.GeneralResultWithSSDto;
import ru.thekrechetofficial.dto.IGeneralResultWithSS;
import ru.thekrechetofficial.dto.LapDtoTimesReport;
import ru.thekrechetofficial.dto.VarianceDto;
import ru.thekrechetofficial.entity.Event;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
public interface EventService {

    String createEvent(EventDto eventDto);
    
    Event getFullEventById(long id);

    void putEventDataToModel(ModelMap model, long id);

    void delete(long eventId);

    //void generateStagePilTable(Long stageId);

    //void generateStageAbsTable(Long stageId);

    //void generateStagePilTable(Long stageId, boolean withPenalty);

    //void generateStageCatBestResult(Long stageId);

    //void generateFinalResult(Long stageId);

    List<Event> getAllEventsForRegistration();

    List<Event> getAllEventsForManage();

    void setIsManaged(boolean b, Long eventId);
    
    void setHasRegistration(boolean b, Long eventId);  
    
    Event getEventById(long id);
    
    Event getEventWithTrackById(long id);
    
    List<GeneralResultWithSSDto> getSortedResultForCategory(Long eventId, String category);
    
    List<GeneralResultWithSSDto> getCategorySortedResultWithSS(Long eventId);
    
    List<VarianceDto> getVarianceCoefficientsForEvent(Long eventId);
    
    List<LapDtoTimesReport> getResultListForAbsoluteReportByStageType(Long event, String stageType);
    
    List<LapDtoTimesReport> getBestLapsForConfigReportByStageType(Long event, String stageType, int configuration);

    void updateEvent(Event event, EventDto dto);

}
