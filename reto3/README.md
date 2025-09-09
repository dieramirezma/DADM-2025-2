Claro, aquí tienes la guía convertida a formato Markdown.

# Aplicación de Programación para Android

## Reto: App de Tres en Raya

### Introducción

[cite\_start]El objetivo de esta tarea es crear un juego simple de tres en raya para Android, como se ilustra a la derecha. [cite: 3] [cite\_start]Utilizarás botones para representar el tablero de juego y un control de texto para mostrar el estado del juego en la parte inferior de la pantalla. [cite: 4] [cite\_start]Los botones no tendrán texto al iniciar el juego, pero cuando el usuario haga clic en un botón, mostrará una X verde. [cite: 5] [cite\_start]Luego, la computadora hará su movimiento y cambiará el texto del botón apropiado a una O roja. [cite: 5] [cite\_start]El control de texto indicará de quién es el turno y cuándo termina el juego. [cite: 5]

### Creando el Proyecto de Android

1.  [cite\_start]Inicia Android Studio, selecciona **Archivo → Nuevo Proyecto...**. [cite: 7]
2.  [cite\_start]Cuando aparezca el diálogo de Nuevo Proyecto, selecciona la carpeta de **Android**, luego **Proyecto de Android**, y haz clic en **Siguiente**. [cite: 7]
3.  [cite\_start]En el diálogo de Nuevo Proyecto de Android, introduce los siguientes valores: [cite: 8]
      * [cite\_start]**Nombre del proyecto:** `AndroidTicTacToe-Tutorial2` [cite: 9]
      * [cite\_start]**Nombre de la Aplicación:** `Android Tic-Tac-Toe` [cite: 9]
      * [cite\_start]**Nombre del Paquete:** `co.edu.unal.tictactoe` [cite: 9]
      * [cite\_start]**Nombre de la Actividad:** `AndroidTicTacToeActivity` [cite: 9]
4.  [cite\_start]Haz clic en el botón **Finalizar** para crear tu proyecto. [cite: 10]

### Diseño de la App

