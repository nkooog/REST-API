package com.example.demo.events;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public void validate(EventDto eventDto, Errors errors) {
        if(eventDto.getMaxPrice() < eventDto.getBasePrice()
                && eventDto.getMaxPrice() != 0) {
            errors.rejectValue("basePrice","wrongValue","BasePrice is wrong");
            errors.rejectValue("maxPrice","wrongValue","maxPrice is wrong");
            errors.reject("wrongPrices","Value Prices are wrong");
        }

        LocalDateTime closeEnrollmentDateTime = eventDto.getCloseEnrollmentDateTime();
        LocalDateTime beginEventDateTime = eventDto.getBeginEventDateTime();
        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();


        if(endEventDateTime.isBefore(eventDto.getBeginEventDateTime())
        || endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())
        || endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong");
        }

        if(beginEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime())
                || beginEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("beginEventDateTime", "wrongValue", "beginEventDateTime is wrong");
        }

        if(closeEnrollmentDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("closeEnrollmentDateTime", "wrongValue", "closeEnrollmentDateTime is wrong");
        }


    }

}
