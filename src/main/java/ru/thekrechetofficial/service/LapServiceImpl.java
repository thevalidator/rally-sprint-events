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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.thekrechetofficial.dto.PenaltyDto;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Penalty;
import ru.thekrechetofficial.entity.Stage;
import ru.thekrechetofficial.entity.StageType;
import ru.thekrechetofficial.repository.LapRepository;
import ru.thekrechetofficial.repository.PenaltyRepository;
import ru.thekrechetofficial.repository.StageRepository;
import ru.thekrechetofficial.util.parser.LapFileParser;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class LapServiceImpl implements LapService {

    private final LapRepository lapRepository;
    private final StageRepository stageRepository;
    private final PenaltyRepository penaltyRepository;
    private final CrewService crewService;
    private final PenaltyService penaltyService;

    @Autowired
    public LapServiceImpl(LapRepository lapRepository,
            StageRepository stageRepository,
            PenaltyRepository penaltyRepository,
            CrewService crewService,
            PenaltyService penaltyService) {

        this.lapRepository = lapRepository;
        this.stageRepository = stageRepository;
        this.penaltyRepository = penaltyRepository;
        this.crewService = crewService;
        this.penaltyService = penaltyService;
    }

    @Override
    public List<Lap> getPracticeLapsByStage(Long stageId, String userName) {
        List<Lap> laps = lapRepository.findAllStagedLapsByUserName(stageId, userName);
        return laps;
    }

    @Override
    public void save(Lap lap) {
        lapRepository.saveAndFlush(lap);
    }

    @Override
    public List<Lap> getAllSortedByStageId(Long stageId) {

        List<Lap> laps = lapRepository.getAllSortedByStageId(stageId);
        for (Lap l : laps) {
            l.getCrew().getPilot().getAge();
            l.getCrew().getVehicle().getHp();
            l.getCrew().getCategory();
            l.getPenalty().getPenaltyTime();
        }

        return laps;
    }

    @Override
    public Lap getLapById(Long lapId) {
        Lap lap = lapRepository.findById(lapId).orElseThrow();
        lap.getPenalty().getPenaltyTime();

        return lap;
    }

    @Override
    public int addLapsToStage(Long stageId, Integer config, MultipartFile file) {

        Stage stage = stageRepository.findById(stageId).orElseThrow();

        if (stage.getType().equals(StageType.SS)) {
            if (stage.getEvent().getHasRegistration()) {
                return -1;
            }
            if (lapRepository.countTotalStageLaps(stageId) > 0) {
                return 0;
            }
            
            parseAndSaveSSLaps(stage, config, file, LapFileParser.MY_TEST);

        }

//        try ( InputStream inputStream = file.getInputStream();  BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
//
//            if (stage.getType().equals(StageType.SS)) {
//                
//
//            } else {
//                Pattern crewPattern = Pattern.compile(LapFileParser.MY_LAPS_PRA_CREW, Pattern.UNICODE_CHARACTER_CLASS);
//                Pattern lapPattern = Pattern.compile(LapFileParser.MY_LAPS_PRA_LAP, Pattern.UNICODE_CHARACTER_CLASS);
//
//                Matcher matcher;
//                String line;
//                int crewStartNumber = 0;
//                Crew crew = null;
//                int lapNumber = 0;
//
//                while ((line = reader.readLine()) != null) {
//                    matcher = crewPattern.matcher(line);
//                    if (matcher.find()) {
//                        crewStartNumber = Integer.parseInt(matcher.group(1));
//                        crew = crewService.getByStartNumber(eventId, crewStartNumber);
//                        lapNumber = crewService.getCrewLapsAmountByStage(stage.getId(), crew.getId());
//
//                    } else if (crewStartNumber != 0 /*&& crew != null*/) {
//                        matcher = lapPattern.matcher(line);
//
//                        if (matcher.find()) {
//                            String lapTimeData = matcher.group("laptime");
//                            long lapTime = convertLapTime(lapTimeData);
//                            lapNumber++;
//                            Lap lap = new Lap();
//                            lap.setConfiguration(config);
//                            lap.setCrew(crew);
//                            lap.setLapNumber(lapNumber);
//                            lap.setLapTime(lapTime);
//                            lap.setPenalty(cleanPenalty);
//                            lap.setStage(stage);
//                            lapRepository.save(lap);
//                            //System.out.printf("crew: %d lapNum: %d, lapTime: %s \n", lap.getCrew().getStartNumber(), lap.getLapNumber(), lap.getFormattedLapTime() );
//
//                        }
//
//                    }
//
//                }
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return 1;

    }

    private void parseAndSaveSSLaps(Stage s, int config, MultipartFile f, String pattern) {
        
        Penalty cleanPenalty = penaltyRepository.getById(1L);
        long maxTime = 0;

        try ( InputStream inputStream = f.getInputStream();  
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            Pattern p = Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher;
            String line;

            while ((line = r.readLine()) != null) {
                matcher = p.matcher(line);
                if (matcher.find()) {

                    int crewStartNumber = Integer.parseInt(matcher.group("crewStartNum"));
                    Crew crew = crewService.getByStartNumber(s.getEvent().getId(), crewStartNumber);
                    String lapTimeData = matcher.group("laptime");

                    long lapTime = convertLapTime(lapTimeData);
                    if (maxTime < lapTime) {
                        maxTime = lapTime;
                    }

                    Lap lap = createLap(crew, s, config, lapTime, cleanPenalty);
                    lapRepository.save(lap);
                }

            }

            List<Crew> dnsCrews = crewService.getAllDidNotStarted(s.getEvent().getId(), s.getId());
            if (!dnsCrews.isEmpty()) {
                for (Crew c : dnsCrews) {
                    Lap lap = createLapForDNQ(c, s, config, maxTime);
                    penaltyRepository.save(lap.getPenalty());
                    lapRepository.save(lap);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Lap createLapForDNQ(Crew c, Stage stage, int config, long maxTime) {

        Penalty penalty = new Penalty();
        penalty.setIsActive(false);
        penalty.setDescription("DNQ");
        penalty.setPenaltyTime(maxTime + 20_000L);

        Lap lap = new Lap();
        lap.setStage(stage);
        lap.setCrew(c);
        lap.setLapNumber(1);
        lap.setConfiguration(config);
        lap.setLapTime(0);
        lap.setPenalty(penalty);
        lap.setIsConfirmed(false);

        return lap;
    }

    private Lap createLap(Crew c, Stage s, int config, long lapTime, Penalty p) {

        Lap lap = new Lap();
        lap.setStage(s);
        lap.setCrew(c);
        lap.setLapNumber(1);
        lap.setConfiguration(config);
        lap.setLapTime(lapTime);
        lap.setPenalty(p);

        return lap;
    }

    private long convertLapTime(String timeData) {
        int minutes = 0;
        int seconds = 0;
        int millsec = 0;

        if (timeData.length() > 6 && timeData.length() < 10) {
            minutes = Integer.parseInt(timeData.substring(0, timeData.indexOf(":")));
            seconds = Integer.parseInt(timeData.substring(timeData.indexOf(":") + 1, timeData.indexOf(".")));
            millsec = Integer.parseInt(timeData.substring(timeData.indexOf(".") + 1));
        } else {
            seconds = Integer.parseInt(timeData.substring(0, timeData.indexOf(".")));
            millsec = Integer.parseInt(timeData.substring(timeData.indexOf(".") + 1));
        }

        long lapTime = Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(millsec).toMillis();

        return lapTime;
    }

    @Override
    public int getStageLapAmountByCrewId(long stageId, long crewId) {
        return lapRepository.getStageLapAmountByCrew(stageId, crewId);
    }

    @Override
    public List<Lap> getAllByEventIdSortByCrewThenLapNum(long eventId) {
        return lapRepository.getAllSortedByEventId(eventId);
    }

    @Override
    public void deleteAllByEvent(long eventId) {
        List<Lap> laps = lapRepository.findAllByEventId(eventId);
        if (!laps.isEmpty()) {
            for (Lap l : laps) {
                lapRepository.delete(l);
            }
        }
    }

    @Override
    public List<Lap> getAllByStageId(long stageId) {
        List<Lap> laps = lapRepository.findAllByStageId(stageId);
        laps.size();
        return laps;
    }

    @Override
    public void deleteById(Long lapId) {
        lapRepository.deleteById(lapId);
    }

    @Override
    public int parseThenSaveLaps(MultipartFile file, Long stageId) {

        List<Lap> laps = new ArrayList<>();
        Stage stage = stageRepository.findById(stageId).orElseThrow();//stageService.getStageById(stageId);
        List<Crew> crews = crewService.getAllCrewByEventId(stage.getEvent().getId());

        Map<Integer, Crew> crewMap = new HashMap<>();
        Map<Long, Integer> lapNumbers = new HashMap<>();

        for (Crew c : crews) {
            int lapsNumber = this.getStageLapAmountByCrewId(stageId, c.getId());
            lapNumbers.put(c.getId(), lapsNumber);
            crewMap.put(c.getStartNumber(), c);
        }
        try ( InputStream inputStream = file.getInputStream();  BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            //LapFileParser.parseLapsMyTest(reader, laps, stage, crewMap, lapNumbers);
            LapFileParser.parseLapsRaceRussia(reader, laps, stage, crewMap, lapNumbers);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Lap l : laps) {
            lapRepository.saveAndFlush(l);
        }
        return laps.size();
        //return 5;
    }

    @Override
    public long addPenaltyToLap(Long lapId, PenaltyDto penaltyDto) {
        Lap lap = lapRepository.findById(lapId).orElseThrow();

        Penalty penalty = new Penalty();
        penalty.setPenaltyTime(penaltyDto.getPenaltyTime() * 1_000);
        penalty.setDescription(penaltyDto.getDescription());

        lap.setPenalty(penalty);

        lapRepository.saveAndFlush(lap);

        return lap.getStage().getId();
    }

    @Override
    public List<Lap> getAllCrewBestLapByStageId(long eventId, long stageId, long eventId2, long stageId2) {
        List<Lap> laps = lapRepository.findAllCrewBestLapByStageId(eventId, stageId, eventId2, stageId2);
        laps.size();
        return laps;
    }

    @Override
    public List<Lap> getAllByCrewIdAndStageId(long crewId, long stageId) {
        List<Lap> laps = lapRepository.findAllByCrewIdAndStageId(crewId, stageId);
        laps.size();
        return laps;
    }

}
