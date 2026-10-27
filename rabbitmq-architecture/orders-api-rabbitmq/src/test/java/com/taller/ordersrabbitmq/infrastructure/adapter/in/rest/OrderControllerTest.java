package com.taller.ordersrabbitmq.infrastructure.adapter.in.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.taller.ordersrabbitmq.application.port.in.CreateOrderUseCase;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class OrderControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private CreateOrderUseCase createOrderUseCase;

    @Test
    void shouldAcceptValidOrder() throws Exception {
        UUID correlationId = UUID.randomUUID();
        when(createOrderUseCase.createOrder(any())).thenReturn(new CreateOrderUseCase.AcceptedOrder("ORD-123", correlationId));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "Pedro",
                                  "product": "Libro de arquitectura",
                                  "quantity": 1,
                                  "unitPrice": 85000
                                }
                                """))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("accepted"))
                .andExpect(jsonPath("$.message").value("Pedido recibido y enviado a RabbitMQ"))
                .andExpect(jsonPath("$.orderId").value("ORD-123"))
                .andExpect(jsonPath("$.correlationId").value(correlationId.toString()));

        verify(createOrderUseCase).createOrder(any());
    }

    @Test
    void shouldReturnConsistentValidationErrors() throws Exception {
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "",
                                  "product": "",
                                  "quantity": 0,
                                  "unitPrice": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("La solicitud contiene errores de validación"))
                .andExpect(jsonPath("$.path").value("/orders"))
                .andExpect(jsonPath("$.correlationId").isNotEmpty())
                .andExpect(jsonPath("$.validationErrors.customerName").exists())
                .andExpect(jsonPath("$.validationErrors.product").exists())
                .andExpect(jsonPath("$.validationErrors.quantity").exists())
                .andExpect(jsonPath("$.validationErrors.unitPrice").exists());
    }
}
