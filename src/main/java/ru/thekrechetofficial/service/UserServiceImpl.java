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

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.thekrechetofficial.entity.User;
import ru.thekrechetofficial.repository.RoleRepository;
import ru.thekrechetofficial.repository.UserRepository;
import ru.thekrechetofficial.util.security.SecurityUtils;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @Override
    public User getByByResetPasswordToken(String token) {
        User user = userRepository.findByResetPasswordToken(token).orElseThrow();
        return user;
    }

    @Override
    public int changeUserPassword(User u, String oldPassword, String newPassword) {

        if (encoder.matches(oldPassword, u.getPassword())) {
            changePassword(u, newPassword);
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public void changePassword(User user, String password) {
        String encryptedNewPassword = encoder.encode(password);
        user.setPassword(encryptedNewPassword);
        userRepository.saveAndFlush(user);
    }

    @Override
    public void create(User user) {
        if (userRepository.findByUsernameIgnoreCase(user.getUsername()).isPresent()) {
            throw new EntityExistsException();
        }
        String password = encoder.encode(user.getPassword());
        user.setPassword(password);
        user.setRoles(List.of(roleRepository.findByName("USER").orElseThrow()));
        user.setRegDate(LocalDateTime.now());
        user.setIsActive(true);

        userRepository.saveAndFlush(user);
    }

    @Override
    public User getById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
        user.getRoles().size();
        return user;
    }

    @Override
    public User getByUserName(String username) {
        User u = userRepository.findByUsernameIgnoreCase(username).orElseThrow();
        return u;
    }

    @Override
    public void saveUser(User u) {
        userRepository.saveAndFlush(u);
    }

    @Override
    public int isExists(String userName, String email) {
        if (userRepository.existsByUsernameIgnoreCase(userName)) {
            return 1;
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return -1;
        }

        return 0;
    }

}
