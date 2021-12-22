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

import com.sun.mail.imap.ACL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.thekrechetofficial.dto.CrewDto;
import ru.thekrechetofficial.entity.Category;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Event;
import ru.thekrechetofficial.entity.Pilot;
import ru.thekrechetofficial.entity.User;
import ru.thekrechetofficial.entity.Vehicle;
import ru.thekrechetofficial.service.CategoryService;
import ru.thekrechetofficial.service.CrewService;
import ru.thekrechetofficial.service.EventService;
import ru.thekrechetofficial.service.PilotService;
import ru.thekrechetofficial.service.UserService;
import ru.thekrechetofficial.service.VehicleService;
import ru.thekrechetofficial.util.security.SecurityUtils;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class CrewController {

    private final CrewService crewService;
    private final EventService eventService;
    private final UserService userService;
    private final PilotService pilotService;
    private final VehicleService vehicleService;
    private final CategoryService categoryService;

    @Autowired
    public CrewController(CrewService crewService,
            EventService eventService,
            UserService userService,
            PilotService pilotService,
            VehicleService vehicleService,
            CategoryService categoryService) {

        this.crewService = crewService;
        this.eventService = eventService;
        this.userService = userService;
        this.pilotService = pilotService;
        this.vehicleService = vehicleService;
        this.categoryService = categoryService;
    }

    @PostMapping("/deleteCrew")
    public String deleteCrew(@RequestParam("crewId") Long crewId) {
        crewService.deleteCrewById(crewId);

        return "redirect:/manage";
    }
    
    @GetMapping("/manage-crew-list")
    public String adminCrewListSortByNum(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {
        Event event = eventService.getEventWithTrackById(eventId);
        List<Crew> crews = crewService.getAllActiveCrewsByEventId(eventId);
        List<Category> categories = categoryService.getAllCategoryFromSetByEventId(eventId);
        model.put("event", event);
        model.put("categories", categories);
        model.put("crews", crews);
        return "manage-crew-list";
    }
    
    @GetMapping("/manage-crew-list-sort-by-cat")
    public String adminCrewListSortByCat(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {
        Event event = eventService.getEventWithTrackById(eventId);
        List<Crew> crews = crewService.getAllActiveCrewsByEventId(eventId);
        List<Category> categories = categoryService.getAllCategoryFromSetByEventId(eventId);
        model.put("event", event);
        model.put("categories", categories);
        model.put("crews", crews);
        return "manage-crew-list-sort-by-cat";
    }

    @PostMapping("/event-crew-list")
    public String showCrewList(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {
        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
        Pilot p = pilotService.getByUserId(u.getId());
        List<Crew> crews = crewService.getAllActiveCrewsByEventId(eventId);
        model.put("pilotId", p.getId());
        List<Category> categories = categoryService.getAllCategoryFromSetByEventId(eventId);
        model.put("categories", categories);
        model.put("crews", crews);
        return "event-crew-list";
    }

    @GetMapping("/participate")
    public String participate(ModelMap model) {
        List<Event> events = eventService.getAllEventsForRegistration();
        model.put("events", events);
        return "active-events";
    }

    @PostMapping("/participate-event")
    public String registerCrew(ModelMap model, CrewDto dto) {
        int result = crewService.registerForEvent(dto);
        if (result == 0) {
            model.put("message", "Данный стартовый номер уже занят другим участником");
        } else {
            model.put("message", "Вы успешно зарегистрировались");
        }
        fillRegEventPage(model, dto.getEventId());

        //System.out.printf("ev: %d, stN: %d, pil: %d veh: %d %s\n", dto.getEventId(), dto.getStartNumber(), dto.getPilotId(), dto.getVehicleId(), dto.getCategory() );
        return "participate-event";
    }

    @GetMapping("/participate-event")
    public String registrationEventPage(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {

        fillRegEventPage(model, eventId);

        //System.out.printf("ev: %d, stN: %d, pil: %d veh: %d %s\n", dto.getEventId(), dto.getStartNumber(), dto.getPilotId(), dto.getVehicleId(), dto.getCategory() );
        return "participate-event";
    }

    @PostMapping("/crew-registration")
    public String showRegistrationForm(ModelMap model,
            @RequestParam(value = "eventId") Long eventId) {

        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
        Pilot p = pilotService.getByUserId(u.getId());
        Event e = eventService.getFullEventById(eventId);

        //System.out.println("eve " + eventId + " pil " + p.getFirstName());
        model.put("event", e);
        model.put("pilot", p);
        List<Category> cats = e.getCategorySet().getCategories();

        model.put("categories", cats);

        return "crew-registration";
    }

    @PostMapping("/crew-unreg")
    public String cancelRegistration(@RequestParam(value = "crewId") Long crewId,
            @RequestParam(value = "eventId") Long eventId) {
        crewService.disactivate(crewId, eventId);
        return "redirect:/participate";  //change on something else
    }

    @PostMapping("editCrew")
    public String editCrew(ModelMap model,
            @RequestParam("crewId") Long crewId) {
        Crew crew = crewService.getById(crewId);

        Set<Vehicle> vehicles = vehicleService.getSetOfPilotsVehicles(crew.getPilot().getId());
        for (Vehicle v : vehicles) {
            if (v.getId() == crew.getVehicle().getId()) {
                vehicles.remove(v);
            }
        }

        List<Category> categories = new ArrayList<>();
//        for (Category c : Category.values()) {
//            if (!c.equals(crew.getCategory())) {
//                categories.add(c);
//            }
//        }

        model.put("crew", crew);
        model.put("vehicles", vehicles);
        model.put("categories", categories);

        return "crew-edit";
    }

    @PostMapping("/crew-correcting")
    @PreAuthorize("hasRole('ADMIN')")
    public String crewCorrect(CrewDto dto,
            @RequestParam(value = "eventId") Long eventId,
            @RequestParam(value = "url", required = false) String url,
            RedirectAttributes redirectAttributes) {

        crewService.correctCrew(dto);
        redirectAttributes.addAttribute("eventId", eventId);
        return "redirect:/" + url;
    }

//    @PostMapping("/manage/editCrew")
//    public String editCrew(CrewDto crewDto, @RequestParam("crewId") Long crewId) {
////        System.out.println("crewId " + crewId);
////        System.out.printf("startN: %d pilName: %s pilLastName: %s pilGender: %s pilBirth: %s "
////                + "vehMak: %s vehMod: %s cat: %s year: %d palte: %s hp: %d\n",
////                crewDto.getStartNumber(), 
////                crewDto.getFirstName(), 
////                crewDto.getLastName(), 
////                crewDto.getGender(),
////                crewDto.getBirthDate(),
////                crewDto.getMake(), 
////                crewDto.getModel(), 
////                crewDto.getCategory(), 
////                crewDto.getYear(),
////                crewDto.getPlateNumber(), 
////                crewDto.getHp());
//
//        //Crew c = crewService.getById(crewId);
//        //System.out.println("c = " + c.getPilot().getFirstName());
//        crewService.updateCrew(crewDto, crewId);
//
//        return "redirect:/manage";
//    }
    private void fillRegEventPage(ModelMap model, Long eventId) {
        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
        Pilot p = pilotService.getByUserId(u.getId());
        Long status = crewService.getCrewIdIfExistsInActiveByPilot(p.getId(), eventId);
        if (status != null) {
            model.put("status", status);
        } else {
            model.put("status", 0);
        }

        Event event = eventService.getFullEventById(eventId);
        model.put("event", event);
        //model.put("crews", crewService.getAllActiveCrewsByEventId(eventId));
        model.put("crewsNumber", crewService.getAllActiveCrewSizeByEventId(eventId));
    }

}
