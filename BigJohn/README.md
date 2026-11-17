# Big John – Ventas y Garantías (Java Swing)

Un solo proyecto con tres módulos (paquetes) y datos en memoria.

## Cómo ejecutarlo
Requiere Java 17 o superior.

**Con Maven:** `mvn compile exec:java`

**Sin Maven:**
```
javac -encoding UTF-8 -d out $(find src -name "*.java")     # Linux / Mac
java -cp out com.bigjohn.Main
```
En Windows: `dir /s /b src\*.java > fuentes.txt` y luego `javac -encoding UTF-8 -d out @fuentes.txt`.
En IntelliJ / Eclipse / NetBeans: abrir la carpeta y ejecutar `com.bigjohn.Main`.

## Módulos
| Paquete | Clases | Quién depende de quién |
|---|---|---|
| `comun` | Persona, Usuario, UsuarioServicio, Asesor, Administrador, Tienda, Sucursal, Producto, RelojSimulado, Repositorio, RepositorioMemoria | No depende de nadie |
| `ventas` | Venta, DetalleVenta, Pago, MetodoPago, PagoConBono (interfaz), Factura, Inventario, EstadoVenta, VentaServicio | Usa `comun` |
| `garantias` | SolicitudGarantia, Bono, EstadoGarantia, ResolucionGarantia, ResultadoRevision, RevisionMedellin, GarantiaServicio | Usa `comun` y `ventas`. Implementa `PagoConBono` |
| `ui` | VentanaPrincipal, PanelVentas, PanelGarantias | Solo habla con los servicios |

`ventas` no conoce a `garantias`: define la interfaz `PagoConBono` y `garantias` la implementa. `Main` los conecta (`ventas.setPagoConBono(garantias)`). Así cada integrante puede programar su módulo por separado.

## Equivalencias con el diagrama de garantías
| Diagrama de garantías | En el código |
|---|---|
| Cliente | `Usuario` (comun) |
| Empleado | `Asesor` / `Administrador` (comun) |
| Tienda | `Tienda` / `Sucursal` (comun) |
| Producto | `Producto` (comun) |
| Compra, DetalleCompra | `Venta`, `DetalleVenta` (ventas) |
| Devolucion | `SolicitudGarantia` (garantias) |
| Reembolso | `ResolucionGarantia` y `Bono` (garantias) |

## Flujo de la garantía
1. La garantía se puede pedir hasta **1 mes después de la compra** (`GarantiaServicio.MESES_PLAZO`). La pestaña **Garantías** muestra la fecha límite de la venta elegida; pasada esa fecha no se acepta la solicitud.
2. Se elige la venta y el producto, se escribe el defecto y se registra la solicitud.
3. La solicitud se envía a **Medellín** (sucursal principal) y queda "En revisión".
4. La respuesta llega **8 días después** (`GarantiaServicio.DIAS_RESPUESTA`). Para simularlo, use **+8 días** en la barra de arriba (también hay **+1 día** y **+30 días**, útil para probar el plazo de un mes).
5. Medellín decide (`RevisionMedellin`, al azar): no aplica garantía, prenda nueva, reparación (se vuelve a coser) o bono por el valor de la prenda. Los porcentajes se cambian en esa clase.

## Bono como medio de pago
- El bono tiene un código (`BONO-0001`) que se escribe en la pestaña **Ventas**; el botón **Verificar bono** muestra su valor.
- Se verifica que **exista** y que **no haya sido redimido**. Si no cumple, la venta no se registra y no cambia nada (ni stock ni bono).
- El bono se usa **una sola vez y completo**:
  - Si vale menos que la compra, paga hasta su valor y el resto se paga con el método elegido (la venta queda con dos pagos).
  - Si vale más que la compra, paga todo y el excedente no se devuelve (el sistema pide confirmación).
- Al registrar la venta, el bono queda redimido con la fecha, la sucursal y el número de venta.
- Para una demo rápida: `java -cp out com.bigjohn.Main bono` hace que Medellín siempre responda con un bono.

## Usuarios nuevos
En la pestaña **Ventas**, el botón **Nuevo usuario...** abre un formulario. Nombre y documento son obligatorios, el documento no se puede repetir y el correo, si se escribe, debe tener formato válido. Al guardar, el usuario queda seleccionado.

## Cambios frente al diagrama de clases de ventas
- `Sucursal`: se agregaron `nombre` y `principal` (Medellín es la principal), y guarda su administrador y sus asesores.
- `Asesor` y `Administrador` están en `comun`, porque los usan los dos procesos.
- `DetalleVenta`: se agregaron `cantidad` y `precioUnitario`. `Factura`: se agregaron `numero`, `fecha` y `total`.
- `Inventario` guarda el stock de cada producto por sucursal.
- Una solicitud de garantía cubre **un producto** de una venta (no hay `DetalleDevolucion`).

## Para después
- Base de datos: escribir otra implementación de `Repositorio` (por ejemplo con JDBC y H2) y cambiarla en `Main`.
- Vencimiento del bono y saldo parcial: hoy el bono no vence y no deja saldo.
- Clave del usuario: es opcional; si el usuario va a iniciar sesión, habría que guardarla cifrada.
