package com.fluxkart.assignment.services.impl;

import com.fluxkart.assignment.dto.IdentifyDto;
import com.fluxkart.assignment.entity.Contact;
import com.fluxkart.assignment.enums.LinkPrecedence;
import com.fluxkart.assignment.repo.ContactRepo;
import com.fluxkart.assignment.services.ContactService;
import com.fluxkart.assignment.webmodel.IdentifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    ContactRepo contactRepo;
    public IdentifyResponse identify(IdentifyDto identifyDto) {
        List<Contact> contactList = contactRepo.findByPhoneNumberOrEmail(identifyDto.getPhone(), identifyDto.getEmail());
        if(contactList.isEmpty()) {
            Contact newContact = contactRepo.save(Contact.builder()
                    .phoneNumber(identifyDto.getPhone())
                    .email(identifyDto.getEmail())
                    .linkPrecedence(LinkPrecedence.PRIMARY)
                    .build());
            return IdentifyResponse.builder()
                    .contact(IdentifyResponse.Contact.builder()
                            .primaryContactId(newContact.getId())
                            .emails(Collections.singletonList(newContact.getEmail()))
                            .phoneNumbers(Collections.singletonList(newContact.getPhoneNumber()))
                            .secondaryContactIds(new ArrayList<>())
                            .build())
                    .build();
        }
    }
}
