package repository;

import model.Alerta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColaDeAlertasTest {

    @Test
    void monticuloAtiendePrimeroLaMayorSeveridadYRespetaLlegadaEnEmpates() {
        ColaDeAlertas.PorMonticulo cola = new ColaDeAlertas.PorMonticulo();
        Alerta critica1 = new Alerta("S1", "Crítica 1", Alerta.CRITICA, 1);
        Alerta advertencia = new Alerta("S2", "Advertencia", Alerta.ADVERTENCIA, 2);
        Alerta critica2 = new Alerta("S3", "Crítica 2", Alerta.CRITICA, 3);

        cola.encolar(critica1);
        cola.encolar(advertencia);
        cola.encolar(critica2);

        assertSame(critica1, cola.atender());
        assertSame(critica2, cola.atender());
        assertSame(advertencia, cola.atender());
        assertNull(cola.atender());
    }

    @Test
    void listaOrdenadaMantienePrioridadYOrdenDeLlegadaEnEmpates() {
        ColaDeAlertas.PorListaOrdenada cola = new ColaDeAlertas.PorListaOrdenada();
        Alerta critica1 = new Alerta("S1", "Crítica 1", Alerta.CRITICA, 1);
        Alerta critica2 = new Alerta("S2", "Crítica 2", Alerta.CRITICA, 2);
        Alerta informativa = new Alerta("S3", "Informativa", Alerta.INFORMATIVA, 3);

        cola.encolar(critica1);
        cola.encolar(critica2);
        cola.encolar(informativa);

        assertSame(critica1, cola.atender());
        assertSame(critica2, cola.atender());
        assertSame(informativa, cola.atender());
    }

    @Test
    void ambasImplementacionesRechazanValoresNulos() {
        ColaDeAlertas.PorMonticulo heap = new ColaDeAlertas.PorMonticulo();
        ColaDeAlertas.PorListaOrdenada lista = new ColaDeAlertas.PorListaOrdenada();

        assertThrows(IllegalArgumentException.class, () -> heap.encolar(null));
        assertThrows(IllegalArgumentException.class, () -> lista.encolar(null));
    }
}
