package com.iforddow.authservice.application.events;

import java.util.UUID;

public record DeleteAccountEvent(UUID accountId) { }
