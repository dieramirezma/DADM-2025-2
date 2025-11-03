package com.example.myapplication

/**
 * Convierte coordenadas en formato DMS (Degrees, Minutes, Seconds) a formato decimal
 * Ejemplo: "7° 6'5.23\"N" -> 7.101453
 */
object CoordenadasUtils {

    /**
     * Convierte latitud en formato DMS a decimal
     * Formato esperado: "7° 6'5.23\"N" o "7° 6'5.23\"S"
     */
    fun latitudADecimal(dms: String?): Double? {
        if (dms.isNullOrBlank()) return null

        return try {
            // Remover espacios extras
            val limpio = dms.trim()

            // Extraer grados, minutos, segundos y dirección
            val regex = """(\d+)°\s*(\d+)'([\d.]+)"([NS])""".toRegex()
            val match = regex.find(limpio) ?: return null

            val (grados, minutos, segundos, direccion) = match.destructured

            // Convertir a decimal
            var decimal = grados.toDouble() +
                         minutos.toDouble() / 60.0 +
                         segundos.toDouble() / 3600.0

            // Si es Sur, hacerlo negativo
            if (direccion == "S") {
                decimal = -decimal
            }

            decimal
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convierte longitud en formato DMS a decimal
     * Formato esperado: "73° 8'26.72\"O" o "73° 8'26.72\"E"
     */
    fun longitudADecimal(dms: String?): Double? {
        if (dms.isNullOrBlank()) return null

        return try {
            // Remover espacios extras
            val limpio = dms.trim()

            // Extraer grados, minutos, segundos y dirección
            val regex = """(\d+)°\s*(\d+)'([\d.]+)"([EOW])""".toRegex()
            val match = regex.find(limpio) ?: return null

            val (grados, minutos, segundos, direccion) = match.destructured

            // Convertir a decimal
            var decimal = grados.toDouble() +
                         minutos.toDouble() / 60.0 +
                         segundos.toDouble() / 3600.0

            // Si es Oeste (O/W), hacerlo negativo
            if (direccion == "O" || direccion == "W") {
                decimal = -decimal
            }

            decimal
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Intenta convertir una coordenada que puede estar en formato DMS o decimal
     */
    fun coordenadaADecimal(coordenada: String?, esLatitud: Boolean = true): Double? {
        if (coordenada.isNullOrBlank()) return null

        // Intentar como decimal directo primero
        coordenada.toDoubleOrNull()?.let { return it }

        // Si no funciona, intentar como DMS
        return if (esLatitud) {
            latitudADecimal(coordenada)
        } else {
            longitudADecimal(coordenada)
        }
    }
}
