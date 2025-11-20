package com.iforddow.authservice.common.service;
import com.iforddow.authservice.common.utility.AuthServiceUtility;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
* A service to send SMS messages using Twilio.
*
* @author IFD
* @since 2024-11-04
* */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    @Value("${twilio.phone.number}")
    private String fromNumber;

    /**
    * A method to send an SMS message.
    *
    * @param phoneNumber The recipient's phone number.
    * @param message The message content.
    * @param throwError Whether to throw an error on failure.
    *
    * @author IFD
    * @since 2024-11-04
    * */
    public void sendSms(String phoneNumber, String message, boolean throwError) {

        // Validate phone number and message
        if(AuthServiceUtility.isNullOrEmpty(phoneNumber) || AuthServiceUtility.isNullOrEmpty(message)) {
            return;
        }

        // Try to send the SMS message using Twilio, log or throw error on failure
        try {
            Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber(fromNumber), message).create();
        } catch (Exception e) {
            if(throwError) {
                throw new RuntimeException("Failed to send SMS to " + phoneNumber + ": " + e.getMessage());
            } else {
                log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            }

        }
    }

}
