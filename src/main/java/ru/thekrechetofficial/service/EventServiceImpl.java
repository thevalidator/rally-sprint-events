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

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import ru.thekrechetofficial.dto.EventDto;
import ru.thekrechetofficial.dto.GeneralResultWithSSDto;
import ru.thekrechetofficial.dto.IGeneralResultWithSS;
import ru.thekrechetofficial.dto.ILapDtoForVariance;
import ru.thekrechetofficial.dto.LapDtoForVariance;
import ru.thekrechetofficial.dto.VarianceDto;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Stage;
import ru.thekrechetofficial.entity.StageType;
import ru.thekrechetofficial.repository.EventRepository;
import ru.thekrechetofficial.repository.ServiceRepository;
import ru.thekrechetofficial.repository.StageRepository;
import ru.thekrechetofficial.repository.TrackRepository;
import ru.thekrechetofficial.util.pdf.PdfCreator;
import ru.thekrechetofficial.dto.ILapDtoTimesReport;
import ru.thekrechetofficial.dto.LapDtoTimesReport;
import ru.thekrechetofficial.entity.CategorySet;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final StageRepository stageRepository;
    private final LapService lapService;
    private final StageService stageService;
    private final CrewService crewService;
    private final TrackRepository trackRepository;
    private final CategorySetService catSetService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
            StageRepository stageRepository,
            LapService lapService,
            StageService stageService,
            CrewService crewService,
            TrackRepository trackRepository,
            CategorySetService catSetService) {
        
        this.eventRepository = eventRepository;
        this.stageRepository = stageRepository;
        this.lapService = lapService;
        this.stageService = stageService;
        this.crewService = crewService;
        this.trackRepository = trackRepository;
        this.catSetService = catSetService;
    }

    @Override
    public void updateEvent(Event e, EventDto dto) {
        e.setEventDate(parseDate(dto.getDate()));
        e.setRulesLink(dto.getRulesLink());
        e.setTrack(trackRepository.getById(dto.getTrackId()));
        e.setMaxCrew(dto.getMaxCrew());
        e.setConfigAmount(dto.getConfigAmount());
        e.setCategorySet(catSetService.getCatSet(dto.getCatSetId()));
        eventRepository.saveAndFlush(e);
    }
    
    
    
    @Override
    public List<LapDtoTimesReport> getBestLapsForConfigReportByStageType(Long event, String stageType, int configuration) {
        List<ILapDtoTimesReport> list = eventRepository.findBestLapsForConfigReportByStageType(event, stageType, configuration);
        List<LapDtoTimesReport> result = makeListOfDto(list);
        return result;
    }

    @Override
    public List<LapDtoTimesReport> getResultListForAbsoluteReportByStageType(Long event, String stageType) {
        List<ILapDtoTimesReport> list = eventRepository.findLapsForAbsoluteReportByStageType(event, stageType);
        List<LapDtoTimesReport> result = makeListOfDto(list);
        return result;
    }

    private List<LapDtoTimesReport> makeListOfDto(List<ILapDtoTimesReport> list) {
        List<LapDtoTimesReport> result = new ArrayList<>();
        for (ILapDtoTimesReport l : list) {
            LapDtoTimesReport dto = new LapDtoTimesReport();
            dto.setStartNumber(l.getStartNumber());
            dto.setFullName(l.getFullName());
            dto.setVehicle(l.getVehicle());
            dto.setHp(l.getHp());
            dto.setFinalTime(getFormattedTime(l.getFinalTime()));
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<VarianceDto> getVarianceCoefficientsForEvent(Long eventId) {
        List<VarianceDto> result = new ArrayList<>();
        List<ILapDtoForVariance> allLapsData = eventRepository.findAllSSLapsForVarianceByEvent(eventId);
        for (ILapDtoForVariance d : allLapsData) {
            HashSet<Integer> configs = new HashSet<>();
            List<LapDtoForVariance> laps = new ArrayList<>();
            String[] lap = d.getLapTime().replaceAll("[{}]", "").split(",");
            String[] config = d.getConfig().replaceAll("[{}]", "").split(",");
            
            for (int i = 0; i < lap.length; i++) {
                LapDtoForVariance l = new LapDtoForVariance();
                Long time = Long.parseLong(lap[i]);
                l.setLapTime(time);
                Integer cfg = Integer.parseInt(config[i]);
                l.setConfiguration(cfg);
                configs.add(cfg);
                laps.add(l);
            }
            VarianceDto vDto = getCoefficientOfVariation(d.getId(), laps, configs.size());
            result.add(vDto);
        }
        result.sort(Comparator.comparing(VarianceDto::getVarianceCoefficient));

        return result;
    }

    private VarianceDto getCoefficientOfVariation(Long crewId, List<LapDtoForVariance> laps, int confAmount) {
        long[] sum = new long[confAmount];
        int[] number = new int[confAmount];
        double[] average = new double[confAmount];

        for (LapDtoForVariance l : laps) {
            int index = l.getConfiguration() - 1;
            sum[index] += l.getLapTime();
            number[index] += 1;
        }
        for (int i = 0; i < confAmount; i++) {
            average[i] = sum[i] / number[i];
        }

        double lineDeviance = 0;
        for (LapDtoForVariance l : laps) {
            int index = l.getConfiguration() - 1;
            long time = l.getLapTime();
            lineDeviance += Math.pow((time - average[index]), 2);
        }
        double variance = lineDeviance / laps.size();
        double averageTime = 0;
        for (int i = 0; i < confAmount; i++) {
            averageTime += average[i];
        }
        averageTime /= confAmount;
        double standartDeviance = Math.sqrt(variance);
        double coefficientOfVariation = standartDeviance / averageTime * 100;

        VarianceDto dto = new VarianceDto();
        dto.setCrew(crewService.getById(crewId));
        dto.setVarianceCoefficient(coefficientOfVariation);

        return dto;
    }

    @Override
    public List<GeneralResultWithSSDto> getSortedResultForCategory(Long eventId, String category) {
        List<GeneralResultWithSSDto> resultList = new ArrayList<>();
        List<IGeneralResultWithSS> results = eventRepository.findSortedResultForCategory(eventId, category);
        int ssAmount = eventRepository.getById(eventId).getSpecialSectorAmount();
        for (IGeneralResultWithSS r: results) {
            GeneralResultWithSSDto dto = new GeneralResultWithSSDto();
            dto.setStartNumber(r.getStartNumber());
            dto.setTotalTime(getFormattedTime(r.getTotalTime()));
            dto.setFullName(r.getFullName());
            String[] ssTimes = r.getSSTimes().replaceAll("[{}]", "").split(",");
            String[] penaltyTimes = r.getPenaltyTimes().replaceAll("[{}]", "").split(",");
            String[] penaltyDescriptions = r.getPenaltyDescriptions().replaceAll("[{}]", "").split(",");
            if (penaltyDescriptions.length < ssAmount) {
                String[] newPenaltyDescriptions = new String[ssAmount];
                for (int i = 0; i < ssAmount; i++) {
                    if (i < penaltyDescriptions.length) {
                        newPenaltyDescriptions[i] = penaltyDescriptions[i];
                    } else {
                        newPenaltyDescriptions[i] = "";
                    }
                }
                dto.setPenaltyDescriptions(newPenaltyDescriptions);
            } else {
                dto.setPenaltyDescriptions(penaltyDescriptions);
            }

            dto.setSsTimes(putFormattedTimesInArray(ssTimes, ssAmount));
            dto.setPenaltyTimes(putFormattedTimesInArray(penaltyTimes, ssAmount));
            resultList.add(dto);
        }
        return resultList;
    }

    @Override
    public List<GeneralResultWithSSDto> getCategorySortedResultWithSS(Long eventId) {
        List<IGeneralResultWithSS> results = eventRepository.findCategorySortedResultWithSS(eventId);
        List<GeneralResultWithSSDto> resultList = new ArrayList<>();
        int ssAmount = eventRepository.getById(eventId).getSpecialSectorAmount();
        for (IGeneralResultWithSS r : results) {
            GeneralResultWithSSDto dto = new GeneralResultWithSSDto();
            dto.setStartNumber(r.getStartNumber());
            dto.setFullName(r.getFullName());
            dto.setVehicleName(r.getVehicleName());
            dto.setSsTimes(r.getSSTimes().replaceAll("[{}]", "").split(","));
            dto.setPenaltyTimes(r.getPenaltyTimes().replaceAll("[{}]", "").split(","));
            dto.setTotalSsTimes(putFormattedTotalSsTimesInArray(dto.getSsTimes(), dto.getPenaltyTimes(), ssAmount));
            dto.setCategory(r.getCategory());
            dto.setTotalTime(getFormattedTime(r.getTotalTime()));

            resultList.add(dto);
        }
        return resultList;
    }

    private String[] putFormattedTimesInArray(String[] times, int arraySize) {
        String[] formattedTimes = new String[arraySize];
        for (int i = 0; i < times.length; i++) {
            long time = Long.parseLong(times[i]);
            formattedTimes[i] = getFormattedTime(time);
        }
        if (times.length < arraySize) {
            for (int i = times.length; i < arraySize; i++) {
                formattedTimes[i] = "---";
            }
        }

        return formattedTimes;
    }

    private String[] putFormattedTotalSsTimesInArray(String[] ssTimes, String[] penaltyTimes, int arraySize) {
        String[] totalSsTimes = new String[arraySize];
        for (int i = 0; i < ssTimes.length; i++) {
            long time = Long.parseLong(ssTimes[i]) + Long.parseLong(penaltyTimes[i]);
            totalSsTimes[i] = getFormattedTime(time);
        }
        if (ssTimes.length < arraySize) {
            for (int i = ssTimes.length; i < arraySize; i++) {
                totalSsTimes[i] = "---";
            }
        }

        return totalSsTimes;
    }

    private String getFormattedTime(Long time) {
        Duration duration = Duration.ofMillis(time);
        String formattedTotalTime = String.format("%02d:%02d.%03d",
                duration.toMinutesPart(),
                duration.toSecondsPart(),
                duration.toMillisPart());

        return formattedTotalTime;
    }

    @Override
    public void setHasRegistration(boolean b, Long eventId) {
        eventRepository.setHasRegistration(b, eventId);
    }

    @Override
    public void setIsManaged(boolean b, Long eventId) {
        eventRepository.setIsManaged(b, eventId);
    }

    @Override
    public List<Event> getAllEventsForManage() {
        List<Event> events = eventRepository.findAllForManage();
        for (Event e : events) {
            e.getTrack().getTrackName();
        }
        return events;
    }

    @Override
    public List<Event> getAllEventsForRegistration() {
        List<Event> events = eventRepository.findAllForRegistration();
        for (Event e : events) {
            e.getTrack().getTrackName();
        }
        return events;
    }

    @Override
    public Event getEventById(long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        return event;
    }

    @Override
    public Event getFullEventById(long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        initEvent(event);
        return event;
    }

    @Override
    public Event getEventWithTrackById(long id) {
        Event event = eventRepository.findById(id).orElseThrow();
        event.getTrack().getTrackName();
        return event;
    }

    @Override
    public void putEventDataToModel(ModelMap model, long id) {
        if (id != 0) {
            Event event = getFullEventById(id);
            event.getCrews().size();
            List<Stage> stages = event.getStages();
            List<Integer> stageLapAmountList = null;
            List<Integer> stageCrewAmountList = null;
            List<Category> categories = catSetService.getCatSetByEventId(id).getCategories();
            
            if (!stages.isEmpty()) {
                stageLapAmountList = new ArrayList<>();
                stageCrewAmountList = new ArrayList<>();
                for (Stage s : stages) {
                    long stageId = s.getId();
                    stageLapAmountList.add(stageRepository.getLapsNumberByStageId(stageId));
                    stageCrewAmountList.add(stageRepository.getCrewsNumberByStageId(stageId));
                }
            }
            List<Lap> laps = lapService.getAllByEventIdSortByCrewThenLapNum(event.getId()); // ???????

            model.put("event", event);
            model.put("catogories", categories);
            model.put("stageCrews", stageCrewAmountList);
            model.put("stageLaps", stageLapAmountList);
            model.put("laps", laps);
        }

    }

    @Override
    public String createEvent(EventDto eventDto) {
        Event event = new Event();
        event.setEventName(eventDto.getName());
        event.setEventDate(parseDate(eventDto.getDate()));
        event.setRulesLink(eventDto.getRulesLink());
        event.setSpecialSectorAmount(eventDto.getSpecialSectorAmount());
        event.setTrack(trackRepository.getById(eventDto.getTrackId()));
        event.setMaxCrew(eventDto.getMaxCrew());
        event.setConfigAmount(eventDto.getConfigAmount());
        event.setCategorySet(catSetService.getCatSet(eventDto.getCatSetId()));
        event.setHasRegistration(false);
        event.setIsManaged(true);
        event.setIsFinished(false);
        event.setIsDeleted(false);
        eventRepository.save(event);
        if (!eventRepository.findById(event.getId()).isPresent()) {
            return "Ошибка при создании мероприятия!";
        }
        for (int i = 1; i <= eventDto.getSpecialSectorAmount(); i++) {
            Stage stage = new Stage();
            stage.setEvent(event);
            stage.setType(StageType.SS);
            stage.setStageName("СУ-" + i);
            stageRepository.saveAndFlush(stage);
            event.getStages().add(stage);  // not needed ???
        }

        return "Мероприятие успешно создано!";
    }

    @Override
    public void delete(long id) {
        lapService.deleteAllByEvent(id);
        stageService.deleteAllByEvent(id);
        crewService.deleteAllByEvent(id);
        eventRepository.deleteById(id);
    }

//    @Override
//    public void generateStageAbsTable(Long stageId) {
//        long eventId = eventRepository.getEventIdByStageId(stageId);
//        Stage stage = stageService.getStageById(stageId);
//        //Stage stage = stageRepository.getById(stageId);
//        List<Lap> laps = lapService
//                .getAllCrewBestLapByStageId(eventId, stageId, eventId, stageId);
//
//        PdfCreator.createStageAbsoluteTable(laps, stage.getStageName());
//    }
//    @Override
//    public void generateStagePilTable(Long stageId, boolean withPenalty) {
//        List<Crew> crews = crewService.getAllCrewByStageId(stageId);
//        Stage stage = stageService.getStageById(stageId);
//        //Stage stage = stageRepository.getById(stageId);
//        HashMap<String, List<Lap>> lapMap = new HashMap<>();
//        for (Crew c : crews) {
//            List<Lap> laps = lapService.getAllByCrewIdAndStageId(c.getId(), stageId);
//            String pilotData = String.format("[%d] %s %s - %s %s (%s)", c.getStartNumber(),
//                    c.getPilot().getLastName(), c.getPilot().getFirstName(),
//                    c.getVehicle().getMake(), c.getVehicle().getModel(),
//                    c.getCategory().getOption());
//            lapMap.put(pilotData, laps);
//        }
//        PdfCreator.createStagePilotsTable(lapMap, stage.getStageName(), withPenalty);
//
//    }
//    @Override
//    public void generateStageCatBestResult(Long stageId) {
//
//        long eventId = eventRepository.getEventIdByStageId(stageId);
//        Stage stage = stageService.getStageById(stageId);
//        //Stage stage = stageRepository.getById(stageId);
//        List<Lap> laps = lapService
//                .getAllCrewBestLapByStageId(eventId, stageId, eventId, stageId);
//        HashMap<Category, Set<Lap>> catMap = new HashMap<>();
//
//        for (Lap l : laps) {
//            Category cat = l.getCrew().getCategory();
//            if (catMap.containsKey(cat)) {
//                catMap.get(cat).add(l);
//            } else {
//                Set<Lap> catLaps = new TreeSet();
//                catLaps.add(l);
//                catMap.put(cat, catLaps);
//            }
//        }
//
//        PdfCreator.createStageCatBestResultTable(catMap, stage.getStageName());
//
//    }
//    @Override
//    public void generateFinalResult(Long stageId) {
//        //long eventId = eventRepository.getEventIdByStageId(stageId);
//        Stage stage = stageRepository.getById(stageId);
//        List<Crew> crews = crewService.getAllCrewByStageId(stageId);
//        //List<Lap> laps = lapService.getAllByStageId(stageId);
//
//        int su = 0;
//        HashMap<Category, Set<List<Lap>>> catMap = new HashMap<>();
//
//        for (Crew c : crews) {
//            if (!catMap.containsKey(c.getCategory())) {
//                Set<List<Lap>> crewLaps = new TreeSet<>((List<Lap> s1, List<Lap> s2) -> {
//                    long sum1 = 0;
//                    long sum2 = 0;
//                    for (Lap l: s1) {
//                        sum1 += l.getLapTime() + l.getPenalty().getPenaltyTime();
//                    }
//                    for (Lap l: s2) {
//                        sum2 += l.getLapTime() + l.getPenalty().getPenaltyTime();
//                    }
//                    return Long.compare(sum1, sum2);
//                });
//                catMap.put(c.getCategory(), crewLaps);
//            }
//
//            List<Lap> crewLaps = lapService.getAllByCrewIdAndStageId(c.getId(), stageId);
//            if (su == 0) {
//                su = crewLaps.size();
//            }
//
//            catMap.get(c.getCategory()).add(crewLaps);
//        }
//
//        PdfCreator.createFinalCatResultTable(catMap, stage.getStageName(), su);
//
//    }
    private void initEvent(Event e) {
        List<Category> categories = e.getCategorySet().getCategories();
        categories.size();
        List<Crew> crews = e.getCrews();
        for (Crew c : crews) {
            c.getPilot().getAge();
            c.getVehicle().getYear();
            c.getCategory().getName();
        }
        e.getStages().size();
        e.getTrack().getTrackName();
    }

    private LocalDate parseDate(String date) {
        DateTimeFormatter datetFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate parsedDate = LocalDate.parse(date, datetFormatter);

        return parsedDate;
    }

}
