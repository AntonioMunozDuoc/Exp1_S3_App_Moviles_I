package utilidades

import anotaciones.Promocionable
import modelo.Producto
import java.time.LocalDate
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

// Reglas de promoción: un periodo de fechas para el pedido y los productos marcados con @Promocionable.

// Periodo "Noviembre de la Pizza", definido como un rango de fechas.
val PERIODO_PROMOCION: ClosedRange<LocalDate> = LocalDate.of(2026, 11, 1)..LocalDate.of(2026, 11, 30)

// Descuento al total del pedido cuando su fecha está dentro del periodo.
const val DESCUENTO_POR_FECHA = 0.10

fun estaEnPeriodoPromocion(fecha: LocalDate): Boolean = fecha in PERIODO_PROMOCION

// Consulta con reflection si la clase del producto tiene la anotación @Promocionable.
fun Producto.esPromocionable(): Boolean = this::class.hasAnnotation<Promocionable>()

// Porcentaje de descuento definido en la anotación (0.0 si el producto no es promocionable).
fun Producto.descuentoPromocional(): Double =
    this::class.findAnnotation<Promocionable>()?.descuento ?: 0.0

fun Producto.descripcionPromocion(): String =
    this::class.findAnnotation<Promocionable>()?.descripcion ?: ""
