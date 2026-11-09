package utilidades

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties

// Quita los paquetes del tipo: "kotlin.collections.Map<modelo.Producto, kotlin.Int>" -> "Map<Producto, Int>"
private fun KType.legible(): String = toString().replace(Regex("(?:\\w+\\.)+"), "")

// Lista las propiedades y los métodos de una clase usando reflection.
// Con `incluirHeredados` agrega también lo que viene de Any (equals, hashCode, toString).
fun listarMiembros(clase: KClass<*>, incluirHeredados: Boolean = false) {
    val propiedades = if (incluirHeredados) clase.memberProperties else clase.declaredMemberProperties
    val funciones = if (incluirHeredados) clase.memberFunctions else clase.declaredMemberFunctions

    println("   Clase ${clase.simpleName}")
    println("   Propiedades (${propiedades.size}):")
    propiedades.sortedBy { it.name }.forEach { propiedad ->
        val mutabilidad = if (propiedad is KMutableProperty<*>) "var" else "val"
        println("     - $mutabilidad ${propiedad.name}: ${propiedad.returnType.legible()}")
    }

    println("   Métodos (${funciones.size}):")
    funciones.sortedBy { it.name }.forEach { funcion ->
        // El primer parámetro de un método es la instancia (this), por eso se omite.
        val parametros = funcion.parameters.drop(1).joinToString(", ") { "${it.name}: ${it.type.legible()}" }
        println("     - ${funcion.name}($parametros): ${funcion.returnType.legible()}")
    }
}
