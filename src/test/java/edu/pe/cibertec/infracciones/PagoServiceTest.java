package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {
    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    @DisplayName("Pregunta3")
    void dadoMulta500_cuandoProcesarPagoHoy_entoncesMontoEs400() {
        Multa m = new Multa();
        m.setId(1L);
        m.setMonto(500.0);
        m.setFechaEmision(LocalDate.now());
        m.setFechaVencimiento(LocalDate.now().plusDays(7));
        m.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(m));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        PagoResponseDTO res = pagoService.procesarPago(1L);
        assertEquals(400.0, res.getMontoPagado());
        assertEquals(EstadoMulta.PAGADA, m.getEstado());
    }

    @Test
    @DisplayName("Pregunta4")
    void dadoMultaVencida_cuandoProcesarPago_entoncesCapturaPagoConRecargo() {
        // Given: Multa de 500 que venció hace 2 días
        Multa multa = new Multa();
        multa.setId(2L);
        multa.setMonto(500.00);
        multa.setFechaEmision(LocalDate.now().minusDays(12));
        multa.setFechaVencimiento(LocalDate.now().minusDays(2));
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(2L)).thenReturn(Optional.of(multa));

        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        org.mockito.ArgumentCaptor<Pago> pagoCaptor = org.mockito.ArgumentCaptor.forClass(Pago.class);

        pagoService.procesarPago(2L);

        verify(pagoRepository, times(1)).save(pagoCaptor.capture());

        Pago pagoCapturado = pagoCaptor.getValue();
        assertEquals(75.00, pagoCapturado.getRecargo());
        assertEquals(0.00, pagoCapturado.getDescuentoAplicado());
        assertEquals(575.00, pagoCapturado.getMontoPagado());
    }
}
