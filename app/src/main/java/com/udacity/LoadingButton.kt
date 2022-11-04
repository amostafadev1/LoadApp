package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val OFFSET_CIRCLE = 100
private const val CIRCLE_DIAMETER = 75f

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0f
    private var heightSize = 0f

    private var buttonBackgroundColor = 0
    private var textColor = 0

    private var valueAnimator = ValueAnimator()

    private val buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textHalfDistance = 0f

    private var circleAngle = 0f
    private var progressWidth = 0f

    private val buttonNonLoadingLabel = context.getString(R.string.button_label)
    private val buttonLoadingLabel = context.getString(R.string.button_loading)
    private var buttonLabel = buttonNonLoadingLabel

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {
                isEnabled = false
            }

            ButtonState.Loading -> {
                buttonLabel = buttonLoadingLabel
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize)
                    .apply {
                        addUpdateListener {
                            val value = animatedValue as Float
                            progressWidth = value
                            circleAngle = value * 360 / widthSize
                            invalidate()
                        }
                        duration = 2000
                        repeatCount = ValueAnimator.INFINITE
                        start()
                    }
            }

            ButtonState.Completed -> {
                valueAnimator.cancel()
                circleAngle = 0f
                progressWidth = 0f
                buttonLabel = buttonNonLoadingLabel
                invalidate()
                isEnabled = true
            }
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(
                R.styleable.LoadingButton_buttonBackgroundColor,
                R.attr.colorPrimary
            )
            textColor = getColor(
                R.styleable.LoadingButton_textColor,
                Color.WHITE
            )
        }

        textPaint.apply {
            color = textColor
            textAlign = Paint.Align.CENTER
            textSize = resources.getDimension(R.dimen.default_text_size)
        }
        textHalfDistance = (textPaint.descent() + textPaint.ascent()) / 2
        buttonPaint.apply {
            color = context.getColor(R.color.colorPrimaryDark)
        }
        circlePaint.apply {
            color = context.getColor(R.color.colorAccent)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null)
            return
        drawBackground(canvas)
        drawText(canvas)
        drawCircle(canvas)
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(
            buttonLabel,
            widthSize / 2f,
            heightSize / 2f - textHalfDistance,
            textPaint
        )
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(buttonBackgroundColor)
        canvas.drawRect(0f, 0f, progressWidth, heightSize, buttonPaint)
    }

    private fun drawCircle(canvas: Canvas) {
        canvas.save()
        canvas.translate(
            widthSize - OFFSET_CIRCLE - CIRCLE_DIAMETER,
            (heightSize - CIRCLE_DIAMETER) / 2
        )
        canvas.drawArc(
            0f, 0f, CIRCLE_DIAMETER, CIRCLE_DIAMETER,
            0f, circleAngle, true, circlePaint
        )
        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w.toFloat()
        heightSize = h.toFloat()
        setMeasuredDimension(w, h)
    }

}
