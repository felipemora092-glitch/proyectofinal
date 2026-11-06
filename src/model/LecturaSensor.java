package model;

import java.io.Serializable;

/**
 * Representa una lectura recibida desde una estación de monitoreo.
 */
public class LecturaSensor implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final double TEMP_MIN = -40.0;
    public static final double TEMP_MAX = 60.0;
    public static final double HUMEDAD_MIN = 0.0;
    public static final double HUMEDAD_MAX = 100.0;
    public static final double PM25_MIN = 0.0;

    private final String idSensor;
    private final String timestamp;
    private final double temperatura;
    private final double humedad;
    private final double pm25;

    public LecturaSensor(String idSensor, String timestamp,
                         double temperatura, double humedad, double pm25) {
        this.idSensor = idSensor;
        this.timestamp = timestamp;
        this.temperatura = temperatura;
        this.humedad = humedad;
        this.pm25 = pm25;
    }

    public String getIdSensor() { return idSensor; }
    public String getTimestamp() { return timestamp; }
    public double getTemperatura() { return temperatura; }
    public double getHumedad() { return humedad; }
    public double getPm25() { return pm25; }

    /**
     * Extrae la hora del timestamp con formato yyyy-MM-dd HH:mm
     * o yyyy-MM-dd HH:mm:ss.
     *
     * @return hora entre 0 y 23
     * @throws IllegalStateException si el timestamp no tiene un formato válido
     */
    public int getHora() {
        if (timestamp == null || timestamp.length() < 13
                || timestamp.charAt(10) != ' '
                || timestamp.charAt(13 - 1) < '0') {
            throw new IllegalStateException("Timestamp inválido: " + timestamp);
        }

        try {
            int hora = Integer.parseInt(timestamp.substring(11, 13));
            int minuto = Integer.parseInt(timestamp.substring(14, 16));
            if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
                throw new IllegalStateException("Hora inválida en timestamp: " + timestamp);
            }
            if (timestamp.length() > 16) {
                if (timestamp.length() < 19 || timestamp.charAt(16) != ':') {
                    throw new IllegalStateException("Timestamp inválido: " + timestamp);
                }
                int segundo = Integer.parseInt(timestamp.substring(17, 19));
                if (segundo < 0 || segundo > 59) {
                    throw new IllegalStateException("Segundos inválidos: " + timestamp);
                }
            }
            return hora;
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new IllegalStateException("Timestamp inválido: " + timestamp, e);
        }
    }

    /**
     * Comprueba identificador, fecha y rangos físicos de los valores.
     */
    public boolean esValida() {
        if (idSensor == null || idSensor.isBlank()
                || timestamp == null || timestamp.isBlank()) {
            return false;
        }

        if (!Double.isFinite(temperatura)
                || !Double.isFinite(humedad)
                || !Double.isFinite(pm25)) {
            return false;
        }

        if (temperatura < TEMP_MIN || temperatura > TEMP_MAX) return false;
        if (humedad < HUMEDAD_MIN || humedad > HUMEDAD_MAX) return false;
        return pm25 >= PM25_MIN;
    }

    @Override
    public String toString() {
        return idSensor + " | " + timestamp
                + " | T=" + temperatura + " | H=" + humedad + " | PM=" + pm25;
    }
}
