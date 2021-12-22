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
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.thekrechetofficial.entity.Crew;

/**
 *
 * @author theValidator <the.validator@yandex.ru>
 */
@Repository
public interface CrewRepository extends JpaRepository<Crew, Long> {

    @Query(value = "select c.* "
            + "from crew c "
            + "where event_id=? "
            + "order by c.start_number", nativeQuery = true)
    List<Crew> findAllByEventId(long eventId);

    @Query(value = "select c.* "
            + "from crew c "
            + "where event_id=? and is_active=true "
            + "order by c.start_number", nativeQuery = true)
    List<Crew> findAllActiveByEventId(long eventId);

    @Query(value = "select c.start_number "
            + "from crew c "
            + "where event_id=? and c.is_active ", nativeQuery = true)
    Set<Integer> findAllEventStartNumbers(long eventId);

    @Query(value = "select exists "
            + "(select 1 "
            + "from crew c "
            + "where pilot_id=? and event_id=?) AS \"exists\"", nativeQuery = true)
    boolean isExistsByPilotId(long pilotId, long eventId);

    @Query(value = "select exists "
            + "(select 1 "
            + "from crew c "
            + "where pilot_id=? and event_id=? and is_active=true) AS \"exists\"", nativeQuery = true)
    boolean isExistsInActiveByPilotId(long pilotId, Long eventId);

    @Query(value = "select c.id "
            + "from crew c "
            + "where pilot_id=? and event_id=? and is_active=true", nativeQuery = true)
    Long getCrewIdIfExistsInActiveByPilot(long pilotId, long eventId);

    @Modifying
    @Query(value = "UPDATE crew "
            + "SET is_active=? WHERE id=? and event_id=?", nativeQuery = true)
    void setIsActive(boolean b, Long crewId, Long eventId);

    @Modifying
    @Query(value = "UPDATE crew "
            + "SET is_paid=? WHERE id=?", nativeQuery = true)
    void setIsPaid(boolean b, Long crewId);

    @Modifying
    @Query(value = "UPDATE crew "
            + "SET is_approved=? WHERE id=?", nativeQuery = true)
    void setIsApproved(boolean b, Long crewId);

    @Query(value = "select c.* "
            + "from crew c "
            + "where pilot_id=? and event_id=?", nativeQuery = true)
    Optional<Crew> findByPilotId(Long crewId, Long eventId);

    @Query(value = "select c.* "
            + "from crew c "
            + "where event_id=? and start_number=?", nativeQuery = true)
    Optional<Crew> findByStartNumberAndEventId(long eventId, int crewStartNumber);

    @Query(value = "select c.* "
            + "from (select * from crew where event_id = ?) as c "
            + "where c.id "
            + "not in (select c1.id "
            + "from lap l "
            + "inner join crew c1 on l.crew_id = c1.id "
            + "where l.stage_id = ?)", nativeQuery = true)
            //+ "where l.stage_id = ? and c1.is_approved = true)", nativeQuery = true)
    List<Crew> findAllDidNotStarted(long eventId, long stageId);
    
    @Query(value = "select count(l.id) "
            + "from lap l "
            + "where stage_id = ? and crew_id = ?", nativeQuery = true)
    int getCrewLapsAmountByStage(long stageId, long crewId);

//    @Query(value = "select  r.start_number as startNumber, r.fullname, r.vehiclename, r.category, r.totaltime "
//            + "from ( "
//            + "SELECT c.event_id, c.start_number, p2.last_name || ' ' || p2.first_name AS fullname, "
//            + "v.make || ' ' || v.model as vehiclename, "
//            + "c.c_category as category, "
//            + "sum(l.lap_time + p.penalty_time) as totaltime, count(*) as laps "
//            + "FROM lap l "
//            + "inner join stage s on l.stage_id = s.id "
//            + "inner join penalty p on l.penalty_id  = p.id "
//            + "inner join crew c on l.crew_id = c.id "
//            + "inner join pilot p2 on c.pilot_id = p2.id "
//            + "inner join vehicle v on c.vehicle_id = v.id "
//            + "where s.stage_type = 'SS' "
//            + "and l.is_confirmed = true "
//            + "and l.lap_time > 0 "
//            + "and c.is_approved = true "
//            + "and c.is_active = true "
//            + "group by c.id, p2.id, v.id "
//            + "order by c.c_category, totaltime ) as r "
//            + "inner join \"event\" e on r.event_id = e.id "
//            + "where r.event_id = ? and  r.laps = e.ss_amount ", nativeQuery = true)
//    List<TotalCrewCommonResult> findAllCompletedAndSortedByTotalTime(long eventId);
}
