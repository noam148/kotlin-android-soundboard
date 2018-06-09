package nl.gillz.soundboard.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import nl.gillz.soundboard.R

/**
 * TODO: document your custom view class.
 */
class FavoriteButton : ImageButton {

    private var active: Boolean = false
    private var soundPath: String = "";

    constructor(context: Context) : super(context) {
        init(null, 0, context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0, context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle, context)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int, context: Context) {

        // Set background
        background = context.getDrawable(R.drawable.icn_star_inactive)

        // Set on click listener
        setOnClickListener(OnClickListener {

        })
    }

    fun setActive(active: Boolean){

        // Set to propery
        this.active = active

        // Set active or in active
        background = if(active){
            context.getDrawable(R.drawable.icn_star_active)
        } else {
            context.getDrawable(R.drawable.icn_star_inactive)
        }
    }

    fun isActive(): Boolean {
        return this.active;
    }
}
