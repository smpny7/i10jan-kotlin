package net.oucrc

import android.text.Spanned
import android.text.method.DigitsKeyListener

// -----------------------------------------------------------------------------------------
//    DecimalDigitLimitFilter
// -----------------------------------------------------------------------------------------
//    http://y-anz-m.blogspot.com/2013/05/inputfilter.html
// -----------------------------------------------------------------------------------------

@Suppress("DEPRECATION")

class DecimalDigitLimitFilter(upperDigitsNum: Int, lowerDigitsNum: Int) :
    DigitsKeyListener(false, true) {
    private val UPPER_POINT_DIGITS_NUM: Int = if (upperDigitsNum < 0) 0 else upperDigitsNum
    private val LOWER_POINT_DIGITS_NUM: Int = if (lowerDigitsNum < 0) 0 else lowerDigitsNum

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val cs = super.filter(source, start, end, dest, dstart, dend)
        if (cs != null)
            return cs

        if (end - start > 1)
            return ""

        val s = dest.toString()
        val i = s.indexOf(".")

        return if (i >= 0) {
            if (i in dstart until dend)
                return if (source != ".")
                    if (dest.length - (dend - dstart) + (end - start) <= UPPER_POINT_DIGITS_NUM)
                        null
                    else
                        "."
                else
                    null

            if (dstart <= i)
                if (i - (dend - dstart) + (end - start) <= UPPER_POINT_DIGITS_NUM)
                    null
                else
                    ""
            else
                if (dest.length - (dend - dstart) + (end - start) - i - 1 <= LOWER_POINT_DIGITS_NUM)
                    null
                else
                    ""

        } else {
            if (source == ".")
                if (dest.length - dend <= LOWER_POINT_DIGITS_NUM)
                    null
                else
                    ""
            else
                if (dest.length - (dend - dstart) + (end - start) <= UPPER_POINT_DIGITS_NUM)
                    null
                else
                    ""
        }
    }

}