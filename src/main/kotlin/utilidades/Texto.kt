package utilidades

// Deja un texto listo para compararlo: sin espacios en los extremos y en minúsculas.
fun String.normalizar(): String = trim().lowercase()
