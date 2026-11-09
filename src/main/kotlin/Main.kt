import modelo.Bebida
import modelo.Cliente
import modelo.Combo
import modelo.Ingrediente
import modelo.Pedido
import modelo.Pizza
import modelo.PizzaGourmet
import modelo.Producto
import utilidades.Entrada
import utilidades.PERIODO_PROMOCION
import utilidades.RANGO_CANTIDAD
import utilidades.Registro
import utilidades.descripcionPromocion
import utilidades.descuentoPromocional
import utilidades.esPromocionable
import utilidades.estaEnPeriodoPromocion
import utilidades.formatearPrecio
import utilidades.formatearTelefono
import utilidades.listarMiembros
import utilidades.validarCantidad
import utilidades.validarCliente
import java.time.LocalDate

// Datos simulados para la demostración: incluyen errores a propósito para mostrar la validación y el reingreso.
private val ENTRADA_SIMULADA = listOf(
    "Camila Soto",          // nombre
    "camila.soto@@mail",    // correo inválido
    "12345",                // teléfono inválido
    "Av. Siempre Viva 742", // dirección
    "camila.soto@mail",     // correo inválido (sin extensión)
    "56 912 345 6789",      // teléfono válido
    "camila.soto@mail.cl",  // correo válido
    "0",                    // cantidad fuera de rango
    "101",                  // cantidad fuera de rango
    "dos",                  // cantidad no numérica
    "2"                     // cantidad válida
)

// Etiqueta con que se pide cada campo del cliente en la consola.
private val ETIQUETAS_CLIENTE = mapOf(
    "nombre" to "Nombre",
    "correo" to "Correo electrónico",
    "telefono" to "Teléfono",
    "direccion" to "Dirección"
)

private fun titulo(texto: String) {
    println("\n" + "=".repeat(64))
    println("  $texto")
    println("=".repeat(64))
}

// Pide los datos del cliente y vuelve a pedir solo los campos con error, como hace un formulario.
fun pedirCliente(entrada: Entrada): Cliente {
    val valores = ETIQUETAS_CLIENTE.mapValues { (_, etiqueta) -> entrada.leer("   $etiqueta:") }.toMutableMap()

    while (true) {
        val errores = validarCliente(valores.getValue("nombre"), valores.getValue("correo"), valores.getValue("telefono"), valores.getValue("direccion"))
        if (errores.isEmpty()) {
            break
        }
        errores.forEach { (campo, mensaje) ->
            println("   [ERROR] $mensaje")
            valores[campo] = entrada.leer("   ${ETIQUETAS_CLIENTE.getValue(campo)}:")
        }
    }

    return Cliente(
        nombre = valores.getValue("nombre").trim(),
        correo = valores.getValue("correo").trim(),
        // Ya está validado, así que el formateo siempre devuelve un valor.
        telefono = formatearTelefono(valores.getValue("telefono"))!!,
        direccion = valores.getValue("direccion").trim()
    )
}

// Repite la pregunta hasta que la cantidad sea un entero dentro del rango permitido.
fun pedirCantidad(entrada: Entrada, producto: Producto): Int {
    while (true) {
        val texto = entrada.leer("   Cantidad de pizzas ${producto.nombre}:")
        val errores = validarCantidad(texto)
        if (errores.isEmpty()) {
            return texto.trim().toInt()
        }
        println("   [ERROR] ${errores.getValue("cantidad")}")
    }
}

fun mostrarFormatoTelefonos() {
    println("\n  Formato uniforme de teléfonos: +XX (XXX) XXX-XXXX")
    listOf("569123456789", "+56 (912) 345-6789", "569-123-456-789", "12345", "abc").forEach { original ->
        println("   '$original' -> ${formatearTelefono(original) ?: "inválido"}")
    }
}

fun mostrarPeriodoPromocion() {
    println("\n  Periodo de promoción: ${PERIODO_PROMOCION.start} a ${PERIODO_PROMOCION.endInclusive}")
    listOf(LocalDate.of(2026, 10, 31), LocalDate.of(2026, 11, 10), LocalDate.of(2026, 11, 30), LocalDate.of(2026, 12, 5))
        .forEach { fecha ->
            val resultado = if (estaEnPeriodoPromocion(fecha)) "dentro del periodo (aplica descuento)" else "fuera del periodo"
            println("   $fecha -> $resultado")
        }
}

