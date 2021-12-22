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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.thekrechetofficial.dto.UserDto;
import ru.thekrechetofficial.dto.VehicleDto;
import ru.thekrechetofficial.entity.Gender;
import ru.thekrechetofficial.entity.Pilot;
import ru.thekrechetofficial.entity.ResetPasswordToken;
import ru.thekrechetofficial.entity.User;
import ru.thekrechetofficial.entity.Vehicle;
import ru.thekrechetofficial.repository.ResetPasswordTokenRepository;
import ru.thekrechetofficial.repository.UserRepository;
import ru.thekrechetofficial.service.EmailSenderService;
//import ru.thekrechetofficial.service.EmailSenderService;
import ru.thekrechetofficial.service.PilotService;
import ru.thekrechetofficial.service.UserService;
import ru.thekrechetofficial.service.VehicleService;
import ru.thekrechetofficial.util.security.SecurityUtils;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Controller
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PilotService pilotService;
    private final VehicleService vehicleService;
    private final EmailSenderService emailSenderService;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    @Autowired
    public UserController(UserService userService,
            UserRepository userRepository,
            PilotService pilotService,
            VehicleService vehicleService,
            EmailSenderService emailSenderService,
            ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.pilotService = pilotService;
        this.vehicleService = vehicleService;
        this.emailSenderService = emailSenderService;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam(value = "token") String tkn, ModelMap model) {
        Optional<ResetPasswordToken> token = resetPasswordTokenRepository.findByToken(tkn);
        String result = checkToken(token);
        if (result != null) {
            model.put("message", result);
            return "login";
        }
        model.put("token", tkn);

        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordForm(RedirectAttributes redirectAttributes,
            @RequestParam(value = "token") String token,
            @RequestParam(value = "password") String password,
            ModelMap model) {

        User u = userService.getByByResetPasswordToken(token);
        userService.changePassword(u, password);
        ResetPasswordToken rt = resetPasswordTokenRepository.findByToken(token).get();
        rt.setTokenCreationDate(rt.getTokenCreationDate().minusDays(1L));
        resetPasswordTokenRepository.saveAndFlush(rt);

        redirectAttributes.addFlashAttribute("message", "Пароль успешно установлен! Теперь вы можете войти под своей учетной записью.");
        return "redirect:/login";
    }

    @PostMapping("/request-password-reset")
    public String requestPasswordReset(HttpServletRequest request,
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {

        String msg = "Ссылка для восстановления пароля выслана почту.";
        Optional user = userRepository.findByEmailIgnoreCase(email);
        if (user.isPresent()) {

            ResetPasswordToken token = createResetToken((User) user.get());
            String link = request.getRequestURL().toString();
            link = link.replace(request.getServletPath(), "/reset-password?token=" + token.getToken());
            resetPasswordTokenRepository.saveAndFlush(token);
            emailSenderService.sendResetPasswordEmail(email, link);

        } else {
            msg = "Пользователя с таким email не существует";
        }

        redirectAttributes.addFlashAttribute("message", msg);
        return "redirect:/login";
    }

    @GetMapping("/fast-user-pilot-add")
    public String fastReg() {
        return "fast-user-pilot-add";
    }

    @PostMapping("/fast-user-pilot-add")
    public String fastDoReg(@RequestParam("file") MultipartFile file) {

        try ( InputStream inputStream = file.getInputStream();  BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            int count = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                System.out.printf("n: %s f: %s c: %s m: %s\n", data[0], data[1], data[2], data[3]);
                count++;
                User u = new User();
                u.setUsername("user" + count);
                u.setPassword("12345678");
                u.setEmail("user" + count + "@mail.com");

                Pilot p = new Pilot();
                p.setUser(u);
                p.setFirstName(data[0]);
                p.setLastName(data[1]);
                p.setBirthday(LocalDate.now().minusYears(20));
                p.setGender(Gender.M);

                userService.create(u);
                pilotService.savePilot(p);

                Vehicle v = new Vehicle();
                v.setHp(555);
                v.setMake(data[2]);
                v.setModel(data[3]);
                v.setPlateNumber("test");
                v.setWeight(555);
                v.setYear(555);
                v.setPilot(p);
                //p.getVehicles().add(v);

                vehicleService.save(v);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fast-user-pilot-add";
    }

    @GetMapping("/login")
    public String login(ModelMap model,
            @RequestParam(name = "message", required = false) String message) {
        return "login";
    }

    @GetMapping("/register")
    public String register(ModelMap model,
            @RequestParam(name = "userDto", required = false) UserDto userDto,
            @RequestParam(name = "message", required = false) String message) {

        if (userDto != null) {
            model.put("userDto", userDto);
            model.put("message", message);
        }

        return "register";
    }

    @PostMapping("/create-new-user")
    public String createUser(UserDto userDto, RedirectAttributes redirectAttributes) {

        int result = userService.isExists(userDto.getLogin(), userDto.getEmail());

        if (result != 0) {
            String msg = null;
            if (result == 1) {
                msg = "Пользователь с таким логином уже существует";
            }
            if (result == -1) {
                msg = "Пользователь с таким email уже существует";
            }

            redirectAttributes.addFlashAttribute("userDto", userDto);
            redirectAttributes.addFlashAttribute("message", msg);
            return "redirect:/register";
        }

        User u = new User();
        u.setUsername(userDto.getLogin());
        u.setPassword(userDto.getPassword());
        u.setEmail(userDto.getEmail());

        Pilot p = new Pilot();
        p.setLastName(userDto.getLastName());
        p.setFirstName(userDto.getFirstName());
        p.setBirthday(LocalDate.parse(userDto.getBirthDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        p.setGender(Gender.valueOf(userDto.getGender()));
        p.setUser(u);

        try {
            userService.create(u);
            pilotService.savePilot(p);
            redirectAttributes.addFlashAttribute("message", "Регистрация завершена, успешно! Теперь вы можете войти под своей учетной записью.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("userDto", userDto);
            redirectAttributes.addFlashAttribute("message", "Ошибка при регистрации, попробуйте еще раз, если ошибка повторится, измените вводимые данные");
            return "redirect:/register";
        }

        return "redirect:/login";
    }

    @GetMapping("/vehicles")
    @PreAuthorize("hasRole('USER')")
    public String showVehicles(ModelMap model) {
        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
        Pilot p = pilotService.getByUserId(u.getId());
        model.put("pilot", p);

        return "vehicles";
    }

    @PostMapping("/vehicle/new")
    @PreAuthorize("hasRole('USER')")
    public String addVehicle(VehicleDto dto) {
//        System.out.printf("id: %d mk: %s mdl: %s hp: %d w: %d y: %d pn: %s\n",
//                dto.getPilotId(), dto.getModel(), dto.getMake(), dto.getHp(), dto.getWeight(), dto.getYear(), dto.getPlateNumber());
        Pilot p = pilotService.getById(dto.getPilotId());
        Vehicle v = new Vehicle();
        v.setPilot(p);
        v.setMake(dto.getMake());
        v.setModel(dto.getModel());
        v.setHp(dto.getHp());
        v.setWeight(dto.getWeight());
        v.setYear(dto.getYear());
        v.setPlateNumber(dto.getPlateNumber());
        p.getVehicles().add(v);

        //vehicleService.save(v);
        pilotService.savePilot(p);

        return "redirect:/vehicles";
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('USER')")
    public String editVehicle(ModelMap model, @RequestParam(value = "vehicleId") Long id) {

        Vehicle v = vehicleService.getById(id);
        model.put("vehicle", v);

        return "vehicle-edit";
    }

    @PostMapping("/vehicle/edit")
    @PreAuthorize("hasRole('USER')")
    public String saveEditedVehicle(VehicleDto dto) {
//        System.out.printf("id: %d mk: %s mdl: %s hp: %d w: %d y: %d pn: %s\n",
//                dto.getVehicleId(), dto.getMake(), dto.getModel(), dto.getHp(), dto.getWeight(), dto.getYear(), dto.getPlateNumber());

        Vehicle v = vehicleService.getById(dto.getVehicleId());

        if (!v.getMake().equals(dto.getMake())) {
            v.setMake(dto.getMake());
        }
        if (!v.getModel().equals(dto.getModel())) {
            v.setModel(dto.getModel());
        }
        if (v.getHp() != dto.getHp()) {
            v.setHp(dto.getHp());
        }
        if (v.getWeight() != dto.getWeight()) {
            v.setWeight(dto.getWeight());
        }
        if (v.getYear() != dto.getYear()) {
            v.setYear(dto.getYear());
        }
        if (!v.getPlateNumber().equals(dto.getPlateNumber())) {
            v.setPlateNumber(dto.getPlateNumber());
        }

        vehicleService.save(v);

        return "redirect:/vehicles";
    }

    @GetMapping("/account")
    @PreAuthorize("hasRole('USER')")
    public String account(ModelMap model) {
        User u = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
        Pilot p = pilotService.getByUserId(u.getId());
        model.put("pilot", p);
        model.put("user", u);

        return "account";
    }

    @GetMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public String showChangeUserPasswordForm(ModelMap model,
            @RequestParam(name = "error", required = false) String message) {

        //User user = userService.getById(userId);
        model.put("error", message);

        return "change-password";
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasRole('USER')")
    public String sUserPassword(ModelMap model,
            @RequestParam(value = "oldpassword") String oldPassword,
            @RequestParam(value = "newpassword") String newPassword,
            @RequestParam(value = "newpassword2") String newPassword2,
            RedirectAttributes redirectAttributes) {

        User user = userService.getByUserName(SecurityUtils.getCurrentUserDetails().getUsername());
        int result = 0;
        if (newPassword.equals(newPassword2)) {
            result = userService.changeUserPassword(user, oldPassword, newPassword);
        } else {
            redirectAttributes.addAttribute("error", "Новые пароли не совпадают");
            return "redirect:/change-password";
        }

        if (result == 0) {
            redirectAttributes.addAttribute("error", "Неверный старый пароль");
            return "redirect:/change-password";
        }

        return "redirect:/account";
    }

    @PostMapping("/accountedit")
    @PreAuthorize("hasRole('USER')")
    public String saveEditedUser(ModelMap model, @RequestParam(value = "pilotId") Long id) {
        Pilot p = pilotService.getById(id);
        User u = p.getUser();
        model.put("pilot", p);
        model.put("user", u);

        return "account-edit";
    }

    @PostMapping("/account/edit")
    @PreAuthorize("hasRole('USER')")
    public String saveEditedUser(UserDto dto, @RequestParam(value = "pilotId") Long id) {
        System.out.println("pilot " + id);
        System.out.printf("name: %s sur: %s email: %s\n", dto.getFirstName(), dto.getLastName(), dto.getEmail()
        );
        Pilot p = pilotService.getById(id);
        User u = p.getUser();

        if (!p.getLastName().equals(dto.getLastName())) {
            p.setLastName(dto.getLastName());
        }
        if (!p.getFirstName().equals(dto.getFirstName())) {
            p.setFirstName(dto.getFirstName());
        }

        pilotService.savePilot(p);
        if (!u.getEmail().equals(dto.getEmail())) {
            u.setEmail(dto.getEmail());
            userService.saveUser(u);
        }

        return "redirect:/account";
    }

    private ResetPasswordToken createResetToken(User user) {
        ResetPasswordToken token = new ResetPasswordToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setTokenCreationDate(LocalDateTime.now());

        return token;
    }

    private String checkToken(Optional<ResetPasswordToken> token) {
        if (checkTokenIsPresent(token)) {
           if (checkTokenIsExpired(token)) {
               return "Срок действия ссылки истёк.";
           }
           return null;
        } 
        return "Неверная ссылка.";
    }
    
    private boolean checkTokenIsPresent(Optional<ResetPasswordToken> token) {
        return token.isPresent();
    }
    
    private boolean checkTokenIsExpired(Optional<ResetPasswordToken> token) {
        LocalDateTime created = token.get().getTokenCreationDate();
        LocalDateTime bestBefore = LocalDateTime.now().minusDays(1L);
        
        return created.isBefore(bestBefore);
    }

}
