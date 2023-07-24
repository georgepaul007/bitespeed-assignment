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
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        if(!contactRepo.existsByPhoneNumberAndEmail(identifyDto.getPhone(), identifyDto.getEmail())) {
            contactList.add(contactRepo.save(Contact.builder()
                    .phoneNumber(identifyDto.getPhone())
                    .email(identifyDto.getEmail())
                    .linkPrecedence(LinkPrecedence.SECONDARY)
                    .build()));
        }
        OptionalInt secondPrimaryIndex = IntStream.range(0, contactList.size())
                .filter(i -> LinkPrecedence.PRIMARY.name().equals(contactList.get(i).getLinkPrecedence().name()))
                .skip(1)
                .findFirst();
        if(secondPrimaryIndex.isPresent()) {
            contactList.get(secondPrimaryIndex.getAsInt()).setLinkPrecedence(LinkPrecedence.SECONDARY);
            contactRepo.save(contactList.get(secondPrimaryIndex.getAsInt()));
        }
        List<String> uniqueEmails = contactList.stream()
                .map(Contact::getEmail)
                .distinct()
                .collect(Collectors.toList());
        List<String> uniquePhoneNumbers = contactList.stream()
                .map(Contact::getPhoneNumber)
                .distinct()
                .collect(Collectors.toList());
        List<Integer> secondaryContactIds = contactList.stream()
                .skip(1) // Skip the first element
                .map(Contact::getId)
                .collect(Collectors.toList());
        return IdentifyResponse.builder()
                .contact(IdentifyResponse.Contact.builder()
                        .secondaryContactIds(secondaryContactIds)
                        .phoneNumbers(uniquePhoneNumbers)
                        .emails(uniqueEmails)
                        .primaryContactId(contactList.get(0).getId())
                        .build())
                .build();
    }
}
