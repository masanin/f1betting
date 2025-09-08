package com.mateja.f1betting.adapter.web.rest.controller;

import com.mateja.f1betting.adapter.web.rest.dto.event.EventResponseDto;
import com.mateja.f1betting.adapter.web.rest.dto.event.EventTypeDto;
import com.mateja.f1betting.adapter.web.rest.mapper.EventResponseMapper;
import com.mateja.f1betting.domain.model.event.EventFilter;
import com.mateja.f1betting.domain.model.event.EventType;
import com.mateja.f1betting.domain.usecase.ViewEventsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "F1 event viewing operations")
public class EventController {
    private final ViewEventsUseCase viewEventsUseCase;

    @GetMapping
    @Operation(summary = "List F1 events",
            description = "Retrieve F1 events with optional filters and driver market odds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved events",
                    content = @Content(schema = @Schema(implementation = EventResponseDto.class)))
    })
    public Flux<EventResponseDto> getEvents(
            @RequestParam(required = false) final EventTypeDto eventType,
            @RequestParam(required = false) final Integer year,
            @RequestParam(required = false) final String country) {

        log.info("Received request to get Events - eventType: {}, year: {}, country: {}",
                eventType, year, country);

        final EventFilter.EventFilterBuilder builder = EventFilter.builder()
                .year(year)
                .country(country);
        if (eventType != null) {
            builder.eventType(EventType.valueOf(eventType.name()));
        }

        return viewEventsUseCase.getEvents(builder.build())
                .map(EventResponseMapper::map);
    }
}