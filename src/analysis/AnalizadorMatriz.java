package analysis;

import model.LecturaSensor;

/**
 * Analiza las concentraciones de PM2.5 por estación y hora.
 * Filas: estaciones EST-001 a EST-009. Columnas: horas 0 a 23.
 */
public class AnalizadorMatriz {

    private static final int NUM_ESTACIONES = 9;
    private static final int NUM_HORAS = 24;

    // Cada celda conserva el promedio de los reportes de esa estación y hora.
    private final double[][] pm25PorEstacionHora;
    private final double[][] sumaPm25;
    private final int[][] cantidadReportes;
    private final boolean[][] hayDato;

    public AnalizadorMatriz() {
        pm25PorEstacionHora = new double[NUM_ESTACIONES][NUM_HORAS];
        sumaPm25 = new double[NUM_ESTACIONES][NUM_HORAS];
        cantidadReportes = new int[NUM_ESTACIONES][NUM_HORAS];
        hayDato = new boolean[NUM_ESTACIONES][NUM_HORAS];
    }

    /**
     * Convierte EST-004 en el índice 3. Rechaza identificadores ajenos
     * al conjunto de nueve estaciones de este proyecto.
     */
    private int indiceDeEstacion(String idSensor) {
        if (idSensor == null || !idSensor.matches("EST-\\d{3}")) {
            throw new IllegalArgumentException("Identificador de estación inválido: " + idSensor);
        }

        int indice = Integer.parseInt(idSensor.substring(4)) - 1;
        if (indice < 0 || indice >= NUM_ESTACIONES) {
            throw new IllegalArgumentException("Estación fuera del rango EST-001 a EST-009: " + idSensor);
        }
        return indice;
    }

    /**
     * Registra una lectura. Si llegan varias lecturas para la misma
     * estación y hora, se calcula el promedio en lugar de sobrescribir.
     */
    public void registrar(LecturaSensor lectura) {
        if (lectura == null) {
            throw new IllegalArgumentException("La lectura no puede ser null");
        }

        int fila = indiceDeEstacion(lectura.getIdSensor());
        int columna = lectura.getHora();
        if (columna < 0 || columna >= NUM_HORAS) {
            throw new IllegalArgumentException("Hora fuera del rango 0..23: " + columna);
        }
        if (!Double.isFinite(lectura.getPm25()) || lectura.getPm25() < 0) {
            throw new IllegalArgumentException("El valor PM2.5 debe ser finito y no negativo");
        }

        sumaPm25[fila][columna] += lectura.getPm25();
        cantidadReportes[fila][columna]++;
        pm25PorEstacionHora[fila][columna] =
                sumaPm25[fila][columna] / cantidadReportes[fila][columna];
        hayDato[fila][columna] = true;
    }

    /**
     * Promedio de PM2.5 de una hora, considerando una vez el promedio
     * de cada estación que reportó en esa hora.
     *
     * Retorna NaN si ninguna estación tiene datos para esa hora.
     */
    public double promedioDeHora(int hora) {
        validarHora(hora);

        double suma = 0;
        int estacionesQueReportaron = 0;
        for (int fila = 0; fila < NUM_ESTACIONES; fila++) {
            if (hayDato[fila][hora]) {
                suma += pm25PorEstacionHora[fila][hora];
                estacionesQueReportaron++;
            }
        }

        return estacionesQueReportaron == 0
                ? Double.NaN : suma / estacionesQueReportaron;
    }

    /**
     * Promedio de PM2.5 de una estación a lo largo de las horas que reportó.
     * Retorna NaN si la estación no tiene datos.
     */
    public double promedioDeEstacion(int fila) {
        if (fila < 0 || fila >= NUM_ESTACIONES) {
            throw new IndexOutOfBoundsException("Estación inválida: " + fila);
        }

        double suma = 0;
        int horasQueReportaron = 0;
        for (int hora = 0; hora < NUM_HORAS; hora++) {
            if (hayDato[fila][hora]) {
                suma += pm25PorEstacionHora[fila][hora];
                horasQueReportaron++;
            }
        }

        return horasQueReportaron == 0 ? Double.NaN : suma / horasQueReportaron;
    }

    /**
     * Devuelve la hora con mayor promedio de PM2.5.
     * Las horas sin datos se ignoran. Retorna -1 si la matriz está vacía.
     */
    public int horaMasContaminada() {
        int mejorHora = -1;
        double mejorPromedio = Double.NEGATIVE_INFINITY;

        for (int hora = 0; hora < NUM_HORAS; hora++) {
            double promedio = promedioDeHora(hora);
            if (!Double.isNaN(promedio) && promedio > mejorPromedio) {
                mejorPromedio = promedio;
                mejorHora = hora;
            }
        }
        return mejorHora;
    }

    private void validarHora(int hora) {
        if (hora < 0 || hora >= NUM_HORAS) {
            throw new IndexOutOfBoundsException("Hora inválida: " + hora);
        }
    }

    /** Imprime la matriz; las celdas sin datos se muestran como guiones. */
    public void imprimirMatriz() {
        System.out.print("EST\\HORA");
        for (int hora = 0; hora < NUM_HORAS; hora++) {
            System.out.printf("%7s", String.format("%02d", hora));
        }
        System.out.println();

        for (int fila = 0; fila < NUM_ESTACIONES; fila++) {
            System.out.printf("EST-%03d ", fila + 1);
            for (int hora = 0; hora < NUM_HORAS; hora++) {
                if (hayDato[fila][hora]) {
                    System.out.printf("%7.1f", pm25PorEstacionHora[fila][hora]);
                } else {
                    System.out.printf("%7s", "-");
                }
            }
            System.out.println();
        }
    }
}
