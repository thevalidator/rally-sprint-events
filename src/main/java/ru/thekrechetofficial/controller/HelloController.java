/**
 * The Krechet Software
 */

package ru.thekrechetofficial.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.thekrechetofficial.util.security.SecurityUtils;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class HelloController {
    
    @Autowired
    public HelloController() {
    }
    
    @GetMapping("/")
    public String main(ModelMap model, 
            @RequestParam(name = "message", required = false) String message){     
        //model.put("username", SecurityUtils.getCurrentUserDetails().getUsername());
        model.put("message", message);
        
        return "index";
    }
    
    @GetMapping("/contacts")
    public String contacts(ModelMap model){
        
        return "contacts";
    }

}
