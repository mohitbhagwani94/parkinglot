package com.example.parking.controller;

import com.example.parking.entity.PricingRule;
import com.example.parking.repository.PricingRuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(PricingController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PricingControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PricingRuleRepository repo;

    // 👇 Add this to satisfy ParkingLotApplication.initData
    @MockBean
    private com.example.parking.repository.ParkingSlotRepository slotRepo;

    @MockBean
    private com.example.parking.repository.VehicleRepository vehicleRepo;

    @MockBean
    private com.example.parking.repository.TicketRepository ticketRepo;

    @MockBean
    private com.example.parking.repository.PaymentRepository paymentRepo;

    @Test
    public void listPricing() throws Exception {
        when(repo.findAll()).thenReturn(List.of(new PricingRule("CAR", 120, 30.0)));
        mvc.perform(get("/api/pricing").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}
