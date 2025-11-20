package com.iforddow.authservice.common.config;

import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* A configuration class for Twilio integration.
*
* @author IFD
* @since 2025-11-04
* */
@Configuration
public class TwilioConfig {

    @Value("${twilio.live.account.sid}")
    private String liveAccountSid;

    @Value("${twilio.test.account.sid}")
    private String testAccountSid;

    @Value("${twilio.live.auth.token}")
    private String liveAuthToken;

    @Value("${twilio.test.auth.token}")
    private String testAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    /**
    * A method to initialize Twilio with the live credentials.
    *
    * @author IFD
    * @since 2025-11-04
    * */
    @PostConstruct
    public void init() {
        Twilio.init(liveAccountSid, liveAuthToken);
    }

    /**
    * A bean to provide TwilioRestClient for sending SMS.
    *
    * @author IFD
    * @since 2025-11-04
    * */
    @Bean
    public TwilioRestClient twilioRestClient() {
        return Twilio.getRestClient();
    }

}
