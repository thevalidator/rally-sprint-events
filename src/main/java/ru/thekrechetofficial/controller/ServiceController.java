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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thekrechetofficial.entity.Service;
import ru.thekrechetofficial.repository.ServiceRepository;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class ServiceController {
    
    private final ServiceRepository serviceRepository;

    @Autowired
    public ServiceController(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }
    
    @GetMapping("/services")
    @PreAuthorize("hasRole('ADMIN')")
    public String manageServices(ModelMap model){     
        model.put("services", serviceRepository.getAllActive());
        
        return "services";
    }
    
    @PostMapping("/service-create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createServices(@RequestParam(value = "serviceName") String serviceName,
                @RequestParam(value = "servicePrice") String servicePrice){   
        
        Service service = new Service();
        service.setIsActive(true);
        service.setServiceName(serviceName);
        service.setServicePrice(Integer.parseInt(servicePrice));
        
        serviceRepository.save(service);
        
        return "redirect:/services";
    }
    
    @PostMapping("/service-deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deactivateServices(@RequestParam(value = "serviceId") Long serviceId){
        serviceRepository.setIsActive(false, serviceId);
        
        return "redirect:/services";
    }
    
    @PostMapping("/show-invoice")
    @PreAuthorize("hasRole('ADMIN')")
    public String showInvoice(ModelMap model, 
            @RequestParam(value = "crewId") Long crewId){
        
        model.put("crewId", crewId);
        
        return "/invoice";
    }
    
    

}
