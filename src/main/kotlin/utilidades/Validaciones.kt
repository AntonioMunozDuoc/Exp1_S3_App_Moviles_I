package utilidades

// Funciones de validación puras: reciben los valores y devuelven un mapa { campo: "mensaje" }.
// Si el mapa vuelve vacío, los datos son válidos.

private val REGEX_CORREO = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$")
private val REGEX_NOMBRE = Regex("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s'.-]+$")

// Caracteres permitidos al escribir un teléfono: dígitos, +, espacios, paréntesis y guiones.
private val REGEX_TELEFONO_ENTRADA = Regex("^\\+?[0-9][0-9\\s().-]*$")

// 12 dígitos agrupados como país(2) área(3) bloque(3) bloque(4).
private val REGEX_TELEFONO_DIGITOS = Regex("^(\\d{2})(\\d{3})(\\d{3})(\\d{4})$")

// Unidades permitidas por producto en un pedido.
val RANGO_CANTIDAD = 1..100

fun cantidadEnRango(cantidad: Int): Boolean = cantidad in RANGO_CANTIDAD

// Devuelve el teléfono con el formato +XX (XXX) XXX-XXXX, o null si no tiene 12 dígitos o trae caracteres inválidos.
fun formatearTelefono(telefono: String): String? {
    val telefonoLimpio = telefono.trim()
    if (!REGEX_TELEFONO_ENTRADA.matches(telefonoLimpio)) {
        return null
    }

    val digitos = telefonoLimpio.filter { it.isDigit() }
    return REGEX_TELEFONO_DIGITOS.matchEntire(digitos)
        ?.destructured
        ?.let { (pais, area, bloque1, bloque2) -> "+$pais ($area) $bloque1-$bloque2" }
}

fun validarCliente(nombre: String, correo: String, telefono: String, direccion: String): Map<String, String> {
    val errores = mutableMapOf<String, String>()

    val nombreLimpio = nombre.trim()
    if (nombreLimpio.isEmpty()) {
        errores["nombre"] = "El nombre es obligatorio."
    } else if (nombreLimpio.length < 3) {
        errores["nombre"] = "El nombre debe tener al menos 3 caracteres."
    } else if (!REGEX_NOMBRE.matches(nombreLimpio)) {
        errores["nombre"] = "El nombre solo puede contener letras y espacios."
    }

    val correoLimpio = correo.trim()
    if (correoLimpio.isEmpty()) {
        errores["correo"] = "El correo electrónico es obligatorio."
    } else if (!REGEX_CORREO.matches(correoLimpio)) {
        errores["correo"] = "Ingresa un correo válido, por ejemplo nombre@dominio.cl."
    }

    if (telefono.isBlank()) {
        errores["telefono"] = "El teléfono es obligatorio."
    } else if (formatearTelefono(telefono) == null) {
        errores["telefono"] = "Ingresa un teléfono de 12 dígitos, por ejemplo +56 912 345 6789."
    }

    if (direccion.trim().length < 5) {
        errores["direccion"] = "La dirección debe tener al menos 5 caracteres."
    }

    return errores
}

fun validarCantidad(cantidad: String): Map<String, String> {
    val errores = mutableMapOf<String, String>()
    val cantidadLimpia = cantidad.trim()
    val cantidadNumero = cantidadLimpia.toIntOrNull()

    if (cantidadLimpia.isEmpty()) {
        errores["cantidad"] = "La cantidad es obligatoria."
    } else if (cantidadNumero == null) {
        errores["cantidad"] = "Ingresa una cantidad numérica entera."
    } else if (!cantidadEnRango(cantidadNumero)) {
        errores["cantidad"] = "La cantidad debe estar entre ${RANGO_CANTIDAD.first} y ${RANGO_CANTIDAD.last}."
    }

    return errores
}
