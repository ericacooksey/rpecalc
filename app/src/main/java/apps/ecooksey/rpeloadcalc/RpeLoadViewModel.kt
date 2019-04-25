package apps.ecooksey.rpeloadcalc

import android.app.Application
import android.databinding.Bindable
import apps.ecooksey.rpeloadcalc.utils.ObservableViewModel
import java.text.DecimalFormat

class RpeLoadViewModel(val context: Application) : ObservableViewModel(context) {

    data class RpeLoadModel(
        val haveWeight: Double? = null, val haveReps: Int? = null, val haveRpe: Double? = null,
        val wantReps: Int? = null, val wantRpe: Double? = null
    )

    private val df: DecimalFormat = DecimalFormat("#.#")

    var model = RpeLoadModel()
        set(value) {
            field = value
            calculatedE1rm = calculateE1RM()
            calculatedWantWeight = if (calculatedE1rm != null) calculateWantWeight(calculatedE1rm!!) else null
            notifyChange()
        }

    private var calculatedE1rm: Double? = null
    private var calculatedWantWeight: Double? = null

    @Bindable
    fun getE1rm(): String {
        val e1RmText = if (calculatedE1rm == null) "" else df.format(calculatedE1rm!!)
        return String.format(context.getString(R.string.e1rm_weight), e1RmText)
    }

    @Bindable
    fun getWantWeight(): String {
        val wantWeightText = if (calculatedWantWeight == null) "" else df.format(calculatedWantWeight!!)
        return String.format(context.getString(R.string.want_weight), wantWeightText)
    }

    private fun isModelEmpty(): Boolean {
        return model.haveReps == null
                && model.haveRpe == null
                && model.haveWeight == null
                && model.wantRpe == null
                && model.wantReps == null
    }

    @Bindable
    fun getHaveWeightError(): String? {
        return if (!isModelEmpty() && model.haveWeight == null) context.getString(R.string.weight_err) else null
    }

    @Bindable
    fun getHaveRepsError(): String? {
        return if (!isModelEmpty() && (model.haveReps == null || model.haveReps!! == 0)) context.getString(R.string.reps_err) else null
    }

    @Bindable
    fun getHaveRpeError(): String? {
        return if (!isModelEmpty() && (model.haveRpe == null || model.haveRpe!! < 4))
            context.getString(R.string.rpe_err)
        else null
    }

    @Bindable
    fun getWantRepsError(): String? {
        return if (!isModelEmpty() && (model.wantReps == null || model.wantReps!! == 0)) context.getString(R.string.reps_err) else null
    }

    @Bindable
    fun getWantRpeError(): String? {
        return if (!isModelEmpty() && (model.wantRpe == null || model.wantRpe!! < 4))
            context.getString(R.string.rpe_err)
        else null
    }

    @Bindable
    fun getLicenseText(): String {
        return context.getString(R.string.licensing)
    }

    /** Calculation methods below **/

    // This is a translation of Tuchscherer's standard percentage chart into
    // a continuous function. This enables using real numbers for RPEs, like 8.75.
    private fun percentage(reps: Int, inRpe: Double): Double {
        // Cap the RPE at 10.
        val rpe = if (inRpe > 10) 10.0 else inRpe

        // No prediction if failure occurred, or if RPE is unreasonably low.
        if (reps < 1 || rpe < 4) {
            return 0.0
        }

        // Handle the obvious case early to avoid bound errors.
        if (reps == 1 && rpe == 10.0) {
            return 100.0
        }

        // x is defined such that 1@10 = 0, 1@9 = 1, 1@8 = 2, etc.
        // By definition of RPE, then also:
        //  2@10 = 1@9 = 1
        //  3@10 = 2@9 = 1@8 = 2
        // And so on. That pattern gives the equation below.
        val x = (10.0 - rpe) + (reps - 1)

        // The logic breaks down for super-high numbers,
        // and it's too hard to extrapolate an E1RM from super-high-rep sets anyway.
        if (x >= 16) {
            return 0.0
        }

        val intersection = 2.92

        // The highest values follow a quadratic.
        // Parameters were resolved via GNUPlot and match extremely closely.
        if (x <= intersection) {
            val a = 0.347619
            val b = -4.60714
            val c = 99.9667
            return (a * x * x) + (b * x) + c
        }

        // Otherwise it's just a line, since Tuchscherer just guessed.
        val m = -2.64249
        val b = 97.0955
        return m * x + b
    }

    private fun calculateE1RM(): Double? {
        // Ensure that the E1RM widgets are sane.
        if (model.haveWeight == null || model.haveWeight!! <= 0.0) return null
        if (model.haveReps == null || model.haveReps!! < 1) return null
        if (model.haveRpe == null || model.haveRpe!! <= 0.0) return null

        // Calculate the E1RM percentage.
        val p = percentage(model.haveReps!!, model.haveRpe!!)
        if (p <= 0) return null
        val e1rm = model.haveWeight!! / p * 100
        if (e1rm <= 0) return null

        return e1rm
    }

    private fun calculateWantWeight(e1rm: Double): Double? {

        // Ensure that the Weight widgets are sane.
        if (model.wantReps == null || model.wantReps!! < 1) return null
        if (model.wantRpe == null || model.wantRpe!! <= 0) return null

        // Calculate the Weight percentage.
        val p2 = percentage(model.wantReps!!, model.wantRpe!!)
        if (p2 <= 0) return null

        return e1rm / 100 * p2
    }
}