fun mostrarElegibilidad(catalogo: List<Producto>) {
    println("\n  Productos del catálogo y su elegibilidad (anotación @Promocionable leída con reflection)")
    catalogo.forEach { producto ->
        val estado = if (producto.esPromocionable()) {
            "PROMOCIONABLE (${(producto.descuentoPromocional() * 100).toInt()}%: ${producto.descripcionPromocion()})"
        } else {
            "no promocionable"
        }
        println("   - ${producto.nombre.padEnd(15)} ${producto::class.simpleName!!.padEnd(12)} $estado")
    }
}

fun mostrarReflection() {
    println("\n  Reflection: propiedades y métodos de las clases del dominio")
    listarMiembros(Cliente::class)
    println()
    listarMiembros(Producto::class)
    println()
    listarMiembros(Pedido::class)
}

// Muestra el operador + sobre pedidos y devuelve el pedido combinado para usarlo en el resto del resumen.
fun mostrarPedidosCombinados(pedido1: Pedido, pedido2: Pedido, pedidoFueraDePeriodo: Pedido, pedidoDeOtroCliente: Pedido): Pedido {
    println("\n[1] Pedidos combinados con el operador +")
    println(pedido1.resumen())
    println()
    println(pedido2.resumen())

    val combinado = pedido1 + pedido2
    println("\n   pedido1 + pedido2 =")
    println(combinado.resumen())
    println("\n   Total combinado ${formatearPrecio(combinado.total)} | suma de totales individuales ${formatearPrecio(pedido1.total + pedido2.total)}")
    println("   (coinciden porque ambos pedidos están dentro del periodo y los descuentos son porcentuales)")

    println("\n   Mismos productos, distinta fecha:")
    println("   ${pedido1.fecha} (dentro del periodo) -> ${formatearPrecio(pedido1.total)}")
    println("   ${pedidoFueraDePeriodo.fecha} (fuera del periodo) -> ${formatearPrecio(pedidoFueraDePeriodo.total)}")

    try {
        pedido1 + pedidoDeOtroCliente
    } catch (e: IllegalArgumentException) {
        println("\n   Combinar pedidos de clientes distintos no está permitido: ${e.message}")
    }

    return combinado
}

fun mostrarDesestructuracion(cliente: Cliente, pedido: Pedido) {
    println("\n[2] Desestructuración")
    val (nombre, correo, telefono) = cliente
    println("   Cliente -> nombre: $nombre | correo: $correo | teléfono: $telefono")

    val (clientePedido, productos, total) = pedido
    println("   Pedido  -> cliente: ${clientePedido.nombre} | total: ${formatearPrecio(total)}")
    println("              productos: " + productos.entries.joinToString { (producto, cantidad) -> "${producto.nombre} x$cantidad" })
}

fun mostrarPromocionables(catalogo: List<Producto>, pedido: Pedido) {
    println("\n[3] Productos promocionables identificados por la anotación @Promocionable")
    catalogo.filter { it.esPromocionable() }.forEach { producto ->
        println("   - ${producto.nombre} (${producto::class.simpleName}): ${producto.descripcionPromocion()}")
    }
    val enElPedido = pedido.productos.keys.filter { it.esPromocionable() }.joinToString { it.nombre }
    println("   En el pedido combinado: $enElPedido -> descuento: ${formatearPrecio(pedido.descuentoPromocionables)}")
}

// Compara clientes y productos con == y ===, y prueba que los registros rechacen duplicados.
fun mostrarIgualdad(clientes: Registro<Cliente>, catalogo: Registro<Producto>, camila: Cliente, diego: Cliente, pepperoni: Producto) {
    println("\n[4] Validación de igualdad de objetos (equals y hashCode)")

    val camilaDuplicada = Cliente("  CAMILA SOTO ", "Camila.Soto@mail.cl", "+56 (900) 000-0000", "Otra dirección 123")
    val camilaOtroCorreo = Cliente("Camila Soto", "camila.otra@mail.cl", camila.telefono, camila.direccion)
    val pepperoniOtroPrecio = Pizza("pepperoni", 9990.0)
    val pepperoniIngrediente = Ingrediente("Pepperoni", 900.0)

    println("   Clientes:")
    println("   - camila == camilaDuplicada (mismo nombre y correo, distinto formato)  -> ${camila == camilaDuplicada}")
    println("   - hashCode iguales                                                     -> ${camila.hashCode() == camilaDuplicada.hashCode()}")
    println("   - camila == camilaOtroCorreo (mismo nombre, distinto correo)           -> ${camila == camilaOtroCorreo}")
    println("   - camila == diego                                                      -> ${camila == diego}")
    println("   Productos:")
    println("   - pepperoni == pepperoniOtroPrecio (mismo nombre y categoría)          -> ${pepperoni == pepperoniOtroPrecio}")
    println("   - pepperoni == pepperoniIngrediente (mismo nombre, distinta categoría) -> ${pepperoni == pepperoniIngrediente}")
    println("   - pepperoni === pepperoniOtroPrecio (misma instancia en memoria)       -> ${pepperoni === pepperoniOtroPrecio}")

    println("\n   Registros sin duplicados (usan equals y hashCode):")
    println("   - Cliente duplicado (Camila)               -> ${if (clientes.registrar(camilaDuplicada)) "registrado" else "rechazado: ya existe"}")
    println("   - Cliente con otro correo                  -> ${if (clientes.registrar(camilaOtroCorreo)) "registrado" else "rechazado: ya existe"}")
    println("   - Producto duplicado (Pepperoni)           -> ${if (catalogo.registrar(pepperoniOtroPrecio)) "registrado" else "rechazado: ya existe"}")
    println("   - Ingrediente 'Pepperoni' (otra categoría) -> ${if (catalogo.registrar(pepperoniIngrediente)) "registrado" else "rechazado: ya existe"}")
    println("   Clientes registrados: ${clientes.cantidad} | Productos en el catálogo: ${catalogo.cantidad}")
}

