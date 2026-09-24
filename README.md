# Proyecto Individual — CRUD con Swing + Maven multi-módulo + MySQL/MariaDB

**Duración: 1 semana, individual, fuera de clase.**

## 1. Contexto

En `clase05-jdbc-con-maven`, `clase07-*` y `clase08-productos-crud` construiste
CRUDs de consola: `Connection` → `PreparedStatement` → `ResultSet`, DAO con
las 4 operaciones, todo persistido en MySQL. Ese es el motor que ya sabes
construir.

Esta semana das dos saltos a la vez:

1. **De consola a interfaz gráfica**: en vez de un menú con `Scanner`,
   construyes una ventana Swing con tabla, formulario y botones.
2. **De un solo proyecto a un proyecto Maven multi-módulo**: tu lógica de
   datos (modelo + DAO) vive en un **módulo librería** independiente, que tu
   módulo de interfaz gráfica **consume como dependencia** — no como código
   copiado y pegado.

No hay tabla de base de datos ya creada para ti. Tu instructor te va a asignar
una de **tres variantes de dominio** (A, B o C, ver carpeta `variantes/`),
descritas en términos de negocio, no en SQL. **Diseñar el esquema es parte de
la evaluación.**

## 2. Objetivo de aprendizaje

Al terminar deberías poder, sin ver ningún ejemplo:

1. Traducir una descripción conceptual de un dominio a una tabla SQL
   (tipos de dato, longitudes, `NOT NULL`, claves) y justificar tus
   decisiones.
2. Estructurar un proyecto Maven **multi-módulo**: un módulo tipo librería
   (`.jar`) instalado en el repositorio local con `mvn install`, y un módulo
   de aplicación que declara esa librería como `<dependency>` en su `pom.xml`.
3. Construir una interfaz Swing (`JFrame`, `JTable`, formulario) que llame al
   DAO de tu librería para listar, crear, actualizar y eliminar.
4. Mantener la interfaz gráfica **desacoplada** de JDBC: la clase de UI no
   debería importar `java.sql.*` — solo llama a métodos del DAO/servicio de
   tu librería.

## 3. Prerequisitos

- MySQL/MariaDB corriendo local, con acceso para crear una base de datos
  nueva (la que uses para tu variante).
- JDK 11+ y Maven instalados.
- Haber completado (o al menos entendido) `clase05-jdbc-con-maven` y
  `clase08-productos-crud` — el patrón DAO es el mismo, solo cambia quién lo
  llama (una UI en vez de un menú de consola).

## 4. Enunciado general

Tu instructor te asignó una variante (A, B o C). Léela en `variantes/`. Ahí
encontrarás **solo la descripción conceptual** de la entidad: qué datos
guarda, qué reglas de negocio tiene. **No hay ningún `CREATE TABLE`.**

Con esa descripción debes:

1. Diseñar tú mismo el esquema de la tabla (nombre de columnas, tipos,
   longitudes, `NOT NULL`, clave primaria autoincremental). Documenta tu
   diseño en un archivo `sql/schema.sql` dentro de tu propio proyecto —
   ese script es tu entregable, no algo que se te da.
2. Construir un proyecto Maven multi-módulo con la estructura de la sección 5.
3. Implementar el CRUD completo (Create, Read, Update, Delete) accesible
   desde una ventana Swing.

### Requisitos técnicos obligatorios

| Requisito | Detalle |
|---|---|
| Maven multi-módulo | Un módulo `*-core` (librería: modelo + DAO) empaquetado como `jar` e instalado con `mvn install`. Un módulo `*-ui` que lo declara como `<dependency>` en su `pom.xml` — **no copies las clases del DAO al módulo UI.** |
| Persistencia real | MySQL/MariaDB vía JDBC + patrón DAO (igual que `clase08`), no en memoria. |
| Interfaz Swing | Una ventana con `JTable` para listar, un formulario (campos de texto según tu entidad) para crear/actualizar, y un botón de eliminar con confirmación (`JOptionPane`). |
| CRUD completo | Las 4 operaciones deben funcionar desde la UI, no solo desde el DAO. |
| Validación antes de tocar la BD | Igual que en `clase08`: campos obligatorios no vacíos, números en su rango válido, antes de llamar al DAO. |
| Manejo de errores sin crash | Ninguna `SQLException` debe tirar la aplicación — se muestra en un `JOptionPane` de error, sin exponer el stacktrace completo al usuario. |
| Esquema propio | Tu `sql/schema.sql`, escrito por ti a partir de la descripción conceptual de tu variante. |

### ¿Por qué un módulo librería separado?

Es la misma idea de "separar responsabilidades" que vas a formalizar más
adelante con SOLID: tu lógica de acceso a datos no debería saber que existe
Swing, y tu interfaz gráfica no debería saber que existe JDBC. Al ponerlas en
módulos Maven distintos, el compilador te obliga a respetar esa separación —
si te equivocas e intentas usar `ResultSet` en la UI, no vas a poder porque
ese módulo ni siquiera tiene el driver de MySQL como dependencia directa.

## 5. Estructura de proyecto esperada

