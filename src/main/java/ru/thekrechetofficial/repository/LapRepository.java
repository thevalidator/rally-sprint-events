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
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.thekrechetofficial.entity.Lap;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@Repository
public interface LapRepository extends JpaRepository<Lap, Long> {

    @Query(value = "select count(id) from lap where stage_id=? and crew_id=?",
            nativeQuery = true)
    int getStageLapAmountByCrew(long stageId, long crewId);

    //probably need to change on findAllLapsByStageId or leave without sorting just for the delete operation
    @Query(value = "select l.* "
            + "from lap l "
            + "inner join crew c on c.id = l.crew_id "
            + "inner join stage s on s.id = l.stage_id "
            + "where s.id=? "
            + "order by l.lap_number asc",
            nativeQuery = true)
    List<Lap> findAllByStageId(long stageId);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join stage s on s.id = l.stage_id "
            + "inner join crew c on c.id = l.crew_id "
            + "inner join event e on e.id = c.event_id "
            + "where stage_id = ? "
            + "group by l.id, c.id "
            + "order by c.id", nativeQuery = true)
    List<Lap> findAllLapsByStageId(long stageId);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join stage s on s.id = l.stage_id "
            + "inner join event e on e.id = s.event_id "
            + "where e.id = ?", nativeQuery = true)
    List<Lap> findAllByEventId(long id);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join stage s on s.id = l.stage_id "
            + "inner join event e on e.id = s.event_id "
            + "where e.id = ?", nativeQuery = true)
    Set<Lap> findAllByEventIdSet(long id);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join stage s on s.id = l.stage_id "
            + "inner join event e on e.id = s.event_id "
            + "where e.event_name = ?", nativeQuery = true)
    List<Lap> findAllByEventName(String title);

    @Query(value = "select l.*"
            + "from lap l "
            + "inner join crew c on c.id = l.crew_id "
            + "inner join stage s on s.id = l.stage_id "
            + "where s.event_id=? "
            + "order by l.stage_id, l.crew_id, l.lap_number asc",
            nativeQuery = true)
    List<Lap> getAllSortedByEventId(long eventId);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join stage s on s.id = l.stage_id "
            + "inner join event e on e.id = s.event_id "
            + "where l.crew_id ||''|| l.lap_time + l.penalty_time "
            + "in (select l2.crew_id ||''|| min(l2.lap_time + l2.penalty_time) "
            + "from lap l2 "
            + "inner join stage s on s.id = l2.stage_id "
            + "inner join event e on e.id = s.event_id "
            + "where e.id = ? and s.id = ? "
            + "group by l2.crew_id "
            + ") and e.id = ? and s.id = ? "
            + "order by (l.lap_time + l.penalty_time)", nativeQuery = true)
    List<Lap> findAllCrewBestLapByStageId(long eventId, long stageId,
            long eventId2, long stageId2);

    @Query(value = "select l.* "
            + "from lap l "
            + "where l.crew_id = ? "
            + "and l.stage_id = ? "
            + "order by l.lap_number ",
            nativeQuery = true)
    List<Lap> findAllByCrewIdAndStageId(long crewId, long stageId);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join penalty p on l.penalty_id = p.id "
            + "where l.stage_id = ? "
            + "group by l.id "
            + "order by sum(l.lap_time + p.penalty_time)",
            nativeQuery = true)
    List<Lap> getAllSortedByStageId(Long stageId);

    @Modifying
    @Query(value = "UPDATE lap "
            + "SET is_confirmed=? "
            + "WHERE id=?",
            nativeQuery = true)
    void setIsConfirmed(boolean b, long id);

    @Query(value = "select count(l.id) "
            + "from lap l "
            + "inner join stage s on l.stage_id = s.id "
            + "where l.stage_id = ?", nativeQuery = true)
    long countTotalStageLaps(long stageId);

    @Query(value = "select l.* "
            + "from lap l "
            + "inner join crew c on l.crew_id = c.id "
            + "inner join pilot p ON c.pilot_id = p.id "
            + "inner join \"user\" u on p.user_id = u.id "
            + "where l.is_confirmed = true and l.stage_id = ? and u.username = ? ",
            nativeQuery = true)
    List<Lap> findAllStagedLapsByUserName(Long stageId, String userName);

}