En lugar de empezar desde cero, se ha proporcionado un juego de tres en raya completamente funcional que se ejecuta en una ventana de consola en:
[cite\_start][http://cs.harding.edu/fmccown/android/TicTacToeConsole.java](http://cs.harding.edu/fmccown/android/TicTacToeConsole.java) [cite: 27]

[cite\_start]Ejecuta este programa y familiarízate con el código fuente para entender cómo funciona. [cite: 30] [cite\_start]Utiliza un arreglo de `char` para representar el tablero de juego, donde 'X' representa al humano y 'O' a la computadora. [cite: 31] [cite\_start]El programa implementa una IA simple para la computadora: ganará el juego si es posible, bloqueará al humano para que no gane, o hará un movimiento aleatorio. [cite: 32]

[cite\_start]El objetivo es convertir este juego de consola en un juego de Android. [cite: 34] [cite\_start]Se mantendrá la lógica del juego separada de la interfaz de usuario (UI) para facilitar futuras modificaciones y portabilidad a otras plataformas. [cite: 35, 36, 37] [cite\_start]Toda la lógica de la UI se colocará en `AndroidTicTacToeActivity.java` y toda la lógica del juego en `TicTacToeGame.java`. [cite: 40]

1.  [cite\_start]**Añade `TicTacToeGame.java` a tu proyecto Android.** Ve a **Archivo → Nuevo → Clase**. [cite: 41] [cite\_start]Asegúrate de que el paquete sea el correcto, nombra la clase como `TicTacToeGame` y haz clic en **Finalizar**. [cite: 42]
2.  [cite\_start]**Copia y pega el código** del juego de consola en `TicTacToeGame.java`. [cite: 43] [cite\_start]Cambia el nombre del constructor de `TicTacToeConsole` a `TicTacToeGame`. [cite: 44]
3.  **Modifica la clase `TicTacToeGame`:**
      * [cite\_start]Crea una constante llamada `OPEN_SPOT` que use un carácter de espacio para representar una ubicación libre. [cite: 46]
        ```java
        // Caracteres usados para representar al humano, la computadora y los espacios abiertos
        [cite_start]public static final char HUMAN_PLAYER = 'X'; [cite: 49]
        [cite_start]public static final char COMPUTER_PLAYER = 'O'; [cite: 50]
        [cite_start]public static final char OPEN_SPOT = ' '; [cite: 51]
        ```
      * [cite\_start]Elimina todo el código innecesario del constructor, dejando solo la inicialización del generador de números aleatorios. [cite: 52, 53]
      * [cite\_start]Elimina las funciones `getUserMove()` y `main()`. [cite: 54]
4.  [cite\_start]**Crea varios métodos públicos** para manipular el tablero de juego y determinar un ganador. [cite: 55] [cite\_start]El resto de los métodos en la clase deben ser privados. [cite: 57]
    ```java
    /** Limpia el tablero de todas las X y O estableciendo todos los lugares a OPEN_SPOT. */
    [cite_start]public void clearBoard() [cite: 58]

    /**
     * Establece el jugador dado en la ubicación dada en el tablero de juego.
     * La ubicación debe estar disponible, o el tablero no se cambiará.
     * @param player - El HUMAN_PLAYER o COMPUTER_PLAYER
     * @param location - La ubicación (0-8) para colocar el movimiento
     */
    [cite_start]public void setMove(char player, int location) [cite: 64]

    /**
     * Devuelve el mejor movimiento para que la computadora lo haga. Debes llamar a setMove()
     * para hacer que la computadora se mueva a esa ubicación.
     * @return El mejor movimiento para que la computadora haga (0-8).
     */
    [cite_start]public int getComputerMove() [cite: 68]

    /**
     * Comprueba si hay un ganador y devuelve un valor de estado que indica quién ha ganado.
     * @return Devuelve 0 si no hay ganador ni empate, 1 si es un empate, 2 si ganó X,
     * o 3 si ganó O.
     */
    [cite_start]public int checkForWinner() [cite: 73]
    ```
5.  [cite\_start]Para que la lógica del juego sea accesible, crea una variable a nivel de clase para `TicTacToeGame` en `AndroidTicTacToeActivity.java`: [cite: 74]
    ```java
    public class AndroidTicTacToeActivity extends Activity {
        // Representa el estado interno del juego
        [cite_start]private TicTacToeGame mGame; [cite: 78]
    }
    ```

### Creando un Tablero de Juego

[cite\_start]El tablero se representará usando `Buttons` y se definirá en un archivo XML. [cite: 84, 85]

1.  [cite\_start]Abre `res/layout/main.xml` y cambia a la vista de XML. [cite: 86, 88]
2.  [cite\_start]Usaremos una combinación de `LinearLayout` y `TableLayout` para organizar los botones. [cite: 90] [cite\_start]El XML a continuación define los botones del 1 al 3. [cite: 95] [cite\_start]Deberás definir los botones del 4 al 9 en sus propias `TableRows`. [cite: 97]

<!-- end list -->

```xml
[cite_start]<?xml version="1.0" encoding="utf-8"?> [cite: 100]
[cite_start]<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android" [cite: 101]
    [cite_start]android:orientation="vertical" [cite: 102]
    [cite_start]android:layout_width="fill_parent" [cite: 103]
    [cite_start]android:layout_height="fill_parent" [cite: 104]
    [cite_start]android:gravity="center_horizontal" > [cite: 105]

    [cite_start]<TableLayout [cite: 106]
        [cite_start]android:id="@+id/play_grid" [cite: 107]
        [cite_start]android:layout_width="fill_parent" [cite: 109]
        [cite_start]android:layout_height="wrap_content" [cite: 108]
        [cite_start]android:layout_marginTop="5dp" > [cite: 110]

        [cite_start]<TableRow android:gravity="center_horizontal"> [cite: 111]
            [cite_start]<Button android:id="@+id/one" [cite: 112]
                [cite_start]android:layout_width="100dp" [cite: 122]
                [cite_start]android:layout_height="100dp" [cite: 113]
                [cite_start]android:text="1" [cite: 123]
                [cite_start]android:textSize="70dp" /> [cite: 114]
            [cite_start]<Button android:id="@+id/two" [cite: 115]
                [cite_start]android:layout_width="100dp" [cite: 124]
                [cite_start]android:layout_height="100dp" [cite: 119]
                [cite_start]android:text="2" [cite: 125]
                [cite_start]android:textSize="70dp" /> [cite: 120]
            [cite_start]<Button android:id="@+id/three" [cite: 121]
                [cite_start]android:layout_width="100dp" [cite: 126]
                android:layout_height="100dp"
                [cite_start]android:text="3" [cite: 127]
                android:textSize="70dp" />
        </TableRow>
        </TableLayout>

    [cite_start]<TextView android:id="@+id/information" [cite: 128]
        [cite_start]android:layout_width="fill_parent" [cite: 130]
        [cite_start]android:layout_height="wrap_content" [cite: 129]
        [cite_start]android:layout_marginTop="20dp" [cite: 131]
        android:text="info"
        android:gravity="center_horizontal"
        android:textSize="20dp" />
</LinearLayout>
```

### Accediendo a los Widgets

[cite\_start]Ahora, conecta los widgets definidos en el XML con tu `Activity`. [cite: 187]

1.  [cite\_start]En `AndroidTicTacToeActivity.java`, declara un arreglo de `Button` y un `TextView`. [cite: 188]

    ```java
    // Botones que componen el tablero
    [cite_start]private Button mBoardButtons[]; [cite: 189]
    // Varios textos mostrados
    [cite_start]private TextView mInfoTextView; [cite: 191]
    ```

    [cite\_start]*Nota: Si encuentras errores, presiona `Ctrl-Shift-O` para organizar las importaciones (`import android.widget.Button;` y `import android.widget.TextView;`).* [cite: 193, 194, 195]

2.  [cite\_start]En el método `onCreate()`, instancia el arreglo y el objeto del juego. [cite: 196] [cite\_start]Usa `findViewById()` para vincular cada botón a una ranura en el arreglo. [cite: 196]

    ```java
    @Override
    public void onCreate(Bundle savedInstanceState) {
        [cite_start]super.onCreate(savedInstanceState); [cite: 203]
        [cite_start]setContentView(R.layout.main); [cite: 203]

        [cite_start]mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE]; [cite: 204]
        [cite_start]mBoardButtons[0] = (Button) findViewById(R.id.one); [cite: 204]
        [cite_start]mBoardButtons[1] = (Button) findViewById(R.id.two); [cite: 204]
        [cite_start]mBoardButtons[2] = (Button) findViewById(R.id.three); [cite: 204]
        [cite_start]mBoardButtons[3] = (Button) findViewById(R.id.four); [cite: 205]
        [cite_start]mBoardButtons[4] = (Button) findViewById(R.id.five); [cite: 205]
        [cite_start]mBoardButtons[5] = (Button) findViewById(R.id.six); [cite: 205]
        [cite_start]mBoardButtons[6] = (Button) findViewById(R.id.seven); [cite: 205]
        [cite_start]mBoardButtons[7] = (Button) findViewById(R.id.eight); [cite: 206]
        [cite_start]mBoardButtons[8] = (Button) findViewById(R.id.nine); [cite: 206]

        [cite_start]mInfoTextView = (TextView) findViewById(R.id.information); [cite: 207]

        [cite_start]mGame = new TicTacToeGame(); [cite: 208]
    }
    ```

### Añadiendo Lógica de Juego

1.  **Crea un método `startNewGame()`**. [cite\_start]Este método limpiará el tablero interno (`mGame.clearBoard()`) y reiniciará el tablero visible. [cite: 212, 214]
    ```java
    // Configura el tablero de juego.
    private void startNewGame() {
        [cite_start]mGame.clearBoard(); [cite: 215]

        // Reinicia todos los botones
        [cite_start]for (int i = 0; i < mBoardButtons.length; i++) { [cite: 221]
            [cite_start]mBoardButtons[i].setText(""); [cite: 223]
            [cite_start]mBoardButtons[i].setEnabled(true); [cite: 224]
            [cite_start]mBoardButtons[i].setOnClickListener(new ButtonClickListener(i)); [cite: 225]
        }

        // El humano va primero
        [cite_start]mInfoTextView.setText("You go first."); [cite: 232]
    }
    ```
2.  [cite\_start]**Llama a `startNewGame()`** al final de tu método `onCreate()`. [cite: 235]
    ```java
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // ... código de inicialización ...
        [cite_start]startNewGame(); [cite: 238]
    }
    ```
3.  [cite\_start]**Crea la clase interna `ButtonClickListener`**. [cite: 240] [cite\_start]Esta clase implementa `View.OnClickListener` y maneja los clics en los botones del tablero. [cite: 241, 249]
    ```java
    // Maneja los clics en los botones del tablero de juego
    [cite_start]private class ButtonClickListener implements View.OnClickListener { [cite: 250]
        [cite_start]int location; [cite: 251]

        [cite_start]public ButtonClickListener(int location) { [cite: 252]
            [cite_start]this.location = location; [cite: 254]
        }

        public void onClick(View view) {
            [cite_start]if (mBoardButtons[location].isEnabled()) { [cite: 256]
                [cite_start]setMove(TicTacToeGame.HUMAN_PLAYER, location); [cite: 258]

                // Si aún no hay ganador, que la computadora haga un movimiento
                [cite_start]int winner = mGame.checkForWinner(); [cite: 259]
                [cite_start]if (winner == 0) { [cite: 260]
                    [cite_start]mInfoTextView.setText("It's Android's turn."); [cite: 261]
                    [cite_start]int move = mGame.getComputerMove(); [cite: 261]
                    [cite_start]setMove(TicTacToeGame.COMPUTER_PLAYER, move); [cite: 262]
                    [cite_start]winner = mGame.checkForWinner(); [cite: 263]
                }

                [cite_start]if (winner == 0) [cite: 264]
                    [cite_start]mInfoTextView.setText("It's your turn."); [cite: 265]
                [cite_start]else if (winner == 1) [cite: 266]
                    [cite_start]mInfoTextView.setText("It's a tie!"); [cite: 267]
                [cite_start]else if (winner == 2) [cite: 268]
                    [cite_start]mInfoTextView.setText("You won!"); [cite: 270]
                [cite_start]else [cite: 269]
                    [cite_start]mInfoTextView.setText("Android won!"); [cite: 271]
            }
        }
    }
    ```
4.  **Crea el método `setMove()`**. [cite\_start]Este método actualiza el modelo del tablero, deshabilita el botón y establece el texto y el color apropiados. [cite: 278, 279]
    ```java
    private void setMove(char player, int location) {
        [cite_start]mGame.setMove(player, location); [cite: 282]
        [cite_start]mBoardButtons[location].setEnabled(false); [cite: 283]
        [cite_start]mBoardButtons[location].setText(String.valueOf(player)); [cite: 283]
        [cite_start]if (player == TicTacToeGame.HUMAN_PLAYER) [cite: 283]
            [cite_start]mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0)); [cite: 284]
        else
            [cite_start]mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0)); [cite: 286]
    }
    ```
5.  **Maneja el fin del juego**. [cite\_start]Introduce una variable booleana a nivel de clase `mGameOver` para evitar que se hagan movimientos después de que el juego haya terminado. [cite: 291]
      * [cite\_start]Establece `mGameOver` a `false` cuando comienza un nuevo juego. [cite: 292]
      * [cite\_start]Verifica `mGameOver` antes de permitir un movimiento. [cite: 293]
      * [cite\_start]Establece `mGameOver` a `true` cuando el juego ha terminado. [cite: 294]

### Añadiendo un Menú

[cite\_start]Para permitir que el usuario inicie un nuevo juego, añade un menú de opciones. [cite: 297]

1.  [cite\_start]**Sobrescribe `onCreateOptionsMenu()`** para crear el menú. [cite: 300]
    ```java
    @Override
    [cite_start]public boolean onCreateOptionsMenu(Menu menu) { [cite: 307]
        [cite_start]super.onCreateOptionsMenu(menu); [cite: 308]
        [cite_start]menu.add("New Game"); [cite: 308]
        [cite_start]return true; [cite: 308]
    }
    ```
2.  [cite\_start]**Sobrescribe `onOptionsItemSelected()`** para manejar los clics en los elementos del menú. [cite: 310]
    ```java
    @Override
    [cite_start]public boolean onOptionsItemSelected(MenuItem item) { [cite: 315]
        [cite_start]startNewGame(); [cite: 316]
        [cite_start]return true; [cite: 317]
    }
    ```

### Mensajes de Cadena

[cite\_start]Para seguir las mejores prácticas, mueve todos los mensajes de la UI codificados en el código a `res/values/strings.xml`. [cite: 326]

1.  [cite\_start]Abre `strings.xml` y añade las siguientes cadenas: [cite: 327, 328]

    ```xml
    [cite_start]<?xml version="1.0" encoding="utf-8"?> [cite: 329]
    [cite_start]<resources> [cite: 330]
        [cite_start]<string name="app_name">Tic-Tac-Toe</string> [cite: 331]
        [cite_start]<string name="first_human">You go first.</string> [cite: 332]
        [cite_start]<string name="turn_human">Your turn.</string> [cite: 333]
        [cite_start]<string name="turn_computer">Android\'s turn.</string> [cite: 334]
        [cite_start]<string name="result_tie">It\'s a tie.</string> [cite: 335]
        [cite_start]<string name="result_human_wins">You won!</string> [cite: 336]
        [cite_start]<string name="result_computer_wins">Android won!</string> [cite: 337]
    [cite_start]</resources> [cite: 338]
    ```

    [cite\_start]*Nota: Los apóstrofes deben escaparse con una barra invertida (`\'`).* [cite: 340]

2.  [cite\_start]Modifica tu código Java para usar estas cadenas de recursos. [cite: 344] Por ejemplo:

    ```java
    [cite_start]mInfoTextView.setText(R.string.result_human_wins); [cite: 346]
    ```

### Reto Adicional

  * [cite\_start]Haz que el juego sea más justo alternando quién va primero. [cite: 350]
  * [cite\_start]Lleva un registro de cuántos juegos ha ganado el usuario, cuántos la computadora y los empates. [cite: 351]
  * [cite\_start]Muestra esta información usando controles `TextView` debajo del estado del juego, utilizando un `RelativeLayout` para posicionarlos. [cite: 352, 353]