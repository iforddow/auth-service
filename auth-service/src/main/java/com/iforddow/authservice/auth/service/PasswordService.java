package com.iforddow.authservice.auth.service;

import com.iforddow.authservice.auth.repository.jpa.AccountRepository;
import com.iforddow.authservice.auth.request.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public void changeAccountPassword(ChangePasswordRequest changePasswordRequest) {

        //User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

//        AuthBO authBO = new AuthBO();
//
//        ArrayList<String> errors = authBO.validatePassword(changePasswordRequest.getOldPassword(),
//                changePasswordRequest.getNewPassword(),
//                changePasswordRequest.getConfirmNewPassword());

//        if(!errors.isEmpty()) {
//            throw new RuntimeException(String.join(", ", errors));
//        }

//        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
//
//        userRepository.save(user);

    }

}
