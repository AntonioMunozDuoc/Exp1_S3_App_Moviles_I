package modelo

import anotaciones.Promocionable
import utilidades.formatearPrecio
import utilidades.normalizar
import java.util.Objects

// Producto base del menú: dos productos son el mismo si tienen igual nombre y categoría (el precio no cuenta).
open class Producto(
    val nombre: String,
    val categoria: Categoria,
    val precio: Double
) {
    init {
        require(nombre.isNotBlank()) { "El producto debe tener nombre." }
        require(precio >= 0) { "El precio de '$nombre' no puede ser negativo." }
    }

    open fun detalle(): String = "$nombre [${categoria.etiqueta}] ${formatearPrecio(precio)}"

    // equals() es lo que Kotlin usa para el operador ==
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Producto) return false
        return nombre.normalizar() == other.nombre.normalizar() && categoria == other.categoria
    }

    override fun hashCode(): Int = Objects.hash(nombre.normalizar(), categoria)

    override fun toString(): String = nombre
}

// Pizza de la carta; sin anotación, así que no participa de promociones por producto.
open class Pizza(
    nombre: String,
    precio: Double,
    val ingredientes: List<String> = emptyList()
) : Producto(nombre, Categoria.PIZZA, precio)

// Pizza gourmet: califica para promoción especial.
@Promocionable(descuento = 0.15, descripcion = "15% en pizzas gourmet")
class PizzaGourmet(
    nombre: String,
    precio: Double,
    ingredientes: List<String> = emptyList()
) : Pizza(nombre, precio, ingredientes)

// Combo de pizzas (queda en la categoría PIZZA); califica para promoción especial.
@Promocionable(descuento = 0.20, descripcion = "20% en combos familiares")
class Combo(
    nombre: String,
    precio: Double,
    val incluye: List<String>
) : Producto(nombre, Categoria.PIZZA, precio)

class Bebida(
    nombre: String,
    precio: Double,
    val litros: Double = 1.5
) : Producto(nombre, Categoria.BEBIDA, precio)

class Ingrediente(
    nombre: String,
    precio: Double
) : Producto(nombre, Categoria.INGREDIENTE, precio)
