package com.example.kepco_mec_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kepco_mec_springboot.model.User;
import com.example.kepco_mec_springboot.repository.UserRepository;

@RestController
@CrossOrigin(origins = "*")
public class AccountController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/api/account")
    public String insertAccount(
        @RequestParam("userId") String userId,
        @RequestParam("userPassword") String userPassword,
        @RequestParam("userEmail") String userEmail,
        @RequestParam("userNickname") String userNickname,
        @RequestParam("userTelephone") String userTelephone
    ) {
        User userInfo = new User();
        userInfo.setUserId(userId);
        userInfo.setUserPassword(userPassword);
        userInfo.setUserEmail(userEmail);
        userInfo.setUserTelephone(userTelephone);
        userInfo.setUserNickname(userNickname);
        userInfo.setUserPoint(0);
        userInfo.setManagerCheck("U");
        userRepository.save(userInfo);

        return "회원가입 성공";
    }
}
