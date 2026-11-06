package ingest;

import model.LecturaSensor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaDeIngestaTest {

    @Test
    void procesaLecturasEnOrdenFifo() {
        ColaDeIngesta cola = new ColaDeIngesta();
        LecturaSensor primera = new LecturaSensor("S1", "T1", 10, 20, 30);
        LecturaSensor segunda = new LecturaSensor("S2", "T2", 11, 21, 31);

        assertTrue(cola.encolar(primera));
        assertTrue(cola.encolar(segunda));
        assertSame(primera, cola.desencolar());
        assertSame(segunda, cola.desencolar());
        assertNull(cola.desencolar());
    }

    @Test
    void rechazaLecturasNulas() {
        ColaDeIngesta cola = new ColaDeIngesta();
        assertThrows(IllegalArgumentException.class, () -> cola.encolar(null));
    }

    @Test
    void informaCuandoElBufferEstaLleno() {
        ColaDeIngesta cola = new ColaDeIngesta();
        for (int i = 0; i < cola.capacidad(); i++) {
            assertTrue(cola.encolar(new LecturaSensor("S1", "T" + i, 10, 20, 30)));
        }

        assertFalse(cola.encolar(new LecturaSensor("S1", "extra", 10, 20, 30)));
        assertEquals(cola.capacidad(), cola.tamano());
        assertEquals(1, cola.getDescartadas());
    }

    @Test
    void reutilizaEspaciosDelBufferCircular() {
        ColaDeIngesta cola = new ColaDeIngesta();
        LecturaSensor[] lecturas = new LecturaSensor[cola.capacidad() + 10];

        for (int i = 0; i < cola.capacidad(); i++) {
            lecturas[i] = new LecturaSensor("S1", "T" + i, 10, 20, i);
            cola.encolar(lecturas[i]);
        }
        for (int i = 0; i < 10; i++) {
            assertSame(lecturas[i], cola.desencolar());
        }
        for (int i = cola.capacidad(); i < lecturas.length; i++) {
            lecturas[i] = new LecturaSensor("S1", "T" + i, 10, 20, i);
            assertTrue(cola.encolar(lecturas[i]));
        }

        for (int i = 10; i < lecturas.length; i++) {
            assertSame(lecturas[i], cola.desencolar());
        }
        assertTrue(cola.estaVacia());
    }
}
