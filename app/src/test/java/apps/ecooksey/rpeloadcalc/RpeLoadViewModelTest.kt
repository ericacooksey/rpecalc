package apps.ecooksey.rpeloadcalc

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.regex.Pattern

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RpeLoadViewModelTest {

    lateinit var viewModel: RpeLoadViewModel
    lateinit var context: Context
    private val validModel =
        RpeLoadViewModel.RpeLoadModel(haveWeight = 135.0, haveRpe = 8.0, haveReps = 5, wantRpe = 9.5, wantReps = 5)
    private val modelNoWant = RpeLoadViewModel.RpeLoadModel(haveWeight = 135.0, haveRpe = 8.0, haveReps = 5)

    @Before
    fun setUp() {
        val applicationMock = RuntimeEnvironment.application
        viewModel = RpeLoadViewModel(applicationMock)
        context = RuntimeEnvironment.application.applicationContext
    }

    @Test
    fun getE1rm() {
        val emptyE1rmLabel = String.format(context.getString(R.string.e1rm_weight), "")
        viewModel.model = validModel
        assertTrue(containsNumbers(viewModel.getE1rm()))
        viewModel.model = modelNoWant
        assertTrue(containsNumbers(viewModel.getE1rm()))
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertEquals(emptyE1rmLabel, viewModel.getE1rm())
    }

    @Test
    fun getWantWeight() {
        val emptyWeightLabel = String.format(context.getString(R.string.want_weight), "")
        viewModel.model = validModel
        assertTrue(containsNumbers(viewModel.getWantWeight()))
        viewModel.model = modelNoWant
        assertEquals(emptyWeightLabel, viewModel.getWantWeight())
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertEquals(emptyWeightLabel, viewModel.getWantWeight())
    }

    @Test
    fun getHaveWeightError() {
        viewModel.model = validModel
        assertNull(viewModel.getHaveWeightError())
        viewModel.model = modelNoWant
        assertNull(viewModel.getHaveWeightError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertNull(viewModel.getHaveWeightError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel(haveReps = 1, haveRpe = 10.0)
        assertEquals(context.getString(R.string.weight_err), viewModel.getHaveWeightError())
    }

    @Test
    fun getHaveRepsError() {
        viewModel.model = validModel
        assertNull(viewModel.getHaveRepsError())
        viewModel.model = modelNoWant
        assertNull(viewModel.getHaveRepsError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertNull(viewModel.getHaveRepsError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel(haveRpe = 10.0, haveWeight = 135.0)
        assertEquals(context.getString(R.string.reps_err), viewModel.getHaveRepsError())
    }

    @Test
    fun getHaveRpeError() {
        viewModel.model = validModel
        assertNull(viewModel.getHaveRpeError())
        viewModel.model = modelNoWant
        assertNull(viewModel.getHaveRpeError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertNull(viewModel.getHaveRpeError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel(haveReps = 1, haveWeight = 135.0)
        assertEquals(context.getString(R.string.rpe_err), viewModel.getHaveRpeError())
    }

    @Test
    fun getWantRepsError() {
        viewModel.model = validModel
        assertNull(viewModel.getWantRepsError())
        viewModel.model = modelNoWant
        assertEquals(context.getString(R.string.reps_err), viewModel.getWantRepsError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertNull(viewModel.getWantRepsError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel(haveRpe = 10.0, haveWeight = 135.0, haveReps = 5, wantRpe = 8.0)
        assertEquals(context.getString(R.string.reps_err), viewModel.getWantRepsError())
    }

    @Test
    fun getWantRpeError() {
        viewModel.model = validModel
        assertNull(viewModel.getWantRpeError())
        viewModel.model = modelNoWant
        assertEquals(context.getString(R.string.rpe_err), viewModel.getWantRpeError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel()
        assertNull(viewModel.getWantRpeError())
        viewModel.model = RpeLoadViewModel.RpeLoadModel(haveRpe = 10.0, haveWeight = 135.0, haveReps = 5, wantReps = 6)
        assertEquals(context.getString(R.string.rpe_err), viewModel.getWantRpeError())
    }

    private fun containsNumbers(str: String?): Boolean {
        return str?.matches(Pattern.compile(".*\\d.*").toRegex()) == true
    }
}