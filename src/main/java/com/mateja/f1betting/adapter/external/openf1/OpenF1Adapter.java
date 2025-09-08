package com.mateja.f1betting.adapter.external.openf1;

import com.mateja.f1betting.adapter.external.openf1.dto.OpenF1DriverResponse;
import com.mateja.f1betting.adapter.external.openf1.dto.OpenF1SessionResponse;
import com.mateja.f1betting.adapter.external.openf1.mapper.EventMapper;
import com.mateja.f1betting.adapter.external.openf1.mapper.F1DriverMarketMapper;
import com.mateja.f1betting.adapter.external.openf1.model.DriverFilter;
import com.mateja.f1betting.domain.integration.F1EventProvider;
import com.mateja.f1betting.domain.model.event.DriverMarket;
import com.mateja.f1betting.domain.model.event.Event;
import com.mateja.f1betting.domain.model.event.EventFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenF1Adapter implements F1EventProvider {
    private final WebClient openF1WebClient;

    @Override
    public Flux<Event> fetchF1Events(final EventFilter filter) {
        log.info("Fetching events from OpenF1 API with filter: {}", filter);
        return getSessions(filter)
                .collectList()
                .flatMapMany(sessions ->
                        getDrivers(DriverFilter.builder().sessionKeys(sessions.stream().map(OpenF1SessionResponse::getSessionKey).toList()).build())
                                .collectMultimap(OpenF1DriverResponse::getSessionKey)
                                .flatMapMany(drivers ->
                                        Flux.fromIterable(sessions)
                                                .map(session -> EventMapper.toF1Event(session, drivers.getOrDefault(session.getSessionKey(), Collections.emptyList())))
                                )
                );
    }

    @Override
    public Mono<DriverMarket> fetchF1DriverMarket(final int eventId, final int driverId) {
        return getDrivers(
                DriverFilter.builder()
                        .driverId(driverId)
                        .sessionKeys(List.of(eventId))
                        .build())
                .single()
                .map(F1DriverMarketMapper::toF1DriverMarket);
    }

    private Flux<OpenF1SessionResponse> getSessions(final EventFilter filter) {
        return openF1WebClient.get()
                .uri(uriBuilder -> {
                    final var uri = uriBuilder.path("/sessions");

                    if (filter.getYear() != null) {
                        uri.queryParam("year", filter.getYear());
                    }
                    if (filter.getCountry() != null) {
                        uri.queryParam("country_name", filter.getCountry());
                    }
                    if (filter.getEventType() != null) {
                        uri.queryParam("session_type", filter.getEventType().name());
                    }

                    log.debug("Built URI: {}", uri.build());
                    return uri.build();
                })
                .retrieve()
                .bodyToFlux(OpenF1SessionResponse.class);
    }

    private Flux<OpenF1DriverResponse> getDrivers(final DriverFilter driverFilter) {
        return openF1WebClient.get()
                .uri(uriBuilder -> {
                    final var uri = uriBuilder.path("/drivers");
                    if (driverFilter.getDriverId() != null) {
                        uri.queryParam("driver_number", driverFilter.getDriverId());
                    }
                    if (driverFilter.getSessionKeys() != null) {
                        driverFilter.getSessionKeys().forEach(sessionKey -> uri.queryParam("session_key", sessionKey));
                    }
                    log.debug("Built URI: {}", uri.build());
                    return uri.build();
                })
                .retrieve()
                .bodyToFlux(OpenF1DriverResponse.class);
    }
}
