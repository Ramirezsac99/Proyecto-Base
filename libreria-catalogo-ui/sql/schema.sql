

CREATE DATABASE IF NOT EXISTS libreria_catalogo
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE libreria_catalogo;

-- ---------------------------------------------------------------------
-- Tabla: libros
--
-- Decisiones de diseño:
--   - id: INT AUTO_INCREMENT, PK. Es el "identificador unico que el
--     sistema asigna automaticamente" que pide el enunciado.
--   - titulo VARCHAR(150): los titulos de libros pueden ser largos
--     (subtitulos, ediciones especiales). 150 da margen sin exagerar.
--   - autor VARCHAR(100): nombre completo de una sola persona, 100
--     caracteres es de sobra incluso para nombres compuestos largos.
--   - categoria VARCHAR(50): el enunciado dice explicitamente que es
--     "texto libre" (no hay catalogo cerrado de generos), asi que NO
--     se modela como tabla separada ni como ENUM. 50 caracteres alcanza
--     para "Novela", "Ciencia Ficcion", "Historia", etc. Se permite
--     NULL porque el enunciado no dice que sea obligatoria, a diferencia
--     de titulo y autor que si son obligatorios por regla de negocio.
--   - precio DECIMAL(8,2): dinero NUNCA se guarda en FLOAT/DOUBLE por
--     los errores de redondeo binario. DECIMAL(8,2) permite hasta
--     999,999.99 -- mas que suficiente para el precio de un libro.
--     CHECK (precio > 0) refleja la regla "el precio debe ser mayor
--     a cero".
--   - existencias INT: cantidad de ejemplares, siempre entero, nunca
--     negativo -> CHECK (existencias >= 0). INT (no TINYINT/SMALLINT)
--     porque una libreria grande podria tener miles de copias de un
--     mismo titulo.
--   - anio_publicacion SMALLINT: un año cabe perfecto en SMALLINT
--     (rango -32768 a 32767), no hace falta INT completo. No se le
--     puso CHECK contra el año actual en la base de datos porque ese
--     valor cambia con el tiempo y MySQL/MariaDB no evalua funciones
--     como YEAR(CURDATE()) dentro de un CHECK de columna de forma
--     portable -- esa regla ("no libros del futuro") se valida en la
--     aplicacion (UI), antes de llamar al DAO.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS libros (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    titulo             VARCHAR(150)   NOT NULL,
    autor              VARCHAR(100)   NOT NULL,
    categoria          VARCHAR(50)    NULL,
    precio             DECIMAL(8,2)   NOT NULL,
    existencias        INT            NOT NULL DEFAULT 0,
    anio_publicacion   SMALLINT       NOT NULL,
    es_best_seller     BOOLEAN        NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_libros_precio       CHECK (precio > 0),
    CONSTRAINT chk_libros_existencias  CHECK (existencias >= 0)
);

-- Datos de ejemplo (opcional, util para probar la UI de una vez)
INSERT INTO libros (titulo, autor, categoria, precio, existencias, anio_publicacion, es_best_seller) VALUES
    ('Cien años de soledad', 'Gabriel Garcia Marquez', 'Novela',  145.00, 12, 1967),
    ('Clean Code',           'Robert C. Martin',       'Tecnico', 220.50, 5,  2008),
    ('El principito',        'Antoine de Saint-Exupery','Infantil', 85.00, 0, 1943);
