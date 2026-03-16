package com.wowmarket.wowmarket.controller;

import com.wowmarket.wowmarket.dto.BlizzardTokenDto;
import com.wowmarket.wowmarket.service.BlizzardAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class BlizzardOAuthController {
    private static final Logger logger = LoggerFactory.getLogger(BlizzardOAuthController.class);

    @Autowired
    private BlizzardAuthService blizzardAuthService;

    @GetMapping("/token")
    public ResponseEntity<String> getBlizzardToken() {
        logger.warn("Direct Blizzard access token retrieval endpoint called. Returning 403 to avoid exposing sensitive tokens.");
        return ResponseEntity.status(403).build();
    }

    @GetMapping("/token/status")
    public ResponseEntity<BlizzardTokenDto> getTokenStatus() {
        logger.debug("endpoint de token(STATUS) chamado, buscando token...");
        BlizzardTokenDto status = blizzardAuthService.getTokenStatus();
        if (status == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(status);
    }
}

