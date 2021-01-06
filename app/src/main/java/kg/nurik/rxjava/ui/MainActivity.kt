package kg.nurik.rxjava.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import kg.nurik.rxjava.databinding.ActivityMainBinding
import kg.nurik.rxjava.utils.doAfterTextChanged
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val vm by viewModel<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.etSearch.doAfterTextChanged {
            vm.search(it)
        }
        vm.data.observe(this, Observer {
            Toast.makeText(this, "est chto to", Toast.LENGTH_SHORT).show()
        })
        vm.progress.observe(this, Observer {
            if (it) binding.progress.visibility = View.VISIBLE
            else binding.progress.visibility = View.GONE
        })
    }
}