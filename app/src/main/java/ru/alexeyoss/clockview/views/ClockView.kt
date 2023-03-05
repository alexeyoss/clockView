package ru.alexeyoss.clockview.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import ru.alexeyoss.clockview.R
import java.util.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.properties.Delegates

class ClockView
@JvmOverloads constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val calendar by lazy { Calendar.getInstance() }

    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0

    // Initialize type of numbers
    private val numbersMap = mapOf(0 to arabicNumbers, 1 to romanNumbers)
    private var clockTypeOfNumber by Delegates.notNull<Int>()

    // Initialize attributes
    var clockRadius = 0f

    private var clockDialColor by Delegates.notNull<Int>()
    private var clockDialStrokeWidth by Delegates.notNull<Float>()

    private var clockHourHandColor by Delegates.notNull<Int>()
    private var clockHourHandStrokeWidth by Delegates.notNull<Float>()

    private var clockMinuteHandColor by Delegates.notNull<Int>()
    private var clockMinuteHandStrokeWidth by Delegates.notNull<Float>()

    private var clockSecondHandColor by Delegates.notNull<Int>()
    private var clockSecondHandStrokeWidth by Delegates.notNull<Float>()

    private var clockNumbersColor by Delegates.notNull<Int>()
    private var clockNumbersSize by Delegates.notNull<Float>()
    private var defaultClockNumbersSize = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, 20f,
        resources.displayMetrics
    )

    // Pre-initialized paints for drawing
    private val dialPaint by lazy { Paint() }
    private val hourPaint by lazy { Paint() }
    private val minutePaint by lazy { Paint() }
    private val secondPaint by lazy { Paint() }
    private val numberPaint by lazy { Paint() }

    // TODO callback flow
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            updateClock()
            sendEmptyMessageDelayed(0, 1000)
        }
    }

    private fun updateClock() {
        calendar.timeInMillis = System.currentTimeMillis()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        second = calendar.get(Calendar.SECOND)
        invalidate()
    }


    // If there is no style, global style, color attributes -> then use values from DefaultClockViewStyle
    constructor(context: Context, attributesSet: AttributeSet?, defStyleAttr: Int) : this(
        context, attributesSet, defStyleAttr, R.style.DefaultClockViewStyle
    )

    // If global style is defined in app theme by using clockViewStyle attribute ->
    // All clockView views in the project will use that style
    constructor(context: Context, attributesSet: AttributeSet?) : this(
        context, attributesSet, R.attr.clockViewStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        if (attrs != null) {
            initAttributes(attrs, defStyleAttr, defStyleRes)
        } else {
            initDefaultStyle()
        }
        initPaints()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Clock Dial
        canvas.drawCircle(
            clockRadius,
            clockRadius,
            clockRadius - dialPaint.strokeWidth / 2f,
            dialPaint
        )
        // Numbers
        drawNumbers(clockRadius, canvas)
        // Clock Hour hand
        drawHourHand(canvas, clockRadius)
        // Clock Minute hand
        drawMinuteHand(canvas, clockRadius)
        // Clock Second hand
        drawSecondHand(canvas, clockRadius)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        handler.sendEmptyMessage(0)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacksAndMessages(null)
    }

    private fun drawSecondHand(canvas: Canvas, radius: Float) {
        canvas.save()
        canvas.rotate(second * 6f, radius, radius)
        canvas.drawLine(radius, radius + 20f, radius, radius - radius * 0.9f, secondPaint)
        canvas.restore()
    }

    private fun drawMinuteHand(canvas: Canvas, radius: Float) {
        canvas.save()
        canvas.rotate(minute * 6f, radius, radius)
        canvas.drawLine(radius, radius + 20f, radius, radius - radius * 0.7f, minutePaint)
        canvas.restore()
    }

    private fun drawHourHand(canvas: Canvas, radius: Float) {
        canvas.save()
        canvas.rotate((hour + minute / 60f) * 30f, radius, radius)
        canvas.drawLine(radius, radius + 20f, radius, radius - radius / 2f, hourPaint)
        canvas.restore()
    }

    private fun drawNumbers(radius: Float, canvas: Canvas) {
        for (i in MIN_HOUR until MAX_HOUR) {
            val x = radius + (radius - 70f) * cos(Math.toRadians((i * 30).toDouble())).toFloat()
            val y = radius + (radius - 70f) * sin(Math.toRadians((i * 30).toDouble())).toFloat()

            canvas.drawText(
                numbersMap[clockTypeOfNumber]!![i], x, y + numberPaint.textSize / 3f, numberPaint
            )
        }
    }

    private fun initPaints() {
        dialPaint.apply {
            isAntiAlias = true
            color = clockDialColor
            strokeWidth = clockDialStrokeWidth
            style = Paint.Style.STROKE
        }

        hourPaint.apply {
            isAntiAlias = true
            color = clockHourHandColor
            strokeWidth = clockHourHandStrokeWidth
        }

        minutePaint.apply {
            isAntiAlias = true
            color = clockMinuteHandColor
            strokeWidth = clockMinuteHandStrokeWidth
        }

        secondPaint.apply {
            isAntiAlias = true
            color = clockSecondHandColor
            strokeWidth = clockSecondHandStrokeWidth
        }

        numberPaint.apply {
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
            color = clockNumbersColor
            textSize = clockNumbersSize
        }

    }

    private fun initDefaultStyle() {
        clockDialColor = DEFAULT_CLOCK_DIAL_COLOR
        clockDialStrokeWidth = DEFAULT_CLOCK_DIAL_STROKE_WIDTH

        clockHourHandColor = DEFAULT_CLOCK_HOUR_HAND_COLOR
        clockHourHandStrokeWidth = DEFAULT_CLOCK_HOUR_HAND_STROKE_WIDTH

        clockMinuteHandColor = DEFAULT_CLOCK_MINUTE_HAND_COLOR
        clockMinuteHandStrokeWidth = DEFAULT_CLOCK_MINUTE_HAND_STROKE_WIDTH

        clockSecondHandColor = DEFAULT_CLOCK_SECOND_HAND_COLOR
        clockSecondHandStrokeWidth = DEFAULT_CLOCK_SECOND_HAND_STROKE_WIDTH

        clockNumbersColor = DEFAULT_NUMBERS_COLOR
        clockNumbersSize = defaultClockNumbersSize
    }

    private fun initAttributes(attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        context.obtainStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, defStyleRes)
            .apply {
                clockRadius =
                    getDimension(R.styleable.ClockView_clockRadius, CLOCK_DEFAULT_RADIUS)
                // Obtain clock dial style
                clockDialColor =
                    getColor(R.styleable.ClockView_clockDialColor, DEFAULT_CLOCK_DIAL_COLOR)
                clockDialStrokeWidth = getFloat(
                    R.styleable.ClockView_clockDialStrokeWidth, DEFAULT_CLOCK_DIAL_STROKE_WIDTH
                )

                // Obtain clock hour hand style
                clockHourHandColor = getColor(
                    R.styleable.ClockView_clockHourHandColor, DEFAULT_CLOCK_HOUR_HAND_COLOR
                )
                clockHourHandStrokeWidth = getFloat(
                    R.styleable.ClockView_clockHourHandStrokeWidth,
                    DEFAULT_CLOCK_HOUR_HAND_STROKE_WIDTH
                )

                // Obtain clock minute hand style
                clockMinuteHandColor = getColor(
                    R.styleable.ClockView_clockMinuteHandColor, DEFAULT_CLOCK_MINUTE_HAND_COLOR
                )
                clockMinuteHandStrokeWidth = getFloat(
                    R.styleable.ClockView_clockMinuteHandStrokeWidth,
                    DEFAULT_CLOCK_MINUTE_HAND_STROKE_WIDTH
                )

                // Obtain clock second hand style
                clockSecondHandColor = getColor(
                    R.styleable.ClockView_clockSecondHandColor, DEFAULT_CLOCK_SECOND_HAND_COLOR
                )
                clockSecondHandStrokeWidth = getFloat(
                    R.styleable.ClockView_clockSecondHandStrokeWidth,
                    DEFAULT_CLOCK_SECOND_HAND_STROKE_WIDTH
                )
                // Clock type of numbers
                clockTypeOfNumber =
                    getInt(R.styleable.ClockView_clockTypeOfNumbers, DEFAULT_TYPE_OF_NUMBERS)

                // Number style
                clockNumbersColor =
                    getColor(R.styleable.ClockView_clockNumbersColor, DEFAULT_NUMBERS_COLOR)
                clockNumbersSize =
                    getFloat(R.styleable.ClockView_clockNumberSize, defaultClockNumbersSize)

                recycle()
            }


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = (clockRadius * 2).toInt()
        val desiredHeight = (clockRadius * 2).toInt()
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec) + paddingLeft + paddingRight,
            resolveSize(desiredHeight, heightMeasureSpec) + paddingTop + paddingBottom
        )
    }

    companion object {
        const val MIN_HOUR = 0
        const val MAX_HOUR = 12

        const val CLOCK_DEFAULT_RADIUS = 200f

        const val DEFAULT_CLOCK_DIAL_COLOR = Color.BLACK
        const val DEFAULT_CLOCK_DIAL_STROKE_WIDTH = 12f

        const val DEFAULT_CLOCK_HOUR_HAND_COLOR = Color.RED
        const val DEFAULT_CLOCK_HOUR_HAND_STROKE_WIDTH = 8f

        const val DEFAULT_CLOCK_MINUTE_HAND_COLOR = Color.RED
        const val DEFAULT_CLOCK_MINUTE_HAND_STROKE_WIDTH = 4.5f

        const val DEFAULT_CLOCK_SECOND_HAND_COLOR = Color.RED
        const val DEFAULT_CLOCK_SECOND_HAND_STROKE_WIDTH = 2f

        const val DEFAULT_TYPE_OF_NUMBERS = 0

        const val DEFAULT_NUMBERS_COLOR = Color.BLACK

        private val arabicNumbers =
            arrayOf("3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "1", "2")
        private val romanNumbers =
            arrayOf("III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "I", "II")
    }
}