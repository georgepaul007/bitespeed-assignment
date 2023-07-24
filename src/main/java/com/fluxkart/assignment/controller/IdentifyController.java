package com.fluxkart.assignment.controller;

import com.fluxkart.assignment.dto.IdentifyDto;
import com.fluxkart.assignment.services.ContactService;
import com.fluxkart.assignment.webmodel.IdentifyRequest;
import com.fluxkart.assignment.webmodel.IdentifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentifyController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/identify")
    public ResponseEntity<IdentifyResponse> identify(@RequestBody IdentifyRequest identifyRequest) {
        return new ResponseEntity<>(contactService.identify(toIdentifyDto(identifyRequest)), HttpStatus.OK);
    }

    private static IdentifyDto toIdentifyDto(IdentifyRequest identifyRequest) {
        return IdentifyDto.builder()
                .email(identifyRequest.getEmail())
                .phone(identifyRequest.getPhone())
                .build();
    }
}