// Con el argumento --interactivo los datos se escriben en la consola; sin él se usan los datos simulados.
fun main(args: Array<String>) {
    val entrada = if ("--interactivo" in args) Entrada.consola() else Entrada.simulada(ENTRADA_SIMULADA)

    titulo("PIZZERIA - CREANDO SOLUCIONES CON KOTLIN (S3)")

    println("\n[PASO 1] Regex y Ranges")
    println("\n  Registro de cliente (correo y teléfono validados con Regex)")
    val camila = pedirCliente(entrada)
    println("   -> Cliente registrado: ${camila.nombre} | ${camila.correo} | ${camila.telefono}")
    mostrarFormatoTelefonos()

    // Menú de la pizzería
    val pepperoni = Pizza("Pepperoni", 8990.0, listOf("Pepperoni", "Queso mozzarella"))
    val cuatroQuesos = PizzaGourmet("Cuatro Quesos", 11990.0, listOf("Mozzarella", "Parmesano", "Gorgonzola", "Provolone"))
    val comboFamiliar = Combo("Combo Familiar", 21990.0, listOf("2 pizzas medianas", "1 bebida 1.5L"))
    val bebida = Bebida("Bebida 1.5L", 2490.0)
    val quesoExtra = Ingrediente("Queso Extra", 1200.0)

    val catalogo = Registro<Producto>()
    listOf(pepperoni, cuatroQuesos, comboFamiliar, bebida, quesoExtra).forEach { catalogo.registrar(it) }

    val diego = Cliente("Diego Muñoz", "diego.munoz@mail.cl", "+56 (987) 654-3210", "Pasaje Los Aromos 15")
    val clientes = Registro<Cliente>()
    clientes.registrar(camila)
    clientes.registrar(diego)

    println("\n  Cantidad de pizzas permitida: $RANGO_CANTIDAD")
    val cantidadPepperoni = pedirCantidad(entrada, pepperoni)
    println("   -> Cantidad aceptada: $cantidadPepperoni")
    mostrarPeriodoPromocion()

    println("\n[PASO 2] Anotaciones y Reflection")
    mostrarElegibilidad(catalogo.todos())
    mostrarReflection()

    // Pedidos de la demostración
    val pedido1 = Pedido(camila, mapOf(pepperoni to cantidadPepperoni, bebida to 1), LocalDate.of(2026, 11, 10))
    val pedido2 = Pedido(camila, mapOf(pepperoni to 1, cuatroQuesos to 1, comboFamiliar to 1), LocalDate.of(2026, 11, 12))
    val pedidoFueraDePeriodo = Pedido(camila, mapOf(pepperoni to cantidadPepperoni, bebida to 1), LocalDate.of(2026, 12, 5))
    val pedidoDeDiego = Pedido(diego, mapOf(quesoExtra to 3), LocalDate.of(2026, 11, 11))

    titulo("RESUMEN FINAL")
    val combinado = mostrarPedidosCombinados(pedido1, pedido2, pedidoFueraDePeriodo, pedidoDeDiego)
    mostrarDesestructuracion(camila, combinado)
    mostrarPromocionables(catalogo.todos(), combinado)
    mostrarIgualdad(clientes, catalogo, camila, diego, pepperoni)

    titulo("Fin de la ejecución")
}
