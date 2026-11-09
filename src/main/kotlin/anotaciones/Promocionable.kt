package anotaciones

// Marca las clases de producto (pizzas o combos) que califican para promociones especiales.
// `descuento` va en decimal (0.15 = 15%). Usa RUNTIME para poder leerla con reflection.
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Promocionable(
    val descuento: Double,
    val descripcion: String = ""
)
