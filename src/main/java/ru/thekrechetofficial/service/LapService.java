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
import org.springframework.web.multipart.MultipartFile;
import ru.thekrechetofficial.dto.PenaltyDto;
import ru.thekrechetofficial.entity.Lap;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
public interface LapService {

    List<Lap> getAllByEventIdSortByCrewThenLapNum(long eventId);

    int getStageLapAmountByCrewId(long stageId, long crewId);

    List<Lap> getAllByStageId(long stageId);
    
    List<Lap> getAllSortedByStageId(Long stageId);

    int parseThenSaveLaps(MultipartFile file, Long stageId);

    void deleteAllByEvent(long id);

    void deleteById(Long lapId);

    long addPenaltyToLap(Long lapId, PenaltyDto penaltyDto);
    
    List<Lap> getAllCrewBestLapByStageId(long eventId, long stageId,
                                         long eventId2, long stageId2);

    List<Lap> getAllByCrewIdAndStageId(long crewId, long stageId);

    int addLapsToStage(Long stageId, Integer config, MultipartFile file);

    Lap getLapById(Long lapId);

    void save(Lap lap);

    List<Lap> getPracticeLapsByStage(Long stageId, String userName);

}
