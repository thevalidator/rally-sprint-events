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
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thekrechetofficial.entity.Track;
import ru.thekrechetofficial.repository.TrackRepository;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class TrackController {
    
    private final TrackRepository trackRepository;

    @Autowired
    public TrackController(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }    
    
    @GetMapping("/tracks")
    @PreAuthorize("hasRole('ADMIN')")
    public String showTrackst(ModelMap model) {
        List<Track> tracks = trackRepository.findActiveTracks();//trackRepository.findAll(Sort.by(Sort.Direction.ASC, "trackName"));
        model.put("tracks", tracks);
        return "tracks";
    }
    
    @PostMapping("/tracks")
    @PreAuthorize("hasRole('ADMIN')")
    public String addTrack(ModelMap model, @RequestParam(value = "trackName") String trackName) {
        Track track = new Track();
        track.setTrackName(trackName);
        track.setIsActive(true);
        trackRepository.saveAndFlush(track);
        
        return "redirect:/tracks";
    }
    
     @PostMapping("/track-deactivate")
     @PreAuthorize("hasRole('ADMIN')")
    public String deactivateTrack(ModelMap model, @RequestParam(value = "trackId") Long trackId) {
        
        trackRepository.setIsActive(false, trackId);
        //Track track = trackRepository.getById(trackId);
        //trackRepository.delete(track);
        
        return "redirect:/tracks";
    }

}
