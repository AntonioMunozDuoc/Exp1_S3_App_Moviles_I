package modelo

// Categorías del menú; las usa Producto para saber si dos productos son el mismo.
enum class Categoria(val etiqueta: String) {
    PIZZA("Pizza"),
    BEBIDA("Bebida"),
    INGREDIENTE("Ingrediente")
}
