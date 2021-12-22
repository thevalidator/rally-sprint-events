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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thekrechetofficial.entity.Penalty;
import ru.thekrechetofficial.service.PenaltyService;

/**
 * @author theValidator <the.validator@yandex.ru>
 */

@Controller
public class PenaltyController {
    
    private final PenaltyService penaltyService;

    @Autowired
    public PenaltyController(PenaltyService penaltyService) {
        this.penaltyService = penaltyService;
    }
    
    @PostMapping("/penalty-create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createPenalty(Penalty dto) {
        Penalty penalty = new Penalty();
        long penaltyTime = Duration.ofSeconds(dto.getPenaltyTime()).toMillis();
        penalty.setPenaltyTime(penaltyTime);
        penalty.setDescription(dto.getDescription());
        penalty.setIsActive(true);
        penaltyService.save(penalty);
        
        return "redirect:/penalties";
    }
    
    @GetMapping("/penalties")
    @PreAuthorize("hasRole('ADMIN')")
    public String managePenalty(ModelMap model){   
        model.put("penalties", penaltyService.getAllActivePenalties());
        return "penalties";
    }
    
    @PostMapping("/penalty-delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivatePenalty(@RequestParam(value = "penaltyId") Long penaltyId) {
        
        penaltyService.deactivatePenalty(penaltyId);
        
        return "redirect:/penalties";
    }

}
