package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    @Test
    @DisplayName("Pregunta2")
    void dadoMultaVencida_cuandoActualizarEstados_entoncesCambiaAVencida() {
        // Given: Una multa que venció ayer
        Multa multa = new Multa();
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setFechaVencimiento(LocalDate.now().minusDays(1));

        when(multaRepository.findAll()).thenReturn(List.of(multa));

        // When: Ejecutamos el proceso de actualización
        multaService.actualizarEstados();

        // Then: Verificamos que ahora sea VENCIDA
        assertEquals(EstadoMulta.VENCIDA, multa.getEstado());
        verify(multaRepository).save(multa);
    }

}
