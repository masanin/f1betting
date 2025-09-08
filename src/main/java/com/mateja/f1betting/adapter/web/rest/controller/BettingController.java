package com.mateja.f1betting.adapter.web.rest.controller;

import com.mateja.f1betting.adapter.web.rest.dto.bet.BetResponseDto;
import com.mateja.f1betting.adapter.web.rest.dto.bet.PlaceBetRequestDto;
import com.mateja.f1betting.adapter.web.rest.mapper.BetResponseMapper;
import com.mateja.f1betting.domain.usecase.ManageBetUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequestMapping("/api/v1/bets")
@RequiredArgsConstructor
@Tag(name = "Betting", description = "Bet management operations")
public class BettingController {
    private final ManageBetUseCase manageBetsUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a bet", description = "Place a new bet on a driver for a specific F1 event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bet placed successfully",
                    content = @Content(schema = @Schema(implementation = BetResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient balance"),
            @ApiResponse(responseCode = "403", description = "Event outcome already exists"),
            @ApiResponse(responseCode = "404", description = "Driver market not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<BetResponseDto>> placeBet(@RequestBody @Valid final PlaceBetRequestDto request) {
        log.info("Received bet request: {}", request);

        final ManageBetUseCase.PlaceBetCommand command = new ManageBetUseCase.PlaceBetCommand(
                request.userId(),
                request.eventId(),
                request.driverId(),
                request.amount()
        );

        return manageBetsUseCase.placeBet(command)
                .map(BetResponseMapper::map)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user bets", description = "Retrieve all bets placed by a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved bets",
                    content = @Content(schema = @Schema(implementation = BetResponseDto.class)))
    })
    public Flux<BetResponseDto> getBets(@PathVariable final String userId) {
        log.info("Received get bets request for userId: {}", userId);
        return manageBetsUseCase.getBets(userId)
                .map(BetResponseMapper::map);
    }
}
