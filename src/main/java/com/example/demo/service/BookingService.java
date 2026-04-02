package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BookingService {
    private final RoomRepository roomRepo;
    private final CustomerRepository customerRepo;
    private final BookingRepository bookingRepo;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public BookingService(RoomRepository roomRepo, CustomerRepository customerRepo, BookingRepository bookingRepo) {
        this.roomRepo = roomRepo;
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
    }

    // 1. CREATE BOOKING
    @Transactional
    public Booking saveBooking(Booking b) {
        Room room = roomRepo.findById(b.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Customer customer = customerRepo.findById(b.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Set room to Occupied upon booking
        room.setStatus("Occupied"); 
        roomRepo.save(room);

        b.setRoomNumber(room.getRoomNumber());
        b.setCustomerName(customer.getName());
        b.setStatus("Confirmed"); // Matches the filter in Booking.jsx

        if (b.getTotalAmount() == 0) {
            b.setTotalAmount(room.getPrice() * 1.05);
        }

        Booking savedBooking = bookingRepo.save(b);
        
        // Async email so the UI doesn't freeze
        CompletableFuture.runAsync(() -> sendConfirmationEmail(customer, savedBooking));

        return savedBooking;
    }

    /**
     * UPDATED: Robust Update Status Logic
     * This is triggered when the "Check-Out" button is clicked in React.
     */
    @Transactional
    public Booking updateBookingStatus(Long id, String status) {
        Booking booking = bookingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Update the booking status (e.g., to "COMPLETED")
        booking.setStatus(status);
        
        // LOGIC: If checking out, the room MUST become available again
        if ("COMPLETED".equalsIgnoreCase(status)) {
            roomRepo.findById(booking.getRoomId()).ifPresent(room -> {
                room.setStatus("Available");
                roomRepo.save(room);
            });
        }

        return bookingRepo.save(booking);
    }

    // 2. Specialized Check-out wrapper
    @Transactional
    public void checkOut(Long id) {
        updateBookingStatus(id, "COMPLETED");
    }

    // 3. DELETE/CANCEL (Improved to ensure room safety)
    @Transactional
    public void deleteById(Long id) {
        bookingRepo.findById(id).ifPresent(booking -> {
            roomRepo.findById(booking.getRoomId()).ifPresent(room -> {
                // Only reset if this specific booking was the one occupying it
                room.setStatus("Available");
                roomRepo.save(room);
            });
            bookingRepo.deleteById(id);
        });
    }

    // 4. EMAIL LOGIC
    private void sendConfirmationEmail(Customer customer, Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(senderEmail); 
            message.setTo(customer.getEmail());
            message.setSubject("Reservation Confirmed - Room " + booking.getRoomNumber());
            message.setText("Dear " + customer.getName() + ",\n\n" +
                            "Your reservation for Room " + booking.getRoomNumber() + " is confirmed!\n" +
                            "Check-in Date: " + booking.getCheckInDate() + "\n" +
                            "Total Amount Paid: $" + booking.getTotalAmount() + "\n\n" +
                            "We look forward to seeing you!");

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email notification failed: " + e.getMessage());
        }
    }

    // HELPER METHODS
    public List<Booking> findCompletedBookings() {
        return bookingRepo.findAll().stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus()))
                .toList();
    }

    public List<Booking> findActiveBookings() {
        return bookingRepo.findAll().stream()
                .filter(b -> "Confirmed".equalsIgnoreCase(b.getStatus()))
                .toList();
    }

    public List<Booking> findAll() { 
        return bookingRepo.findAll(); 
    }
}