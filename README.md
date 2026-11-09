# Pizzería - Creando soluciones con Kotlin (PMY2201)

**Alumno:** Antonio Muñoz
**Asignatura:** Desarrollo App Móviles I (PMY2201) - Duoc UC
**Evaluación:** Actividad sumativa Experiencia 1, Semana 3

Aplicación de consola en Kotlin para la gestión de una pizzería (clientes, productos y pedidos). Incorpora validaciones con **Regex** y **Ranges**, una **anotación personalizada** leída con **Reflection**, **sobrecarga de operadores**, **desestructuración** y evaluación de **igualdad de objetos**.

## Tecnologías

| Tecnología | Uso en el proyecto |
| --- | --- |
| Kotlin 2.1 (JVM) | Lenguaje de toda la aplicación. |
| Gradle (Kotlin DSL) | Construcción del proyecto y dependencia `kotlin-reflect`. |
| IntelliJ IDEA | Entorno de desarrollo y ejecución. |

## Cumplimiento de los requerimientos

| Requerimiento | Dónde se implementa |
| --- | --- |
| Validar correo con Regex y volver a pedirlo si es incorrecto | `validarCliente` en `utilidades/Validaciones.kt`; `pedirCliente` en `Main.kt` repite solo los campos con error. |
| Formatear teléfonos como `+XX (XXX) XXX-XXXX` | `formatearTelefono` en `utilidades/Validaciones.kt`. |
| Rango de fechas de promoción con descuento al total | `PERIODO_PROMOCION` y `estaEnPeriodoPromocion` en `utilidades/Promociones.kt`; el descuento se aplica en `Pedido.descuentoPorFecha`. |
| Cantidad dentro del rango 1-100 con reingreso | `RANGO_CANTIDAD` y `validarCantidad` en `utilidades/Validaciones.kt`; `pedirCantidad` en `Main.kt`. |
| Anotación personalizada `@Promocionable` | `anotaciones/Promocionable.kt`, aplicada en `PizzaGourmet` y `Combo` (`modelo/Producto.kt`). |
| Identificar productos promocionables en el procesamiento de pedidos | `esPromocionable` y `descuentoPromocional` (`utilidades/Promociones.kt`) leen la anotación con reflection; `Pedido.descuentoPromocionables` las usa al calcular el total. |
| Listar métodos y propiedades con Reflection | `listarMiembros` en `utilidades/Reflexion.kt`, usada con `Cliente`, `Producto` y `Pedido`. |
| Operador `+` para combinar pedidos | `Pedido.plus` en `modelo/Pedido.kt`. |
| Operador `==` para comparar productos por nombre y categoría | `Producto.equals` en `modelo/Producto.kt`. |
| Desestructurar `Cliente` y `Pedido` | `component1..3` en `modelo/Cliente.kt` y `modelo/Pedido.kt`. |
| `equals()` y `hashCode()` en `Cliente` y `Producto` | `modelo/Cliente.kt` y `modelo/Producto.kt`; `utilidades/Registro.kt` evita duplicados al registrar. |
| Resumen en consola e integración en `Main.kt` | `mostrarPedidosCombinados`, `mostrarDesestructuracion`, `mostrarPromocionables` y `mostrarIgualdad` en `Main.kt`. |

## Estructura del proyecto

```
PizzeriaKotlin/
├── build.gradle.kts
├── settings.gradle.kts
├── src/main/kotlin/
│   ├── Main.kt
│   ├── anotaciones/
│   │   └── Promocionable.kt
│   ├── modelo/
│   │   ├── Categoria.kt
│   │   ├── Cliente.kt
│   │   ├── Producto.kt
│   │   └── Pedido.kt
│   └── utilidades/
│       ├── Entrada.kt
│       ├── Precios.kt
│       ├── Promociones.kt
│       ├── Reflexion.kt
│       ├── Registro.kt
│       ├── Texto.kt
│       └── Validaciones.kt
└── capturas/
    └── salida-consola.txt
```

## Modelo y reglas de negocio

- **Productos:** `Producto` es la clase base (nombre, categoría y precio). De ella heredan `Pizza`, `PizzaGourmet`, `Combo`, `Bebida` e `Ingrediente`. Dos productos son iguales si tienen el mismo nombre y la misma categoría, aunque el precio sea distinto.
- **Clientes:** dos clientes son iguales si coinciden el nombre y el correo, sin distinguir mayúsculas ni espacios sobrantes.
- **Pedidos:** guardan un `Map<Producto, Int>` con las cantidades. `pedido1 + pedido2` crea un pedido nuevo (solo si son del mismo cliente), suma las cantidades y recalcula el total.
- **Descuentos:**
  - Los productos con `@Promocionable` descuentan el porcentaje de su anotación (15% en `PizzaGourmet` y 20% en `Combo`).
  - Si la fecha del pedido está dentro del periodo de promoción (1 al 30 de noviembre de 2026), se descuenta además un 10% al total.

## Instalación y uso

Requisitos: **IntelliJ IDEA** y **JDK 17 o superior**.

1. Abrir IntelliJ y seleccionar **File > Open**, eligiendo la carpeta `PizzeriaKotlin`. IntelliJ importa el proyecto con Gradle e instala `kotlin-reflect` automáticamente.
2. Abrir `src/main/kotlin/Main.kt` y ejecutar la función `main` con el botón verde.

Por defecto el programa usa datos simulados que incluyen errores a propósito, para mostrar las validaciones y el reingreso sin escribir nada.

### Cómo probar la aplicación

1. **Datos simulados:** ejecutar `main` y revisar la salida; muestra correos y teléfonos inválidos que se vuelven a pedir, cantidades fuera de rango y el resumen final.
2. **Datos propios:** en **Run > Edit Configurations > Program arguments** escribir `--interactivo` y ejecutar de nuevo; el programa pide el nombre, correo, teléfono, dirección y cantidad por consola.
3. **Validaciones:** probar `algo@` como correo, `12345` como teléfono y `0`, `101` o `abc` como cantidad.
4. **Igualdad:** en la sección final, comprobar que "CAMILA SOTO" con el mismo correo se rechaza como duplicado y que `Pepperoni` como ingrediente sí se registra (otra categoría).

## Salida de ejemplo

El archivo `capturas/salida-consola.txt` contiene la salida completa de una ejecución con los datos simulados.
