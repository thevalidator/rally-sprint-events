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
import ru.thekrechetofficial.dto.StageDto;
import ru.thekrechetofficial.entity.Stage;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
public interface StageService {

    void addStage(StageDto dto);
            
    //void createStage(StageDto stageDto);

    void deleteAllByEvent(long eventId);

    Stage getStageById(long stageId);
    
    Stage getStageWithEventById(long stageId);

    List<Stage> getAllByEventId(long eventId);
    
    List<Stage> getAllSortedStagesByEventId(long eventId);

    void deleteById(Long stageId);

    void confirmStageLaps(Long stageId);

    List<Stage> getAllStagesByTypeFromEvent(Long eventId, String type);

    void save(Stage stage);
    
    void deleteSS(Long eventId);
    
    void adSS(Long eventId);



}
