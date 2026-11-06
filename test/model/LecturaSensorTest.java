import model.LecturaSensor;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LecturaSensorTest {

    @Test
    public void testGetHora() {
        LecturaSensor ls = new LecturaSensor("EST-001", "2026-03-02 07:15", 20.0, 50.0, 30.0);
        assertEquals(7, ls.getHora());
    }

    @Test
    public void testEsValidaTrue() {
        LecturaSensor ls = new LecturaSensor("EST-001", "2026-03-02 07:15", 20.0, 50.0, 30.0);
        assertTrue(ls.esValida());
    }

    @Test
    public void testEsValidaFalseOutOfRange() {
        LecturaSensor lowTemp = new LecturaSensor("EST-001", "2026-03-02 07:15", -41.0, 50.0, 30.0);
        LecturaSensor highHum = new LecturaSensor("EST-001", "2026-03-02 07:15", 20.0, 150.0, 30.0);
        LecturaSensor negativePm = new LecturaSensor("EST-001", "2026-03-02 07:15", 20.0, 50.0, -5.0);
        assertFalse(lowTemp.esValida());
        assertFalse(highHum.esValida());
        assertFalse(negativePm.esValida());
    }

    @Test
    public void testEsValidaRechazaNaNEInfinitos() {
        LecturaSensor nan = new LecturaSensor("EST-001", "2026-03-02 07:15",
                Double.NaN, 50.0, 30.0);
        LecturaSensor infinito = new LecturaSensor("EST-001", "2026-03-02 07:15",
                20.0, 50.0, Double.POSITIVE_INFINITY);
        assertFalse(nan.esValida());
        assertFalse(infinito.esValida());
    }

    @Test
    public void testGetHoraRechazaTimestampMalFormado() {
        LecturaSensor lectura = new LecturaSensor("EST-001", "fecha-invalida",
                20.0, 50.0, 30.0);
        assertThrows(IllegalStateException.class, lectura::getHora);
    }

    @Test
    public void testToStringFormat() {
        LecturaSensor ls = new LecturaSensor("EST-001", "2026-03-02 07:15", 20.0, 50.0, 30.0);
        String expected = "EST-001 | 2026-03-02 07:15 | T=20.0 | H=50.0 | PM=30.0";
        assertEquals(expected, ls.toString());
    }
}
