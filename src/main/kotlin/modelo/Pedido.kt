package modelo

import utilidades.DESCUENTO_POR_FECHA
import utilidades.RANGO_CANTIDAD
import utilidades.cantidadEnRango
import utilidades.descuentoPromocional
import utilidades.estaEnPeriodoPromocion
import utilidades.formatearPrecio
import java.time.LocalDate

enum class EstadoPedido(val etiqueta: String) {
    EN_PREPARACION("En preparación"),
    LISTO("Listo"),
    EN_CAMINO("En camino"),
    ENTREGADO("Entregado")
}

// Pedido de un cliente. `productos` asocia cada producto con su cantidad; como la clave usa equals(),
// dos productos iguales (mismo nombre y categoría) quedan en una sola línea.
class Pedido(
    val cliente: Cliente,
    val productos: Map<Producto, Int>,
    val fecha: LocalDate,
    var estado: EstadoPedido = EstadoPedido.EN_PREPARACION,
    val id: Int = nuevoId()
) {
    init {
        require(productos.isNotEmpty()) { "El pedido debe tener al menos un producto." }
        productos.forEach { (producto, cantidad) ->
            require(cantidadEnRango(cantidad)) {
                "Cantidad inválida para '${producto.nombre}': $cantidad (permitido: $RANGO_CANTIDAD)."
            }
        }
    }

    // Suma de precio x cantidad, sin descuentos.
    val subtotal: Double
        get() = productos.entries.sumOf { (producto, cantidad) -> producto.precio * cantidad }

    // Descuento de los productos @Promocionable; el porcentaje se lee de la anotación con reflection.
    val descuentoPromocionables: Double
        get() = productos.entries.sumOf { (producto, cantidad) ->
            producto.precio * cantidad * producto.descuentoPromocional()
        }

    // Descuento al total cuando la fecha del pedido cae dentro del periodo de promoción.
    val descuentoPorFecha: Double
        get() = if (estaEnPeriodoPromocion(fecha)) {
            (subtotal - descuentoPromocionables) * DESCUENTO_POR_FECHA
        } else {
            0.0
        }

    val total: Double
        get() = subtotal - descuentoPromocionables - descuentoPorFecha

    // Desestructuración: val (cliente, productos, total) = pedido
    operator fun component1(): Cliente = cliente
    operator fun component2(): Map<Producto, Int> = productos
    operator fun component3(): Double = total

    // Combina dos pedidos del mismo cliente en uno nuevo: suma las cantidades y recalcula el total.
    // Los pedidos originales no cambian y la fecha es la del primero (el de la izquierda).
    operator fun plus(otro: Pedido): Pedido {
        require(cliente == otro.cliente) {
            "Solo se pueden combinar pedidos del mismo cliente (${cliente.nombre} y ${otro.cliente.nombre})."
        }
        val combinados = productos.toMutableMap()
        otro.productos.forEach { (producto, cantidad) ->
            combinados[producto] = (combinados[producto] ?: 0) + cantidad
        }
        return Pedido(cliente, combinados, fecha, estado)
    }

    fun resumen(): String = buildString {
        appendLine("   Pedido #$id | ${cliente.nombre} | $fecha | ${estado.etiqueta}")
        productos.forEach { (producto, cantidad) ->
            appendLine("     - ${producto.nombre} x$cantidad = ${formatearPrecio(producto.precio * cantidad)}")
        }
        appendLine("     Subtotal:               ${formatearPrecio(subtotal)}")
        appendLine("     Desc. promocionables:  -${formatearPrecio(descuentoPromocionables)}")
        appendLine("     Desc. por fecha:       -${formatearPrecio(descuentoPorFecha)}")
        append("     TOTAL:                  ${formatearPrecio(total)}")
    }

    companion object {
        private var ultimoId = 0
        private fun nuevoId(): Int = ++ultimoId
    }
}
