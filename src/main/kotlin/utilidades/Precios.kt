package utilidades

import java.util.Locale

// Utilidades de precios compartidas por los pedidos y el resumen en consola.

// Formato chileno con separador de miles: 21990.0 -> $21.990
fun formatearPrecio(valor: Double): String =
    "$" + String.format(Locale.forLanguageTag("es-CL"), "%,.0f", valor)
