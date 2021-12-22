/*
 * The Krechet Software
 */
package ru.thekrechetofficial.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.dto.IGeneralResult;
import ru.thekrechetofficial.dto.IGeneralResultWithSS;
import ru.thekrechetofficial.dto.ILapDtoForVariance;
import ru.thekrechetofficial.dto.ILapDtoTimesReport;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByEventName(String name);

    List<Event> findByEventDateBetween(LocalDate from, LocalDate to);

    List<Event> findByEventNameLikeIgnoreCase(String name);

    @Query(value = "select e.* "
            + "from event e ", nativeQuery = true)
    Set<Event> findAllToSet();
    
    @Query(value = "select e.* "
            + "from event e where is_deleted=false", nativeQuery = true)
    Set<Event> findAllNotDeletedToSet();

    @Query(value = "select s.event_id "
            + "from stage s "
            + "where id=?", nativeQuery = true)
    long getEventIdByStageId(long stageId);

    @Query(value = "select count(id) "
            + "from crew "
            + "where event_id=?",
            nativeQuery = true)
    int getCrewAmountByEvent(long id);

//    @Query(value = "select a.event_id "
//            + "from active a "
//            + "where id=1", nativeQuery = true)
//    long getActiveEvent();

//    @Modifying
//    @Query(value = "update active "
//            + "set event_id =? "
//            + "where id=1", nativeQuery = true)
//    void setActiveEvent(long id);
    
    @Query(value = "select e.* "
            + "from \"event\" e "
            + "where has_registration=true and is_managed=true "
            + "and is_deleted=false "
            //+ "where is_managed=true "
            + "order by event_date desc;", nativeQuery = true)
    List<Event> findAllForRegistration();

    @Query(value = "select e.* "
            + "from \"event\" e "
            + "where is_managed=true "
            + "order by event_date desc;", nativeQuery = true)
    List<Event> findAllForManage();

    @Modifying
    @Query(value = "update \"event\" "
            + "set is_managed=? "
            + "where id=?", nativeQuery = true)
    void setIsManaged(boolean b, Long eventId);

    @Modifying
    @Query(value = "update \"event\" "
            + "set has_registration=? "
            + "where id=?", nativeQuery = true)
    void setHasRegistration(boolean b, Long eventId);

    @Query(value = "select e.has_registration "
            + "from \"event\" e "
            + "where id=?", nativeQuery = true)
    boolean getHasRegistration(Long eventId);

    @Query(value = "WITH ss_sectors AS ( "
            + "	SELECT e1.ss_amount as laps_amount, "
            + "	l1.crew_id, "
            + "	c1.start_number, "
            + "	c2.category_name as category, "
            + "	p2.last_name || ' ' || p2.first_name AS fullname, "
            + "	v1.make || ' ' || v1.model AS vehiclename, "
            + "	s1.stage_name, "
            + "	l1.lap_time, "
            + "	p1.penalty_time, "
            + "	l1.\"configuration\" "
            + "	FROM lap l1 "
            + "	INNER JOIN crew c1 ON l1.crew_id = c1.id "
            + " INNER JOIN category c2 ON c1.category_id = c2.id "
            + "	INNER JOIN stage s1 ON l1.stage_id = s1.id "
            + "	INNER JOIN penalty p1 ON l1.penalty_id = p1.id "
            + "	INNER JOIN \"event\" e1 ON s1.event_id = e1.id "
            + "	INNER JOIN pilot p2 ON c1.pilot_id = p2.id "
            + "	INNER JOIN vehicle v1 ON c1.vehicle_id = v1.id "
            + "	WHERE l1.is_confirmed and e1.id = ? and s1.stage_type = 'SS' and c1.is_approved = true "
            + ") "
            + "SELECT fin.startnumber, "
            + "fin.fullname, "
            + "fin.vehiclename, "
            + "fin.category, "
            + "cast(fin.sstimes as text), "
            + "cast(fin.penaltytimes as text), "
            + "fin.totaltime "
            + "FROM(SELECT start_number as startnumber, "
            + "		fullname, "
            + "		vehiclename, "
            + "		category, sstimes, "
            + "		penaltytimes, "
            + "		totaltime "
            + "	FROM  (	SELECT 	laps_amount, "
            + "			crew_id, "
            + "			start_number, "
            + "			fullname, "
            + "			vehiclename, "
            + "			category, "
            + "			array_agg(lap_time order by stage_name) AS ssTimes, "
            + "			array_agg(penalty_time order by stage_name) AS penaltytimes, "
            + "			(SELECT SUM(lap_time + penalty_time) AS total) AS totaltime "
            + "			FROM ss_sectors  "
            + "			GROUP BY crew_id, start_number, fullname, vehiclename, category, laps_amount) "
            + "			AS res "
            + "	 ) "
            + "	AS fin "
            + "ORDER BY totaltime", nativeQuery = true)
    List<IGeneralResultWithSS> findCategorySortedResultWithSS(Long eventId);

    @Query(value = "WITH ss_sectors AS ( "
            + "			SELECT "
            + "	            l1.crew_id, "
            + "	            c1.start_number, "
            + "	            c2.category_name as category, "
            + "	            p2.last_name || ' ' || p2.first_name AS fullname, "
            + "	            s1.stage_name, "
            + "	            l1.lap_time, "
            + "	            p1.penalty_time, "
            + "	            p1.description, "
            + "	            l1.\"configuration\" "
            + "            FROM lap l1 "
            + "            INNER JOIN crew c1 ON l1.crew_id = c1.id "
            + "            INNER JOIN category c2 ON c1.category_id = c2.id "
            + "            INNER JOIN stage s1 ON l1.stage_id = s1.id "
            + "            INNER JOIN penalty p1 ON l1.penalty_id = p1.id "
            + "            INNER JOIN \"event\" e1 ON s1.event_id = e1.id "
            + "            INNER JOIN pilot p2 ON c1.pilot_id = p2.id "
            + "            WHERE l1.is_confirmed and e1.id = ? and s1.stage_type = 'SS' and c1.is_approved = true and c2.category_name = ? "
            + ") "
            + "            SELECT fin.startnumber, "
            + "            fin.fullname, "
            + "            fin.category, "
            + "            cast(fin.sstimes as text), "
            + "            cast(fin.penaltytimes as text), "
            + "            cast(fin.penaltydescriptions as text), "
            + "            fin.totaltime "
            + "            FROM(SELECT start_number as startnumber, "
            + "            fullname, "
            + "            category, sstimes, "
            + "            penaltytimes, "
            + "            penaltydescriptions, "
            + "            totaltime "
            + "            FROM  (	SELECT "
            + "            crew_id, "
            + "            start_number, "
            + "            fullname, "
            + "            category, "
            + "            array_agg(lap_time order by stage_name) AS ssTimes, "
            + "            array_agg(penalty_time order by stage_name) AS penaltytimes, "
            + "            array_agg(description order by stage_name) AS penaltydescriptions, "
            + "            (SELECT SUM(lap_time + penalty_time) AS total) AS totaltime "
            + "            FROM ss_sectors  "
            + "            GROUP BY crew_id, start_number, fullname, category) "
            + "            AS res "
            + "            ) "
            + "            AS fin "
            + "ORDER BY totaltime", nativeQuery = true)
    List<IGeneralResultWithSS> findSortedResultForCategory(Long eventId, String category);

    @Query(value = "select id, cast(array_agg(lap_time + penalty_time) AS text) AS lapTime, cast(array_agg(\"configuration\") as text) AS config "
            + "from ("
            + "select c.id, l.lap_time, p.penalty_time, l.\"configuration\" "
            + "from lap l "
            + "inner join crew c on l.crew_id = c.id "
            + "inner join penalty p on l.penalty_id = p.id "
            + "inner join stage s on l.stage_id = s.id "
            + "inner join \"event\" e on s.event_id = e.id "
            + "where l.is_confirmed = true and e.id = ? and s.stage_type = 'SS' "
            + "order by c.id, s.stage_name"
            + ") as foo "
            + "group by id ", nativeQuery = true)
    List<ILapDtoForVariance> findAllSSLapsForVarianceByEvent(Long eventId);

    @Query(value = "select c.start_number as startnumber, "
            + "pil.last_name || ' ' || pil.first_name AS fullname, "
            + "v.make || ' ' || v.model AS vehicle, "
            + "v.hp, "
            + "min(l.lap_time + p.penalty_time) as finalTime "
            + "from lap l "
            + "inner join penalty p on l.penalty_id = p.id "
            + "inner join stage s on l.stage_id = s.id "
            + "inner join crew c on l.crew_id = c.id "
            + "inner join pilot pil on c.pilot_id = pil.id "
            + "inner join vehicle v on c.vehicle_id = v.id "
            + "inner join \"event\" e on s.event_id = e.id "
            + "where e.id = ? and s.stage_type = ? and l.\"configuration\" = ? "
            + "group by c.start_number, pil.last_name, pil.first_name, v.make, v.model, v.hp "
            + "order by finalTime", nativeQuery = true)
    List<ILapDtoTimesReport> findBestLapsForConfigReportByStageType(Long event, String stageType, int configuration);

    @Query(value = "select c.start_number as startnumber, "
            + "pil.last_name || ' ' || pil.first_name AS fullname, "
            + "v.make || ' ' || v.model AS vehicle, "
            + "v.hp, "
            + "sum(l.lap_time + p.penalty_time) as finalTime "
            + "from lap l "
            + "inner join penalty p on l.penalty_id = p.id "
            + "inner join stage s on l.stage_id = s.id "
            + "inner join crew c on l.crew_id = c.id "
            + "inner join pilot pil on c.pilot_id = pil.id "
            + "inner join vehicle v on c.vehicle_id = v.id "
            + "inner join \"event\" e on s.event_id = e.id "
            + "where e.id = ? and s.stage_type = ? "
            + "group by c.start_number, pil.last_name, pil.first_name, v.make, v.model, v.hp "
            + "order by finalTime", nativeQuery = true)
    List<ILapDtoTimesReport> findLapsForAbsoluteReportByStageType(Long event, String stageType);

    @Modifying
    @Query(value = "update \"event\" "
            + "set ss_amount=? "
            + "where id=?", nativeQuery = true)
    void updateSSAmount(int i, long eventId);

}
