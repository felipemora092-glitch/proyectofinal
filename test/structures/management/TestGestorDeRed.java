package structures.management;

import model.LecturaSensor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestGestorDeRed {

    private GestorDeRed gestor;

    @BeforeEach
    void setUp() {
        gestor = new GestorDeRed();
    }

    @Test
    void historialRegistraFechaYEvento() {
        gestor.agregarEvento("Sensor EST-01 activo");

        List<String> eventos = gestor.obtenerEventosRecientes(1);
        assertEquals(1, eventos.size());
        assertTrue(eventos.get(0).contains("Sensor EST-01 activo"));
        assertTrue(eventos.get(0).startsWith("["));
    }

    @Test
    void historialConservaSoloLosEventosMasRecientes() {
        for (int i = 0; i < 5; i++) {
            gestor.agregarEvento("Evento " + i);
        }

        gestor.limpiarHistorialAntiguo(2);
        List<String> eventos = gestor.obtenerEventosRecientes(10);

        assertEquals(2, eventos.size());
        assertTrue(eventos.get(0).contains("Evento 3"));
        assertTrue(eventos.get(1).contains("Evento 4"));
    }

    @Test
    void navegadorPermiteAvanzarYRetrocederDeFormaCircular() {
        gestor.registrarAlerta("Alerta A");
        gestor.registrarAlerta("Alerta B");
        gestor.registrarAlerta("Alerta C");

        assertEquals("Alerta A", gestor.getAlertaActual());
        assertEquals("Alerta B", gestor.navegarAlSiguiente());
        assertEquals("Alerta C", gestor.navegarAlSiguiente());
        assertEquals("Alerta A", gestor.navegarAlSiguiente());
        assertEquals("Alerta C", gestor.navegarAlAnterior());
        assertEquals("Alerta C", gestor.getAlertaActual());
    }

    @Test
    void navegadorVacioRetornaNull() {
        assertNull(gestor.navegarAlSiguiente());
        assertNull(gestor.navegarAlAnterior());
    }

    @Test
    void cicloMonitoreoEvitaDuplicadosYRotaSensores() {
        gestor.programarConsulta("S1");
        gestor.programarConsulta("S2");
        gestor.programarConsulta("S1");

        assertEquals("S1", gestor.obtenerSiguienteSensor());
        assertEquals("S2", gestor.obtenerSiguienteSensor());
        assertEquals("S1", gestor.obtenerSiguienteSensor());
    }

    @Test
    void topologiaRegistraConexionBidireccionalSinDuplicados() {
        gestor.registrarConexion("S1", "S2");
        gestor.registrarConexion("S1", "S2");

        assertEquals(List.of("S2"), gestor.obtenerVecinos("S1"));
        assertEquals(List.of("S1"), gestor.obtenerVecinos("S2"));
    }

    @Test
    void topologiaDetectaEstacionesAisladasRegistradas() {
        gestor.registrarSensor("S3");
        gestor.registrarConexion("S1", "S2");

        assertEquals(List.of("S3"), gestor.detectarEstacionesAisladas());
    }

    @Test
    void bufferProcesaLecturasEnOrdenFifo() {
        LecturaSensor primera = new LecturaSensor("S1", "2026-01-01 10:00", 10, 20, 30);
        LecturaSensor segunda = new LecturaSensor("S2", "2026-01-01 10:01", 15, 25, 35);

        gestor.recibirLectura(primera);
        gestor.recibirLectura(segunda);

        assertSame(primera, gestor.procesarSiguienteLectura());
        assertSame(segunda, gestor.procesarSiguienteLectura());
        assertNull(gestor.procesarSiguienteLectura());
    }

    @Test
    void filtrarAlertasCriticasNoEliminaLecturasDelBuffer() {
        LecturaSensor normal = new LecturaSensor("S1", "2026-01-01 10:00", 10, 20, 15);
        LecturaSensor critica = new LecturaSensor("S2", "2026-01-01 10:01", 15, 25, 45);

        gestor.recibirLectura(normal);
        gestor.recibirLectura(critica);

        assertEquals(List.of(critica), gestor.filtrarAlertasCriticas(30));
        assertSame(normal, gestor.procesarSiguienteLectura());
    }
}
