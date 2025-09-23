package com.example.reto3

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val GRID_WIDTH = 6f
    }

    private lateinit var paint: Paint
    private var boardColor = Color.GRAY
    private var xImage: Bitmap? = null
    private var oImage: Bitmap? = null
    private var game: TicTacToeGame? = null
    private var cellWidth = 0f
    private var cellHeight = 0f

    init {
        initialize()
    }

    private fun initialize() {
        // Configurar paint para dibujar las líneas del tablero
        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = boardColor
            strokeWidth = GRID_WIDTH
            style = Paint.Style.STROKE
        }

        // Cargar imágenes de X y O desde recursos drawable
        try {
            val xDrawable = context.getDrawable(R.drawable.x_piece)
            val oDrawable = context.getDrawable(R.drawable.o_piece)

            // Convertir drawables a bitmaps
            xImage = drawableToBitmap(xDrawable)
            oImage = drawableToBitmap(oDrawable)
        } catch (e: Exception) {
            // Si hay error cargando imágenes, crear bitmaps de colores
            xImage = createColoredBitmap(100, 100, Color.GREEN)
            oImage = createColoredBitmap(100, 100, Color.RED)
        }
    }

    private fun createColoredBitmap(width: Int, height: Int, color: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height / 2f, width / 3f, paint)
        return bitmap
    }

    private fun drawableToBitmap(drawable: android.graphics.drawable.Drawable?): Bitmap? {
        drawable ?: return null

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun setGame(game: TicTacToeGame) {
        this.game = game
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Calcular el tamaño de cada celda
        val boardWidth = (w - paddingLeft - paddingRight).toFloat()
        val boardHeight = (h - paddingTop - paddingBottom).toFloat()

        cellWidth = boardWidth / 3f
        cellHeight = boardHeight / 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width - paddingLeft - paddingRight
        val height = height - paddingTop - paddingBottom

        // Dibujar las líneas del tablero
        // Líneas verticales
        for (i in 1..2) {
            val x = paddingLeft + (width * i / 3f)
            canvas.drawLine(x, paddingTop.toFloat(), x, (paddingTop + height).toFloat(), paint)
        }

        // Líneas horizontales
        for (i in 1..2) {
            val y = paddingTop + (height * i / 3f)
            canvas.drawLine(paddingLeft.toFloat(), y, (paddingLeft + width).toFloat(), y, paint)
        }

        // Dibujar las piezas (X y O)
        game?.let { ticTacToeGame ->
            for (row in 0..2) {
                for (col in 0..2) {
                    val position = row * 3 + col
                    val piece = ticTacToeGame.getBoardOccupant(position)

                    if (piece != TicTacToeGame.OPEN_SPOT) {
                        val left = paddingLeft + col * cellWidth
                        val top = paddingTop + row * cellHeight
                        val right = left + cellWidth
                        val bottom = top + cellHeight

                        val rect = RectF(left, top, right, bottom)

                        // Agregar un pequeño margen para que las piezas no toquen las líneas
                        val margin = 20f
                        rect.inset(margin, margin)

                        val bitmap = if (piece == TicTacToeGame.HUMAN_PLAYER) xImage else oImage
                        bitmap?.let { bmp ->
                            canvas.drawBitmap(bmp, null, rect, null)
                        }
                    }
                }
            }
        }
    }

    fun getBoardCellFromCoordinates(x: Float, y: Float): Int {
        if (cellWidth == 0f || cellHeight == 0f) return -1

        val adjustedX = x - paddingLeft
        val adjustedY = y - paddingTop

        val col = (adjustedX / cellWidth).toInt()
        val row = (adjustedY / cellHeight).toInt()

        return if (row in 0..2 && col in 0..2) {
            row * 3 + col
        } else {
            -1
        }
    }
}