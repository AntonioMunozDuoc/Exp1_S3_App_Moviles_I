package utilidades

/**
 * Lee datos desde la consola real o desde una lista de valores predefinidos,
 * para poder ejecutar la demostración completa sin escribir nada a mano.
 */
class Entrada private constructor(
    private val fuente: () -> String?,
    private val mostrarEco: Boolean
) {
    // Muestra el mensaje y lee una línea; en modo simulado repite el valor para que se vea como una sesión real.
    fun leer(mensaje: String): String {
        print("$mensaje ")
        val valor = fuente()?.trim() ?: throw IllegalStateException("Se terminó la entrada de datos.")
        if (mostrarEco) {
            println(valor)
        }
        return valor
    }

    companion object {
        fun consola() = Entrada({ readlnOrNull() }, mostrarEco = false)

        fun simulada(valores: List<String>): Entrada {
            val iterador = valores.iterator()
            return Entrada({ if (iterador.hasNext()) iterador.next() else null }, mostrarEco = true)
        }
    }
}
