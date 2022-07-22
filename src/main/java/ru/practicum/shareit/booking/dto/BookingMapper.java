package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getItem(),
                booking.getBooker(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDto bookingDto, User user, Status status) {
        Booking booking = new Booking();
        booking.setItem(bookingDto.getItem());
        booking.setBooker(user);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(status);
        return booking;
    }

    public static List<BookingDto> toBookingDto(Collection<Booking> bookings) {
        List<BookingDto> dtos = new ArrayList<>();
        for (Booking booking : bookings) {
            dtos.add(toBookingDto(booking));
        }
        return dtos;
    }
}
