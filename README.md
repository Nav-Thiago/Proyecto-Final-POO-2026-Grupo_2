# Proyecto-Final-POO-2026

# Proyecto: Cazador de Zombies (Nombre a definir)

## 1. Integrantes del Equipo 

- Kornijew, Mirko 
- Navarro, Thiago 
- Perez, Joaquin 
- Oyarzo, Emiliano

## 2. Dominio y Alcance del Sistema 

### Descripción del problema
Se busca desarrollar una aplicación de escritorio de un videojuego de tipo **roguelike**, específicamente enfocado en la exploración de mazmorras y la acción frenética, ambientado en un mundo de apocalipsis zombie. El jugador tomará el control de un sobreviviente al apocalipsis que debe eliminar zombies en diferentes salas para sobrevivir.

### Objetivo del Sistema
El sistema será un juego funcional y extensible. El diseño aplicará rigurosamente la arquitectura **MVC**, los principios de la Programación Orientada a Objetos (POO) y patrones de diseño para facilitar la incorporación de nuevos personajes, habilidades, mapas y persistencia de datos.
 El objetivo del juego es eliminar al jefe final. Para esto debe encontrar una llave por cada nivel para acceder al siguiente. Al llegar a una determinada cantidad de salas completadas, se encuentra con un jefe al que tiene que eliminar.

### Idea general
El jugador es un sobreviviente de un apocalipsis zombie caminando por un mapa en 2D de perspectiva top-down donde aparecen justamente zombies que le quieren quitar vidas y él se tiene que defender, para eso cuenta con una pistola y un cuchillo. La pistola tiene balas y cooldown, en cada nivel aparece con una cantidad default de balas y se puede ir encontrando más de las mismas, además de otros potenciadores o power ups como por ejemplo de velocidad o cooldown. También podría encontrarse otras armas mejores que sean más eficientes y faciliten la jugabilidad. Todo esto el jugador lo utiliza para derrotar a los enemigos de cada etapa, y al llegar a la última debe derrotar a un jefe final para pasar el nivel. 
 El juego cuenta con varios escenarios en donde se desarrollaran los niveles del juego y también con variedad en cuanto a los enemigos como posibles jefes que tendrán mecánicas propias que los hagan únicos a comparación de los enemigos comunes que se encontrarán dispersos por distintas zonas del mapa.

### Funcionalidades Principales (Features)

**Gestión de Personajes y Clases:**
- Control de personajes con atributos propios (Vida, Ataque).
- Variedad de armas con formas de disparo diferentes.

**Interacciones con el entorno:**
- El jugador inicia con dos armas, una es para que tenga capacidad de hacer daño a los zombies a larga distancia, hay que tener cuidado porque estas balas tienen un límite, fomentando al jugador a usar cada bala con cuidado y con consciencia. La otra arma es un cuchillo con el que va a poder golpear a melee (ataque cuerpo a cuerpo)
- El jugador puede llegar a conseguir potenciadores o power-ups que se encuentran dispersos alrededor del mapa, estos incrementan sus stats ya sea de velocidad, cooldown del arma, más balas, etc. Beneficiando al jugador y dándole ventaja contra la situación. 
- Cuando entran a las salas donde están los enemigos las puertas se cierran, obligando al jugador a eliminarlos a todos para poder irse.

 **Mecánicas de Juego:**
- Si los zombies atacan al jugador, le quitan 1 corazón de 5 que representa su vida.
- Ciertos enemigos al ser eliminados dejan caer balas o power-ups.
- Hay salas donde hay cofres que tienen armas dentro o una tienda donde se puedan comprar armas, vida o power ups.

**Interfaz Gráfica (IGU):**
- Visualización del mapa (bosque, edificios, etc.), en perspectiva top-down.
- Indicadores visuales de estado (barra de vida, cantidad de munición, power ups activos).
- Botón para iniciar la partida.

**Creación de enemigos:**
- Oleadas de una cantidad aleatoria de zombies dependiendo la mazmorra donde se encuentre.

**Persistencia de datos:**
- Guardado y consulta de mejores puntuaciones (High Scores) en una base de datos SQLite.

