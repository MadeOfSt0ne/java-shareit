package ru.practicum.shareit.booking.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                new BookingDto.User(booking.getBooker().getId()),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }

    public static UpdateBookingDto toUpdateBookingDto(Booking booking) {
        return new UpdateBookingDto(
                booking.getId(),
                booking.getStatus(),
                new UpdateBookingDto.User(booking.getBooker().getId()),
                new UpdateBookingDto.Item(booking.getItem().getId(), booking.getItem().getName())
        );
    }

    public static LastNextBookingDto toLastNextBookingDto(Booking booking) {
        return new LastNextBookingDto(
                booking.getId(),
                booking.getBooker().getId()
        );
    }

    public static Booking toBooking(NewBookingDto bookingDto, User user, Item item, Status status) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setItem(item);
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
