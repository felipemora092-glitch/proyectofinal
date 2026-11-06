package control;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PilaDeAccionesTest {

    @Test
    void pilaRespetaOrdenLifo() {
        PilaDeAcciones pila = new PilaDeAcciones();
        pila.apilar("A");
        pila.apilar("B");

        assertEquals("B", pila.cima());
        assertEquals("B", pila.desapilar());
        assertEquals("A", pila.desapilar());
        assertNull(pila.desapilar());
        assertTrue(pila.estaVacia());
    }

    @Test
    void alLlenarseConservaLasCienAccionesMasRecientes() {
        PilaDeAcciones pila = new PilaDeAcciones();
        for (int i = 0; i < 101; i++) {
            pila.apilar("Accion " + i);
        }

        assertEquals(100, pila.tamano());
        assertEquals("Accion 100", pila.desapilar());
        assertEquals("Accion 99", pila.desapilar());
        assertEquals("Accion 1", pila.cima());
    }

    @Test
    void rechazaAccionesVacias() {
        PilaDeAcciones pila = new PilaDeAcciones();
        assertThrows(IllegalArgumentException.class, () -> pila.apilar(null));
        assertThrows(IllegalArgumentException.class, () -> pila.apilar(" "));
    }
}
