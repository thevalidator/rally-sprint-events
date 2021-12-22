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

package ru.thekrechetofficial.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.thekrechetofficial.entity.Stage;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    
    @Query(value = "select e.id "
                 + "from event e "
                 + "inner join stage s on e.id = s.event_id "
                 + "where s.id = ?", nativeQuery = true)
    long getEventIdByStageId(long stageId);
    
    List<Stage> findByStageName(String title);
    
    @Query(value = "select s.* "
            + "from stage s "
            + "inner join event e on e.id = s.event_id "
            + "where e.id = ?", nativeQuery = true)
    List<Stage> findAllByEventId(long id);
    
    
    @Query(value = "select l.id "
            + "from lap l "
            + "where l.stage_id = ?", nativeQuery = true)
    List<Long> getLapsIdByStageId(long id);
    
    @Query(value = "select count(id) from lap where stage_id=?",
            nativeQuery = true)
    int getLapsNumberByStageId(long id);
    
    @Query(value = "select count(t.crew_id) "
            + "from (select distinct crew_id as crew_id "
            + "from lap where stage_id=?) as t",
            nativeQuery = true)
    int getCrewsNumberByStageId(long id);
    
    @Query(value = "select s.* from stage s "
            + "where s.event_id = ?",
            nativeQuery = true)
    List<Stage> getAllStagesByEventId(long eventId);

    @Query(value = "SELECT * FROM public.stage "
            + "WHERE event_id=? "
            + "ORDER BY stage_type, stage_name, id asc",
            nativeQuery = true)
    List<Stage> getAllSortedStagesByEventId(long eventId);

    
    @Query(value = "select s.* "
            + "from stage s "
            + "where s.event_id = ? and s.stage_type = ? "
            + "ORDER BY stage_name asc",
            nativeQuery = true)
    List<Stage> findAllSessionsByTypeFromEvent(Long eventId, String type);
    
//    @Query(value = "select s.* "
//            + "from stage s "
//            + "where s.stage_name=? "
//            + "and s.event_id=?",
//            nativeQuery = true)
    Stage findBystageNameAndEventId(String name, Long eventId);
  
}
