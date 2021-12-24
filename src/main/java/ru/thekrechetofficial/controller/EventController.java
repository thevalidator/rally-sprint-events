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
package ru.thekrechetofficial.controller;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.thekrechetofficial.dto.CrewDto;
import ru.thekrechetofficial.dto.EventDto;
import ru.thekrechetofficial.dto.GeneralResultDto;
import ru.thekrechetofficial.dto.GeneralResultWithSSDto;
import ru.thekrechetofficial.dto.StageDto;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Penalty;
import ru.thekrechetofficial.entity.Stage;
import ru.thekrechetofficial.entity.StageType;
import ru.thekrechetofficial.entity.Track;
import ru.thekrechetofficial.repository.EventRepository;
import ru.thekrechetofficial.repository.LapRepository;
import ru.thekrechetofficial.repository.TrackRepository;
import ru.thekrechetofficial.service.CrewService;
import ru.thekrechetofficial.service.EventService;
import ru.thekrechetofficial.service.LapService;
import ru.thekrechetofficial.service.PenaltyService;
import ru.thekrechetofficial.service.StageService;
import ru.thekrechetofficial.dto.IGeneralResult;
import ru.thekrechetofficial.dto.ILapDtoForVariance;
import ru.thekrechetofficial.dto.LapDtoTimesReport;
import ru.thekrechetofficial.dto.VarianceDto;
import ru.thekrechetofficial.entity.CategorySet;
import ru.thekrechetofficial.entity.Pilot;
import ru.thekrechetofficial.entity.User;
import ru.thekrechetofficial.service.CategorySetService;
import ru.thekrechetofficial.util.security.SecurityUtils;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class EventController {

    private final EventRepository eventRepository;
    private final EventService eventService;
    private final StageService stageService;
    private final LapService lapService;
    private final CrewService crewService;
    private final TrackRepository trackRepository;
    private final PenaltyService penaltyService;
    private final CategorySetService catSetService;

    @Autowired
    public EventController(EventRepository eventRepository,
            EventService eventService,
            StageService stageService,
            LapService lapService,
            CrewService crewService,
            TrackRepository trackRepository,
            PenaltyService penaltyService,
            CategorySetService catSetServise) {

        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.stageService = stageService;
        this.lapService = lapService;
        this.crewService = crewService;
        this.trackRepository = trackRepository;
        this.penaltyService = penaltyService;
        this.catSetService = catSetServise;
    }

    @GetMapping("/generate-startlist")
    public String generateStratList(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {

        //List<LapDtoTimesReport> result = eventService.getBestLapsForConfigReportByStageType(eventId, "SS", config);
        model.put("eventId", eventId);

        return "start-list";
    }

    @GetMapping("/show-config-result")
    public String showBestLapTimesResultsByConfig(ModelMap model,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "configNumber") Integer config) {

        List<LapDtoTimesReport> result = eventService.getBestLapsForConfigReportByStageType(eventId, "SS", config);
        Integer configAmount = eventService.getEventById(eventId).getConfigAmount();

        model.put("configAmount", configAmount);
        model.put("configNumber", config);
        model.put("eventId", eventId);
        model.put("results", result);

        return "best-by-config";
    }

    @GetMapping("/show-absolute-result")
    public String showAbsoluteEventResults(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {

        List<LapDtoTimesReport> result = eventService.getResultListForAbsoluteReportByStageType(eventId, "SS");
        model.put("eventId", eventId);
        model.put("results", result);

        return "absolute";
    }

    @GetMapping("/show-final-result-variance")
    public String showVarianceEventResults(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {

        List<VarianceDto> result = eventService.getVarianceCoefficientsForEvent(eventId);
        model.put("eventId", eventId);
        model.put("variances", result);

        return "variance";
    }

    @GetMapping("/show-final-result-by-category")
    public String showEventResultsForCategory(ModelMap model,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "category") String category) {
        int ssAmount = eventRepository.findById(eventId).orElseThrow().getSpecialSectorAmount();

        List<GeneralResultWithSSDto> results = eventService.getSortedResultForCategory(eventId, category);
        //List<Category> categories = catSetService.getCatSetByEventId(eventId).getCategories();

        model.put("category", category);
        model.put("eventId", eventId);
        model.put("ss", ssAmount);
        model.put("results", results);

        return "show-final-result-by-category";
    }

    @GetMapping("/show-practice-result-by-stage")
    public String showEventPracticeResult(ModelMap model,
            @RequestParam(value = "stageId") Long stageId) {

        String stageName = stageService.getStageById(stageId).getStageName();
        String userName = SecurityUtils.getCurrentUserDetails().getUsername();
        List<Lap> laps = lapService.getPracticeLapsByStage(stageId, userName);

        model.put("stageName", stageName);
        model.put("laps", laps);

        return "practice-result";
    }

    @GetMapping("/show-event-practice")
    public String showEventPractice(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {

        List<Stage> practiceStages = stageService.getAllStagesByTypeFromEvent(eventId, StageType.PRA.name());
        model.put("practices", practiceStages);

        return "practice-view";
    }

    @GetMapping("/show-event-results")
    public String showEventResults(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {
        Event e = eventService.getEventById(eventId);
        if (e.getResLink() != null) {
            
            return "redirect:/" + e.getResLink();
        }
        
        int ssAmount = eventRepository.findById(eventId).orElseThrow().getSpecialSectorAmount();

        List<GeneralResultWithSSDto> results = eventService.getCategorySortedResultWithSS(eventId);
        List<Category> categories = eventService.getFullEventById(eventId).getCategorySet().getCategories();//catSetService.getCatSetByEventId(eventId).getCategories();

        model.put("categories", categories);
        model.put("eventId", eventId);
        model.put("ss", ssAmount);
        model.put("results", results);

        return "final-results";
    }

    @PostMapping("/add-penalty")
    @PreAuthorize("hasRole('ADMIN')")
    public String addPenalty(Penalty dto,
            @RequestParam(value = "lapId") Long lapId,
            @RequestParam(value = "stageId") Long stageId,
            @RequestParam(value = "penaltyId") Long penaltyId,
            RedirectAttributes redirectAttributes) {

        Lap lap = lapService.getLapById(lapId);
        Penalty penalty = penaltyService.getPenaltyById(penaltyId);
        long penaltyTime = Duration.ofSeconds(dto.getPenaltyTime()).toMillis();
        penalty.setPenaltyTime(penaltyTime);
        penalty.setDescription(dto.getDescription());

        lap.setPenalty(penalty);
        lapService.save(lap);

        redirectAttributes.addAttribute("stageId", stageId);
        return "redirect:/stage-view";
    }

    // moved to STAGE CONTROLLER
//    @GetMapping("/stage-view")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String stageView(ModelMap model,
//            @RequestParam(value = "message", required = false) String msg,
//            @RequestParam(value = "stageId") Long stageId) {
//        List<Penalty> penalties = penaltyService.getAllActivePenalties();
//        List<Lap> laps = lapService.getAllSortedByStageId(stageId);
//        Stage stage = stageService.getStageWithEventById(stageId);
//
//        model.put("configAmount", stage.getEvent().getConfigAmount());
//        model.put("penalties", penalties);
//        model.put("laps", laps);
//        model.put("stage", stage);
//
//        return "stage-view";
//    }

    @PostMapping("/add-stage-laps")
    @PreAuthorize("hasRole('ADMIN')")
    public String addStageLaps(@RequestParam(value = "stageId") Long stageId,
            @RequestParam(value = "config") Integer config,
            @RequestParam(value = "lapFile") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        int result = lapService.addLapsToStage(stageId, config, file);
        redirectAttributes.addAttribute("message", getMessage(result));
        redirectAttributes.addAttribute("stageId", stageId);

        return "redirect:/stage-view";
    }

    @PostMapping("/add-laps")
    @PreAuthorize("hasRole('ADMIN')")
    public String addLaps(@RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "stageId") Long stageId,
            @RequestParam(value = "config") Integer config,
            @RequestParam(value = "lapFile") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        int result = lapService.addLapsToStage(stageId, config, file);
        redirectAttributes.addAttribute("message", getMessage(result));
        redirectAttributes.addAttribute("eventId", eventId);
        redirectAttributes.addAttribute("expand", true);
        return "redirect:/manage-event";
    }

    // moved to STAGE CONTROLLER
//    @PostMapping("/add-session")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String addSession(StageDto dto,
//            @RequestParam(value = "eventId") Long eventId,
//            RedirectAttributes redirectAttributes) {
//
//        stageService.addStage(dto);
//        redirectAttributes.addAttribute("eventId", eventId);
//        redirectAttributes.addAttribute("expand", true);
//        return "redirect:/manage-event";
//    }

    @PostMapping("/registration-close")
    @PreAuthorize("hasRole('ADMIN')")
    public String closeRegistration(CrewDto dto,
            @RequestParam(value = "eventId") Long eventId,
            RedirectAttributes redirectAttributes) {

        eventService.setHasRegistration(false, eventId);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/manage-event";
    }

    @PostMapping("/registration-open")
    @PreAuthorize("hasRole('ADMIN')")
    public String openRegistration(CrewDto dto,
            @RequestParam(value = "eventId") Long eventId,
            RedirectAttributes redirectAttributes) {

        eventService.setHasRegistration(true, eventId);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/manage-event";
    }

    // moved to CREW CONTROLLER
//    @PostMapping("/crew-correcting")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String crewCorrect(CrewDto dto,
//            @RequestParam(value = "eventId") Long eventId, 
//            RedirectAttributes redirectAttributes) {
//        
//        crewService.correctCrew(dto);
//        redirectAttributes.addAttribute("eventId", eventId);
//        return "redirect:/manage-event";
//    }
    @PostMapping("/cancel-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancelApprove(@RequestParam(value = "crewId") Long crewId,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "url", required = false) String url,
            RedirectAttributes redirectAttributes) {
        crewService.setIsApproved(false, crewId);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/" + url;
    }

    @PostMapping("/confirm-approve")
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmApprove(@RequestParam(value = "crewId") Long crewId,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "url", required = false) String url,
            RedirectAttributes redirectAttributes) {
        crewService.setIsApproved(true, crewId);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/" + url;
    }

    @PostMapping("/confirm-payment")
    @PreAuthorize("hasRole('ADMIN')")
    public String confirmPayment(@RequestParam(value = "crewId") Long crewId,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "url", required = false) String url,
            RedirectAttributes redirectAttributes) {
        crewService.setIsPaid(true, crewId);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/" + url;
    }

    @PostMapping("/cancel-payment")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancelPayment(@RequestParam(value = "crewId") Long crewId,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "url", required = false) String url,
            RedirectAttributes redirectAttributes) {
        crewService.setIsPaid(false, crewId);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/" + url;
    }

    @PostMapping("/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public String createEvent(ModelMap model,
            EventDto eventDto,
            RedirectAttributes redirectAttr) {

        String result = eventService.createEvent(eventDto);
        redirectAttr.addFlashAttribute("message", result);
        return "redirect:/manage";
    }

    @GetMapping("/manage")
    @PreAuthorize("hasRole('ADMIN')")
    public String manage(ModelMap model,
            @RequestParam(value = "result", required = false) String result) {

        List<Event> events = eventService.getAllEventsForManage();
        model.put("events", events);
        if (result != null) {
            model.put("result", result);
        }

        return "manage-events";
    }

    @PostMapping("/event-save")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String saveEvent(ModelMap model,
            EventDto dto,
            @RequestParam(value = "eventId", required = true) long eventId,
            RedirectAttributes redirectAttr) {

        Event event = eventService.getEventById(eventId);
        eventService.updateEvent(event, dto);
        
//        int oldSS = event.getSpecialSectorAmount();
//        int newSS = dto.getSpecialSectorAmount();
//
//        if (oldSS != newSS) {
//            List<Stage> ssStages = stageService.getAllStagesByTypeFromEvent(eventId, StageType.SS.name());
//            
//            if (oldSS < newSS) {
//                //List<Stage> ssStages = stageService.getAllStagesByTypeFromEvent(eventId, StageType.SS.name());
//                //System.out.println(ssStages.size());
//                for (int i = oldSS + 1; i <= newSS; i++) {
//                    Stage s = new Stage();
//                    s.setEvent(event);
//                    s.setType(StageType.SS);
//                    s.setStageName("СУ-" + i);
//                    stageService.save(s);
//                }
//            } else if (newSS > 1) {
//                
//                for (int i = oldSS; i > newSS; i--) {
//                    long stageId = ssStages.get(oldSS - 1).getId();
//                    stageService.deleteById(stageId);
//
//                } 
//            }
//            event.setSpecialSectorAmount(newSS);
//            eventRepository.save(event);
//            //System.out.println("==> " + dto.getSpecialSectorAmount());
//        }

        //String result = eventService.createEvent(dto);
        //redirectAttr.addFlashAttribute("message", result);
        return "redirect:/manage";
    }

    @PostMapping("/event-edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editEvent(ModelMap model,
            @RequestParam(value = "eventId", required = true) long eventId) {
        Event event = eventService.getEventWithTrackById(eventId);
        List<Track> tracks = trackRepository.findAll(Sort.by(Sort.Direction.ASC, "trackName"));
        List<CategorySet> catSets = catSetService.getAllActiveCatSets();

        EventDto dto = new EventDto();
        dto.setDate(DateTimeFormatter.ofPattern("dd.MM.yyyy").format(event.getEventDate()));
        dto.setName(event.getEventName());
        dto.setRulesLink(event.getRulesLink());
        //dto.setSpecialSectorAmount(event.getSpecialSectorAmount());
        dto.setMaxCrew(event.getMaxCrew());
        dto.setConfigAmount(event.getConfigAmount());

        Track track = trackRepository.findById(event.getTrack().getId()).orElseThrow();
        CategorySet catSet = catSetService.getCatSet(event.getCategorySet().getId());

        model.put("eventId", eventId);
        model.put("dto", dto);
        model.put("catSet", catSet);
        model.put("track", track);
        model.put("tracks", tracks);
        model.put("catSets", catSets);

        return "event-edit";
    }

    @GetMapping("/manage-event")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageEvent(ModelMap model,
            @RequestParam(value = "message", required = false) String msg,
            @RequestParam(value = "eventId", required = true) long eventId,
            @RequestParam(value = "expand", required = false) Boolean expand) {
        Event event = eventService.getEventWithTrackById(eventId);
        List<Crew> crews = crewService.getAllActiveCrewsByEventId(eventId);
        List<Stage> stages = stageService.getAllSortedStagesByEventId(eventId);
        //List<Category> categories = catSetService.getCatSetByEventId(eventId).getCategories();
        if (msg != null) {
            model.put("message", msg);
        }
        model.put("event", event);
        model.put("crewAmount", crews.size());
        model.put("stages", stages);
        model.put("types", StageType.values());
        //model.put("categories", categories);
//        if (expand != null) {
//            model.put("expand", true);
//        } else {
//            model.put("expand", false);
//        }
        model.put("expand", true);

        return "manage-event";
    }

    @PostMapping("/deactivate-event")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateEvent(@RequestParam(value = "eventId") Long eventId) {
        eventService.setIsManaged(false, eventId);
        eventService.setHasRegistration(false, eventId);
        return "redirect:/manage";
    }

    @PostMapping("/activate-event")
    @PreAuthorize("hasRole('ADMIN')")
    public String activateEvent(@RequestParam(value = "eventId") Long eventId) {
        eventService.setIsManaged(true, eventId);
        return "redirect:/manage";
    }

    //NOT NEEDED ANYMORE
