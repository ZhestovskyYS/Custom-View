package otus.homework.customview

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.isGone
import kotlin.math.max

class Column @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
): ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMS: Int, heightMS: Int) {
        var usedHeight = paddingTop + paddingBottom
        var maxChildWidth = 0

        children.forEach { child ->
            if (child.isGone) return@forEach

            val lp = child.layoutParams as MarginLayoutParams
            val childWidthMS = getChildMeasureSpec(
                widthMS,
                paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin,
                lp.width
            )
            val childHeightMS = getChildMeasureSpec(
                heightMS,
                usedHeight + lp.topMargin + lp.bottomMargin,
                lp.height
            )
            child.measure(childWidthMS, childHeightMS)

            usedHeight += child.measuredHeight + lp.topMargin + lp.bottomMargin
            maxChildWidth = max(maxChildWidth, child.measuredWidth + lp.leftMargin + lp.rightMargin)
        }

        val finalWidth  = resolveSize(maxChildWidth + paddingLeft + paddingRight, widthMS)
        val finalHeight = resolveSize(usedHeight, heightMS)
        setMeasuredDimension(finalWidth, finalHeight)
    }

    override fun onLayout(ch: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var y = paddingTop
        children.forEach { child ->
            if (child.isGone) return@forEach

            val lp = child.layoutParams as MarginLayoutParams
            val left  = paddingLeft + lp.leftMargin
            val top   = y + lp.topMargin
            val right = left + child.measuredWidth
            val bottom = top + child.measuredHeight
            child.layout(left, top, right, bottom)

            y = bottom + lp.bottomMargin
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p != null && p is MarginLayoutParams
    }
}