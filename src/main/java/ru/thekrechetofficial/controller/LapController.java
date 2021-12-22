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

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.thekrechetofficial.dto.PenaltyDto;
import ru.thekrechetofficial.service.LapService;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class LapController {

    private final LapService lapService;

    @Autowired
    public LapController(LapService lapService) {
        this.lapService = lapService;
    }

//    @PostMapping("/uploadFile")
//    public String submit(@RequestParam("file") MultipartFile file,
//                         @RequestParam("stageId") Long stageId,
//                         ModelMap model) throws IOException {
//
//        int result = lapService.parseThenSaveLaps(file, stageId);
//
//        model.put("result", result);
//        model.put("name", file.getName());
//        model.put("type", file.getContentType());
//        model.put("orig", file.getOriginalFilename());
//        model.put("size", file.getSize());
//
//        return "upload";
//    }

    @PostMapping("/lap-delete")
    public String deleteLap(@RequestParam(value = "lapId") Long lapId,
                            @RequestParam(value = "stageId") Long stageId,
                            RedirectAttributes redirectAttributes) {
        lapService.deleteById(lapId);
        
        redirectAttributes.addAttribute("stageId", stageId);
        return "redirect:/stage-view";
    }

    @PostMapping("/manage/addPenalty")
    public String addPenalty(PenaltyDto penaltyDto, //StageDto stageDto
            @RequestParam(value = "lapId", required = true) Long lapId//,
    //            @RequestParam(value = "stageId", required=true) Long stageId

    ) {
        //System.out.println("stageId " + + stageDto.getId());

        long stageId = lapService.addPenaltyToLap(lapId, penaltyDto);

//        System.out.println("sta " + stageId);
        return "redirect:/manage/" + stageId;

    }

}
