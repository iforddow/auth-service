package com.iforddow.authservice.common.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
* A configuration class for RabbitMQ.
*
* @author IFD
* @since 2025-10-29
* */
@Configuration
public class RabbitConfig {

    // New accounts stream
    public static final String NEW_ACCOUNT_QUEUE = "authService.newAccount.queue";
    public static final String NEW_ACCOUNT_EXCHANGE = "authService.newAccount.exchange";
    public static final String NEW_ACCOUNT_ROUTING_KEY = "authService.newAccount.routingkey";

    // Deleted accounts stream
    public static final String DELETED_ACCOUNT_QUEUE = "authService.deletedAccount.queue";
    public static final String DELETED_ACCOUNT_EXCHANGE = "authService.deletedAccount.exchange";
    public static final String DELETED_ACCOUNT_ROUTING_KEY = "authService.deletedAccount.routingkey";


    /**
    * A bean for the new account queue, exchange, and binding.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Bean
    Queue newAccountQueue() {
        return new Queue(NEW_ACCOUNT_QUEUE, true);
    }

    /**
    * A bean for the new account exchange.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Bean
    DirectExchange newAccountExchange() {
        return new DirectExchange(NEW_ACCOUNT_EXCHANGE);
    }

    /**
    * A bean for the new account binding.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Bean
    Binding newAccountBinding(Queue newAccountQueue, DirectExchange newAccountExchange) {
        return BindingBuilder.bind(newAccountQueue).to(newAccountExchange).with(NEW_ACCOUNT_ROUTING_KEY);
    }

    /**
    * A bean for the deleted account queue, exchange, and binding.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Bean
    Queue deletedAccountQueue() {
        return new Queue(DELETED_ACCOUNT_QUEUE, true);
    }

    /**
    * A bean for the deleted account exchange.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Bean
    DirectExchange deletedAccountExchange() {
        return new DirectExchange(DELETED_ACCOUNT_EXCHANGE);
    }

    /**
    * A bean for the deleted account binding.
    *
    * @author IFD
    * @since 2025-10-29
    * */
    @Bean
    Binding deletedAccountBinding(Queue deletedAccountQueue, DirectExchange deletedAccountExchange) {
        return BindingBuilder.bind(deletedAccountQueue).to(deletedAccountExchange).with(DELETED_ACCOUNT_ROUTING_KEY);
    }

}