//    @PostMapping("/allevents")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String createEvent(ModelMap model, EventDto eventDto) {
//        String result = eventService.createEvent(eventDto);
//        TreeSet<Event> events = new TreeSet<>(eventRepository.findAllToSet());
//        model.put("events", events);
//        model.put("result", result);
//
//        return "allevents";
//    }
    @GetMapping("/results")
    public String events(ModelMap model) {
        //findAllNotDeletedToSet
        TreeSet<Event> events = new TreeSet<>(eventRepository.findAllNotDeletedToSet());
        //TreeSet<Event> events = new TreeSet<>(eventRepository.findAllToSet());
        model.put("events", events);
        return "allevents";
    }

    @GetMapping("/event-new")
    @PreAuthorize("hasRole('ADMIN')")
    public String addEvent(ModelMap model) {
        List<Track> tracks = trackRepository.findAll(Sort.by(Sort.Direction.ASC, "trackName"));
        List<CategorySet> catSets = catSetService.getAllActiveCatSets();

        model.put("tracks", tracks);
        model.put("catSets", catSets);
        return "event-new";
    }
//
//    @PostMapping("/event-delete")
//    @PreAuthorize("hasRole('ADMIN')")
//    //@ResponseStatus(HttpStatus.OK)
//    public String delete(@RequestParam(value = "eventId") Long eventId) {
//        eventService.delete(eventId);
//        return "redirect:/allevents";
//    }

    private String getMessage(int result) {
        String msg = result == 1 ? "Круги добавлены!"
                : (result == 0 ? "Невозможно добавить, круги уже были добавлены!" : "Необходимо закрыть регистрацию!");
        return msg;
    }

