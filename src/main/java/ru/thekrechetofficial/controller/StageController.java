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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.thekrechetofficial.dto.StageDto;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Penalty;
import ru.thekrechetofficial.entity.Stage;
import ru.thekrechetofficial.entity.StageType;
import ru.thekrechetofficial.repository.EventRepository;
import ru.thekrechetofficial.service.LapService;
import ru.thekrechetofficial.service.PenaltyService;
import ru.thekrechetofficial.service.StageService;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class StageController {

    private final StageService stageService;
    private final EventRepository eventRepository;
    private final PenaltyService penaltyService;
    private final LapService lapService;

    @Autowired
    public StageController(StageService stageService, EventRepository eventRepository, PenaltyService penaltyService, LapService lapService) {
        this.stageService = stageService;
        this.eventRepository = eventRepository;
        this.penaltyService = penaltyService;
        this.lapService = lapService;
    }

    @GetMapping("/stage-view")
    @PreAuthorize("hasRole('ADMIN')")
    public String stageView(ModelMap model,
            @RequestParam(value = "message", required = false) String msg,
            @RequestParam(value = "stageId") Long stageId) {
        List<Penalty> penalties = penaltyService.getAllActivePenalties();
        List<Lap> laps = lapService.getAllSortedByStageId(stageId);
        Stage stage = stageService.getStageWithEventById(stageId);

        model.put("eventId", stage.getEvent().getId());
        model.put("configAmount", stage.getEvent().getConfigAmount());
        model.put("penalties", penalties);
        model.put("laps", laps);
        model.put("stage", stage);

        return "stage-view";
    }

    @PostMapping("/stage-edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String stageEdit(ModelMap model,
            @RequestParam(value = "stageId") Long stageId,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "stageName") String stageName,
            RedirectAttributes redirectAttributes) {
        if (!stageName.isEmpty()) {
            Stage s = stageService.getStageById(stageId);
            s.setStageName(stageName);
            stageService.save(s);
        }

        redirectAttributes.addAttribute("eventId", eventId);
        redirectAttributes.addAttribute("expand", true);
        return "redirect:/manage-event";
    }

    @PostMapping("/add-session")
    @PreAuthorize("hasRole('ADMIN')")
    public String addSession(StageDto dto,
            @RequestParam(value = "eventId") Long eventId,
            RedirectAttributes redirectAttributes) {

        stageService.addStage(dto);
        redirectAttributes.addAttribute("eventId", eventId);
        redirectAttributes.addAttribute("expand", true);
        return "redirect:/manage-event";
    }

//    @PostMapping("/stage/new")
//    public String createStage(StageDto stageDto, 
//            RedirectAttributes redirectAttributes) {
//        stageService.createStage(stageDto);
//
//        return "redirect:/manage";
//    }
    @PostMapping("/stage-result-confirm")
    public String confirmStageLaps(@RequestParam(value = "stageId") Long stageId,
            RedirectAttributes redirectAttributes) {

        stageService.confirmStageLaps(stageId);

        redirectAttributes.addAttribute("stageId", stageId);
        return "redirect:/stage-view";
    }

    //DISABLED NOW
//    @PostMapping("/stage-delete")
//    public String deletesStage(@RequestParam(value = "stageId") Long stageId) {
//        
//        Stage stage = stageService.getStageById(stageId);
//        //laps lazy initialization need to fix
//        if (stage.getLaps().isEmpty()) {
//            stageService.deleteById(stageId);
//        }
//        
//        // fix redirect
//        return "redirect:/manage-event";
//    }
    @PostMapping("/session-delete")
    public String deleteStage(@RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "stageId") Long stageId,
            RedirectAttributes redirectAttributes) {
//        Stage s = stageService.getStageById(stageId);
//        if (s.getType().equals(StageType.SS)) {
//            Event e = eventRepository.findById(eventId).orElseThrow();
//            eventRepository.updateSSAmount(e.getSpecialSectorAmount() - 1, eventId);
//        }
        stageService.deleteById(stageId);

        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/manage-event";
    }

    @PostMapping("/ss-add")
    public String addSSStage(@RequestParam(value = "eventId") Long eventId,
            RedirectAttributes redirectAttributes) {

        stageService.adSS(eventId);

        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/manage-event";
    }

    @PostMapping("/ss-delete")
    public String deleteSSStage(@RequestParam(value = "eventId") Long eventId,
            RedirectAttributes redirectAttributes) {

        stageService.deleteSS(eventId);

        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/manage-event";
    }

}
