package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;
    private final BookingValidation validation;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER) long userId,
                                             @RequestBody NewBookingDto newBookingDto) {
        log.info("GATEWAY: Creating booking {}, userId={}", newBookingDto, userId);
        validation.validate(newBookingDto);
        return bookingClient.bookItem(userId, newBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@RequestHeader(HEADER) long userId,
                                         @RequestParam Boolean approved, @PathVariable Long bookingId) {
        log.info("GATEWAY: Patch booking {}, userId={}, approved={}", bookingId, userId, approved);
        validation.validate(approved);
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("GATEWAY: Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(HEADER) long userId,
                                          @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForItems(@RequestHeader(HEADER) long userId,
                                          @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GATEWAY: Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsForItems(userId, state, from, size);
    }

}