//    @PostMapping("/manage")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String setActive(ModelMap model, @RequestParam(value = "activeEventId") Long activeEventId) {
//        if (activeEventId != eventService.getActiveEventId()) {
//            eventService.setActiveEventId(activeEventId);
//        }
//        eventService.putEventDataToModel(model, activeEventId);
//
//        return "manage-event";
//    }
//    @GetMapping("/event/{id}")
//    public String event(ModelMap model, @PathVariable long id) {
//
//        Event event = eventService.getFullEventById(id);
//        List<Stage> stages = stageService.getAllByEventId(id);
//        List<Lap> laps = lapService.getAllByEventIdSortByCrewThenLapNum(id);
//        List<Crew> crews = crewService.getAllActiveCrewsByEventId(event.getId());
//
//        model.put("event", event);
//        model.put("stages", stages);
//        model.put("laps", laps);
//        model.put("crews", crews);
//
//        return "event";
//    }
//    @GetMapping("/manage/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public String stage(ModelMap model, @PathVariable long id) {
//
//        Stage stage = stageService.getStageById(id);
//        List<Crew> crews = crewService.getAllCrewByStageId(id);
//        List<Lap> laps = lapService.getAllByStageId(id);
//        //laps.size();
//
//        model.put("stage", stage);
//        model.put("laps", laps);
//        model.put("crews", crews);
//        return "stage";
//    }
//    @GetMapping("/result")
//    public String generateResult(Model model) {
//
//        long activeEventId = eventService.getActiveEventId();
//        List<Stage> stages = stageService.getAllByEventId(activeEventId);
//        //StageCheckboxDto checkboxDto = new StageCheckboxDto();
//
//        //model.put("checkbox", checkboxDto);
//        model.addAttribute("stages", stages);
//        //model.put("stages", stages);
//
//        return "result";
//    }
//
//    @PostMapping("/generateStageAbs")
//    public String generateStageAbsResult(@RequestParam(value = "stageId") Long stageId) {
//        eventService.generateStageAbsTable(stageId);
//        return "redirect:/allevents";
//    }
//    @PostMapping("/generateStagePil")
//    public String generateStagePilResult(@RequestParam(value = "stageId") Long stageId) {
//        eventService.generateStagePilTable(stageId, false);
//        return "redirect:/allevents";
//    }
//    @PostMapping("/generateStageCatRes")
//    public String generateStageCatResult(@RequestParam(value = "stageId") Long stageId) {
//        eventService.generateStageCatBestResult(stageId);
//        return "redirect:/allevents";
//    }
//
//    @PostMapping("/generateCustomCatRes")
//    public String generateCustomCatResult(@RequestParam(value = "stageId") Long stageId) {
//        return "redirect:/allevents";
//    }
//
//    @PostMapping("/generateCustomAbsRes")
//    public String generateCustomAbsResult() {
//        return "redirect:/allevents";
//    }
//
//    @PostMapping("/generateStblRes")
//    public String generateStblResult(@RequestParam(value = "stages") List<Stage> stages) {
//        return "redirect:/allevents";
//    }
//    @PostMapping("/generateFinalRes")
//    public String generateFinalResult(@RequestParam(value = "stageId") Long stageId) {
//        eventService.generateFinalResult(stageId);
//        return "redirect:/allevents";
//    }
}
