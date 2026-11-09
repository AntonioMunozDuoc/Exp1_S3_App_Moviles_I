package modelo

import utilidades.normalizar
import java.util.Objects

// Cliente de la pizzería. No es data class porque la igualdad usa solo nombre y correo.
class Cliente(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val direccion: String
) {
    // Desestructuración: val (nombre, correo, telefono) = cliente
    operator fun component1(): String = nombre
    operator fun component2(): String = correo
    operator fun component3(): String = telefono

    // Dos clientes son el mismo si coinciden nombre y correo, sin importar mayúsculas ni espacios sobrantes.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Cliente) return false
        return nombre.normalizar() == other.nombre.normalizar() &&
            correo.normalizar() == other.correo.normalizar()
    }

    // Usa los mismos atributos que equals() para que Set y Map no dejen pasar duplicados.
    override fun hashCode(): Int = Objects.hash(nombre.normalizar(), correo.normalizar())

    override fun toString(): String = "Cliente(nombre=$nombre, correo=$correo)"
}
