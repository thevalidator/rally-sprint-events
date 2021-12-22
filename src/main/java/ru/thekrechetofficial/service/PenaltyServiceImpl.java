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

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thekrechetofficial.entity.Penalty;
import ru.thekrechetofficial.repository.PenaltyRepository;


/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class PenaltyServiceImpl implements PenaltyService {
    
    private final PenaltyRepository penaltyRepository;

    @Autowired
    public PenaltyServiceImpl(PenaltyRepository penaltyRepository) {
        this.penaltyRepository = penaltyRepository;
    }

    @Override
    public void save(Penalty penalty) {
        penaltyRepository.saveAndFlush(penalty);
    }

    @Override
    public void deactivatePenalty(Long penaltyId) {
        penaltyRepository.setIsActive(false, penaltyId);
    }

    @Override
    public Penalty getPenaltyById(Long penaltyId) {
        Penalty penalty = penaltyRepository.findById(penaltyId).orElseThrow();
        return penalty;
    }

    @Override
    public List<Penalty> getAllActivePenalties() {
        //return penaltyRepository.findAll(Sort.by(Sort.Direction.DESC, "description"));
        List<Penalty> activePenalties = penaltyRepository.getAllActivePenalties();
        
        return activePenalties;
        
    }
    
    
    
    
    
    
    
    

}
