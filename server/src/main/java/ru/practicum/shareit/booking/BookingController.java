package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.dto.UpdateBookingDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addNewBooking(@RequestHeader(HEADER) long userId, @RequestBody NewBookingDto bookingDto) {
        log.info("User {} create booking {}", userId, bookingDto);
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public UpdateBookingDto updateBooking(@RequestHeader(HEADER) long userId,
                                          @RequestParam Boolean approved, @PathVariable long bookingId) {
        log.info("User {} updated booking {} set approval = {}", userId, bookingId, approved);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(HEADER) long userId, @PathVariable long bookingId) {
        log.info("User {} get booking id = {}", userId, bookingId);
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsFromUser(@RequestHeader(HEADER) long userId,
                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("User {} get own bookings state = {}", userId, state);
        return bookingService.getAllFromUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsForUsersItems(@RequestHeader(HEADER) long userId,
                            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
                            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
                            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        log.info("User {} get bookings for items state = {}", userId, state);
        return bookingService.getAllForItems(userId, state, from, size);
    }

}