### Diagramas de Diseño

#### **Diagrama de Clases UML (Conceptual)**
![imagen](src/Resources/imagenes/DiagramadeClase-POO.drawio.png)


#### **Prototipo de la IGU (Wireframe)**

*Idea gráfica*

![Imagen 1](src/Resources/imagenes/imagen1.png) 



![Imagen 2](src/Resources/imagenes/imagen2.png) 



![Imagen 3](src/Resources/imagenes/imagen3.png)     
*Mapa del bosque (posible diseño)*


![Imagen 4](src/Resources/imagenes/imagen4.png)     
*Mapa del edificio (posible diseño)*


![Imagen 5](src/Resources/imagenes/imagen5.png)     
*Jefe final / por nivel -> Policia SWAT*


![Imagen 6](src/Resources/imagenes/imagen6.png)     
*Cientifico loco*


![Imagen 7](src/Resources/imagenes/imagen7.png)     
*Zombie gigante*


## 4. Stack Tecnológico 

- **Lenguaje:** Java 26
- **IDE:** Visual Studio Code & Intellij IDEA
- **Base de Datos:** SQL Lite
- **Framework de IGU:** Java Swing
- **Control de Versiones:** Git y GitHub Classroom

## Herencia y Polimorfismo

## Herencia
**Entity (abstracta) --> Character:**
- Todo lo que existe en el mapa (jugador, zombies, etc.) tiene una posición en el mapa (x, y) y un tamaño (width, height), y necesita poder calcular su rectángulo de colisión. En lugar de repetir esos métodos y atributos en cada clase, se los guarda una vez en Entity.

**Character (abstracta, extiende de Entity) --> Player, Enemy:**
-  El jugador y los enemigos comparten algo que las demás clases no tienen, vida y la capacidad de recibir daño y morir. Por eso se guardó en una clase, de tal manera que no se reutilize código.

**Enemy (abstracta, extiende Character) --> ZombieNormal:**
- Todo enemigo persigue al jugador de la misma forma (update (Player target)), lo que ya está escrito una vez en Enemy, además que ataca por contacto con un mecanismo de cooldown. Lo único que cada tipo de zombie define es cuanto daño hace si ataque, por eso el método attack() es abstracto.

**Implementaciones futuras:**
- Se van a agregar más tipos de zombies que hereden de Enemy, solo cambiando los valores que se pasen por el constructor y su propia versión de attack().
- Se va a crear una clase padre abstracta Weapon para que otros tipos de armas puedan heredar de ella, todas son un arma, pero cada una define como dispara.
- Se va a crear una clase padre abstracta PowerUp, con subclases de distintos tipos de power-up, como la munición o una mejora de velocidad, todas son power-up que el jugador puede recoger del piso, pero cada una define su comportamiento.

## Polimorfismo 
**Enemy.attack():**
- Enemy.update() llama a attack() sin saber ni importarle que tipo de zombi es, más adelante cuando se agreguen más tipos de zombies esto va a ser más visible. Pero el mecanismo ya es polimórfico, cuando se agregue otro tipo de zombie, Enemy.update() no va a tener que cambiar ninguna línea de código para que el ataque sea distinto. El código que lo usa no sabe cuantas variantes existen, y no le hace falta saberlo.

**Implementaciones futuras:**
- Al agregarse más tipos de zombies, el método attack() se va a seguir utilizando. Cuando hayan varios tipos de zombies en la misma partida, Enemy.update() va a llamar a attack() sobre zombies mezclados en la misma lista, sin preguntar de qué tipo es cada uno, y cada uno va a responder con su propio daño.
- Cuando se agreguen más tipos de armas, el controller va a poder llamar algo como armaActual.disparar()... sin saber cuál arma tiene equipada el jugador, cada una va a crear distintos proyectiles a través del mismo llamado.
- Cuando se agreguen los power-up, se va a utilizar un método como aplicar(Player), cuando el jugador toque el power-up en el piso, el código que lo recoge va a llamar a powerUp.aplicar(Player) sin importarle que tipo de power-up es, cada uno hace su efecto propio a través del mismo método. 