///*
// * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package ru.thekrechetofficial.controller;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import ru.thekrechetofficial.dto.CrewDto;
//import ru.thekrechetofficial.entity.Category;
//import ru.thekrechetofficial.entity.Crew;
//import ru.thekrechetofficial.entity.Event;
//import ru.thekrechetofficial.entity.Pilot;
//import ru.thekrechetofficial.entity.User;
//import ru.thekrechetofficial.entity.Vehicle;
//import ru.thekrechetofficial.service.CrewService;
//import ru.thekrechetofficial.service.EventService;
//import ru.thekrechetofficial.service.PilotService;
//import ru.thekrechetofficial.service.UserService;
//import ru.thekrechetofficial.service.VehicleService;
//import ru.thekrechetofficial.util.security.SecurityUtils;
//
///**
// * @author theValidator <the.validator@yandex.ru>
// */
//@Controller
//public class CrewController_old {
//    
//    private final CrewService crewService;
//    private final EventService eventService;
//    private final UserService userService;
//    private final PilotService pilotService;
//    private final VehicleService vehicleService;
//    
//
//    @Autowired
//    public CrewController_old(CrewService crewService,
//                          EventService eventService,
//                          UserService userService,
//                          PilotService pilotService,
//                          VehicleService vehicleService) {
//        this.crewService = crewService;
//        this.eventService = eventService;
//        this.userService = userService;
//        this.pilotService = pilotService;
//        this.vehicleService = vehicleService;
//    }
//    
//    @PostMapping("/deleteCrew")
//    public String deleteCrew(@RequestParam("crewId") Long crewId) {
//        crewService.deleteCrewById(crewId);
//        
//        return "redirect:/manage";
//    }
//    
//    @GetMapping("/participate")
//    public String participate(ModelMap model, 
//            @RequestParam(name = "message", required = false) String message) {
//        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
//        Pilot p = pilotService.getByUserId(u.getId());
//        Event e = eventService.getEventById(eventService.getActiveEventId());
//        List<Crew> activeCrews = crewService.getAllActiveCrewByEventId(e.getId());
//        
//        if (message != null) {
//            model.put("message", message);
//        }
//        model.put("pilotId", p.getId());
//        model.put("event", e);
//        model.put("crews", activeCrews);
//        
//        return "participate";
//    }
//    
//    @PostMapping("/participate")
//    public String registerCrewForEvent(CrewDto dto,
//            RedirectAttributes redirectAttributes) {
//        
//        int result = crewService.registerForEvent(dto);
//        if (result == 0) {
//            redirectAttributes.addFlashAttribute("message", "Данный стартовый номер уже занят другим участником");
//        } else {
//            redirectAttributes.addFlashAttribute("message", "Вы успешно зарегистрировались");
//        }
//        
//        //System.out.printf("ev: %d, stN: %d, pil: %d veh: %d %s\n", dto.getEventId(), dto.getStartNumber(), dto.getPilotId(), dto.getVehicleId(), dto.getCategory() );
//        
//        return "redirect:/participate";
//    }
//    
//    @PostMapping("/crew-registration")
//    public String showRegistrationForm(ModelMap model, 
//            @RequestParam(value = "eventId") Long eventId) {
//        
//        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
//        Pilot p = pilotService.getByUserId(u.getId());
//        if (crewService.isExists(p.getId(), eventId)) {
//            System.out.println("yes");
//            Crew c = crewService.getByPilotId(p.getId(), eventId);
//            if (!c.getIsActive()) {
//                System.out.println("yes 2");
//                crewService.activate(c.getId(), eventId); //make crew id
//            }
//            return "redirect:/participate";
//        }
//        
//        Event e = eventService.getEventById(eventId);
//
//        //System.out.println("eve " + eventId + " pil " + p.getFirstName());
//        
//        model.put("event", e);
//        model.put("pilot", p);
//        model.put("categories", Category.values());
//    
//        return "crew-registration";
//    }
//    
////    @PostMapping("/unregCrew")
////    public String cancelRegistration(@RequestParam(value = "crewId") Long crewId) {
////        crewService.disactivate(crewId, );
////        return "redirect:/participate";
////    }
//    
//    @PostMapping("editCrew")
//     public String editCrew(ModelMap model,
//                           @RequestParam("crewId") Long crewId) {
//         Crew crew = crewService.getById(crewId);
//         
//         Set<Vehicle> vehicles = vehicleService.getSetOfPilotsVehicles(crew.getPilot().getId());
//         for (Vehicle v: vehicles) {
//             if (v.getId() == crew.getVehicle().getId()) {
//                 vehicles.remove(v);
//             }
//         }
//         
//         List<Category> categories = new ArrayList<>();
//         for (Category c: Category.values()) {
//             if (!c.equals(crew.getCategory())) {
//                 categories.add(c);
//             }
//         }
//         
////         List<Category> categories = Arrays.asList(Category.values());
////         for (Category c: categories) {
////             if (c.equals(crew.getCategory())) {
////                 categories.
////                 
////                 categories.remove(c);
////             }
////         }
//         //crew.getPilot().getVehicles().size();
//         model.put("crew", crew);
//         model.put("vehicles", vehicles);
//         model.put("categories", categories);
//
//         return "crew-edit";
//     }
//    
//    
//    
//    
//    
//    
//    
//    
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
//        
//        return "redirect:/manage";
//    }
//    
////    @PostMapping("addcrewfromFile")
////    public String addCrewFromFile(@RequestParam("crewFile") MultipartFile file,
////                                  @RequestParam("eventId") Long eventId
////                                  ) throws IOException {
////        
////        crewService.quickAddCrewFromFile(file, eventId);
////        
////        return "redirect:/allevents/";
////    }
//    
//    
//
//}