```
<tu-proyecto>/                        (pom.xml "padre", packaging=pom)
├── pom.xml
├── <tu-proyecto>-core/               (módulo librería)
│   ├── pom.xml                       (packaging=jar)
│   └── src/main/java/edu/umg/programacion2/proyecto/
│       ├── modelo/<Entidad>.java
│       └── dao/<Entidad>DAO.java
└── <tu-proyecto>-ui/                 (módulo aplicación)
    ├── pom.xml                       (packaging=jar, depende de <tu-proyecto>-core)
    ├── sql/schema.sql                (tu diseño, no un archivo dado)
    └── src/main/java/edu/umg/programacion2/proyecto/
        ├── MainUI.java               (lanza la ventana)
        └── ui/VentanaPrincipal.java  (JFrame con JTable + formulario)
```

Referencia de multi-módulo Maven (`<modules>` en el `pom.xml` padre,
`<dependency>` del `-ui` hacia el `-core`): puedes preguntarle a la IA cómo se
declara un `pom.xml` padre con módulos — eso es sintaxis de Maven, no la
lógica de tu proyecto (ver sección 8).

### Contrato del DAO (adapta `<Entidad>` a tu variante)

```java
public class <Entidad>DAO {
    public <Entidad> crear(<Entidad> item) throws SQLException { ... }
    public List<<Entidad>> listarTodos() throws SQLException { ... }
    public Optional<<Entidad>> buscarPorId(int id) throws SQLException { ... }
    public boolean actualizar(<Entidad> item) throws SQLException { ... }
    public boolean eliminar(int id) throws SQLException { ... }
}
```

Mismo contrato que viste en `clase08`, viviendo ahora en el módulo `-core`.

## 6. Checklist de entrega

- [ ] `sql/schema.sql` propio, coherente con la descripción conceptual de tu
      variante (tipos, `NOT NULL`, PK autoincremental).
- [ ] Proyecto Maven con **dos módulos reales** (`-core` y `-ui`), el padre
      con packaging `pom` y `<modules>`.
- [ ] `-core` compila e instala (`mvn install`) sin depender de Swing.
- [ ] `-ui` declara `-core` como `<dependency>` — no hay clases DAO
      duplicadas en `-ui`.
- [ ] Ventana Swing: listar (`JTable`), crear, actualizar, eliminar (con
      confirmación) — las 4 operaciones funcionan contra MySQL/MariaDB.
- [ ] Validación de campos antes de llamar al DAO.
- [ ] Ningún `catch` vacío; errores mostrados al usuario sin stacktrace.
- [ ] Todo el SQL usa `PreparedStatement`.
- [ ] Puedes explicar, para cualquier parte del código, por qué está en
      `-core` o en `-ui`.

## 7. Criterios de evaluación

| Criterio | % |
|---|---|
| Esquema propio bien diseñado (tipos, NOT NULL, PK) y justificado | 15% |
| Separación real en dos módulos Maven (`-core` sin Swing, `-ui` consumiendo `-core` como dependencia) | 20% |
| CRUD completo y funcional desde la interfaz Swing (las 4 operaciones) | 30% |
| `PreparedStatement` + manejo de `SQLException` sin exponer stacktraces ni catch vacíos | 15% |
| Validación de entrada antes de tocar la BD | 10% |
| Puede explicar cualquier decisión de diseño cuando se le pregunta | 10% |

## 8. Cómo usar la IA en este proyecto

Permitido y esperado para **agilizar sintaxis**, por ejemplo:

- "¿Cómo declaro un `pom.xml` padre con dos módulos en Maven?"
- "¿Cómo hago que un `JTable` se refresque después de un `INSERT`?"
- "¿Cómo capturo el evento de doble clic en una fila de `JTable`?"

**No permitido**: pegar la descripción conceptual de tu variante y pedir que
te genere el esquema SQL o el DAO completo. Ese diseño es tuyo — dos de estas
mejoras y tu capacidad de defenderlas en el examen parcial dependen de que
las hayas escrito y entendido tú.

## 9. Cronograma sugerido (1 semana)

| Día | Bloque |
|---|---|
| 1 | Leer tu variante, diseñar `sql/schema.sql`, crear la BD y probarla con datos de ejemplo. |
| 2 | Armar el `pom.xml` padre + los dos módulos vacíos, verificar que `-ui` compila contra `-core` con `mvn install`. |
| 3 | En `-core`: modelo + DAO completo (las 4 operaciones), probado desde un `main` de consola temporal. |
| 4–5 | En `-ui`: ventana Swing — listar en `JTable`, formulario de creación. |
| 6 | Actualizar y eliminar (con confirmación) desde la UI. |
| 7 | Pulir validaciones y manejo de errores; prueba end-to-end completa. |

## 10. Siguiente paso: examen parcial

Este proyecto es la base de dos mejoras que vas a implementar en el examen
parcial (2 horas hábiles). Cuáles te tocan depende del último dígito de tu
carné — ver `examen-parcial-mejoras.md`. Conviene que termines el CRUD base
con margen antes del parcial: ese día no hay tiempo para terminar el
proyecto base, solo para extenderlo.
