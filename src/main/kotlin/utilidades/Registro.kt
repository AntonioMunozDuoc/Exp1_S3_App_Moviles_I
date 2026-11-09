package utilidades

/**
 * Guarda elementos sin repetirlos. Usa un Set, que se apoya en equals() y hashCode():
 * si un elemento es igual a otro ya registrado, no se agrega.
 */
class Registro<T : Any> {
    private val elementos = linkedSetOf<T>()

    // Devuelve true si se registró, false si ya existía un elemento igual.
    fun registrar(elemento: T): Boolean = elementos.add(elemento)

    fun todos(): List<T> = elementos.toList()

    val cantidad: Int get() = elementos.size
}
