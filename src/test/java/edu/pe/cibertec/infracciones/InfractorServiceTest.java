package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    @DisplayName("Pregunta 1: Infractor con 2 multas VENCIDAS no se bloquea")
    void dadoInfractorCon2Vencidas_cuandoVerificarBloqueo_entoncesNoSeBloquea() {
        Long id = 1L;
        Infractor infractor = new Infractor();
        infractor.setId(id);
        infractor.setBloqueado(false);

        when(infractorRepository.findById(id)).thenReturn(Optional.of(infractor));
        when(multaRepository.countByInfractorIdAndEstado(id, EstadoMulta.VENCIDA)).thenReturn(2L);

        infractorService.verificarBloqueo(id);

        assertFalse(infractor.isBloqueado());
        verify(infractorRepository).save(infractor);
    }

}
