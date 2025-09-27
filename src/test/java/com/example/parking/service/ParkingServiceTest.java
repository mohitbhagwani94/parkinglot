package com.example.parking.service;

import com.example.parking.entity.ParkingSlot;
import com.example.parking.entity.Ticket;
import com.example.parking.model.SlotStatus;
import com.example.parking.repository.ParkingSlotRepository;
import com.example.parking.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private TicketRepository ticketRepo;

    @Mock
    private ParkingSlotRepository slotRepo;

    @InjectMocks
    private ParkingService parkingService;

    @Test
    void finalizeExit_whenPaymentSuccess_shouldFreeSlotAndCloseTicket() {
        Ticket ticket = Ticket.builder()
                .id(1L)
                .slotId(10L)
                .paid(false)
                .status("ACTIVE")
                .entryTime(LocalDateTime.now().minusHours(2))
                .build();

        ParkingSlot slot = ParkingSlot.builder()
                .id(10L)
                .status(SlotStatus.OCCUPIED)
                .build();

        when(ticketRepo.findById(1L)).thenReturn(Optional.of(ticket));
        when(slotRepo.findById(10L)).thenReturn(Optional.of(slot));
        when(slotRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(ticketRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Ticket result = parkingService.finalizeExit(1L, true);

        assertTrue(result.isPaid());
        assertEquals("CLOSED", result.getStatus());
        ArgumentCaptor<ParkingSlot> slotCaptor = ArgumentCaptor.forClass(ParkingSlot.class);
        verify(slotRepo).save(slotCaptor.capture());
        assertEquals(SlotStatus.FREE, slotCaptor.getValue().getStatus());
    }

    @Test
    void finalizeExit_whenPaymentFails_shouldKeepSlotOccupiedAndReturnTicket() {
        Ticket ticket = Ticket.builder()
                .id(2L)
                .slotId(20L)
                .paid(false)
                .status("ACTIVE")
                .entryTime(LocalDateTime.now().minusHours(1))
                .build();

        when(ticketRepo.findById(2L)).thenReturn(Optional.of(ticket));

        Ticket result = parkingService.finalizeExit(2L, false);

        assertFalse(result.isPaid());
        assertEquals("ACTIVE", result.getStatus());
        verify(slotRepo, never()).save(any());
        verify(ticketRepo, never()).save(ticket); // because method returns early when payment fails
    }
}
