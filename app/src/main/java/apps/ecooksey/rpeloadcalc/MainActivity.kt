package apps.ecooksey.rpeloadcalc

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import apps.ecooksey.rpeloadcalc.databinding.ActivityMainBinding
import apps.ecooksey.rpeloadcalc.utils.hideSoftKeyboard

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(RpeLoadViewModel::class.java)
        binding.licensing.movementMethod = LinkMovementMethod.getInstance()
        binding.viewModel = viewModel
        binding.btnCalculate.setOnClickListener {
            binding.viewModel?.model = RpeLoadViewModel.RpeLoadModel(
                haveWeight = binding.haveWeight.text.toString().toDoubleOrNull(),
                haveReps = binding.haveReps.text.toString().toIntOrNull(),
                haveRpe = binding.haveRpe.text.toString().toDoubleOrNull(),
                wantReps = binding.wantReps.text.toString().toIntOrNull(),
                wantRpe = binding.wantRpe.text.toString().toDoubleOrNull()
            )
            hideSoftKeyboard()
        }
    }
}
