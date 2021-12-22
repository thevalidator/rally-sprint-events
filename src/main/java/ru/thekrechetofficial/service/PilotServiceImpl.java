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

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thekrechetofficial.entity.Pilot;
import ru.thekrechetofficial.entity.Vehicle;
import ru.thekrechetofficial.repository.PilotRepository;


/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class PilotServiceImpl implements PilotService {
    
    private final PilotRepository pilotRepository;
    private final VehicleService vehicleService;

    @Autowired
    public PilotServiceImpl(PilotRepository pilotRepository, VehicleService vehicleService) {
        this.pilotRepository = pilotRepository;
        this.vehicleService = vehicleService;
    }

    @Override
    public void savePilot(Pilot p) {
        Set<Vehicle> vehicles = p.getVehicles();
        if (!vehicles.isEmpty()) {
            for (Vehicle v: vehicles) {
                vehicleService.save(v);
            }
        }
        pilotRepository.saveAndFlush(p);
    }    

    @Override
    public Pilot getByUserId(long userId) {
        Pilot p = pilotRepository.findByUserId(userId).orElseThrow();
        Set<Vehicle> vehicles = p.getVehicles();
        vehicles.size();
        return p;
    }

    @Override
    public Pilot getById(Long pilotId) {
        Pilot p = pilotRepository.findById(pilotId).orElseThrow();
        p.getVehicles().size();
        
        return p;
    }
    
    
    

}
