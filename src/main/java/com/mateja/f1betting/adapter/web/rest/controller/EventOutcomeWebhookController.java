package com.mateja.f1betting.adapter.web.rest.controller;

import com.mateja.f1betting.adapter.web.rest.dto.event.EventOutcomeRequestDto;
import com.mateja.f1betting.adapter.web.rest.dto.event.EventOutcomeResponseDto;
import com.mateja.f1betting.adapter.web.rest.mapper.EventOutcomeMapper;
import com.mateja.f1betting.domain.usecase.ResultBetsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/event-outcome")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Event outcome webhook processing")
public class EventOutcomeWebhookController {
    private final ResultBetsUseCase resultBetsUseCase;

    @PostMapping
    @Operation(summary = "Process event outcome",
            description = "Webhook endpoint to process F1 event results and calculate bet payouts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event outcome processed successfully",
                    content = @Content(schema = @Schema(implementation = EventOutcomeResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Event outcome already processed"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public Mono<ResponseEntity<EventOutcomeResponseDto>> receiveWebhook(
            @Valid @NotNull @RequestBody final EventOutcomeRequestDto request) {

        log.info("Webhook received: sessionKey={}, winner={}",
                request.sessionKey(), request.winnerDriverNumber());

        return resultBetsUseCase.processEventOutcome(EventOutcomeMapper.map(request))
                .map(EventOutcomeMapper::map)
                .map(ResponseEntity::ok);
    }
}
