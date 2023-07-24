package com.fluxkart.assignment.services;

import com.fluxkart.assignment.dto.IdentifyDto;
import com.fluxkart.assignment.webmodel.IdentifyResponse;

public interface ContactService {

    IdentifyResponse identify(IdentifyDto identifyDto);
}
