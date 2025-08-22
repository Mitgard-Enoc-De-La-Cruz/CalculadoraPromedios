package com.example.calculadorapromedios

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.calculadorapromedios.databinding.ActivityMainBinding
import com.example.calculadorapromedios.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupListeners()
        setupValidaciones()
    }

    private fun setupObservers() {
        viewModel.promedio.observe(this, Observer { promedio ->
            binding.tvResultado.text = String.format("%.2f", promedio)
        })

        viewModel.promedioRedondeado.observe(this, Observer { redondeado ->
            redondeado?.let {
                binding.tvRedondeado.text = "Redondeado: ${String.format("%.2f", it)}"
            }
        })

        viewModel.estado.observe(this, Observer { estado ->
            binding.tvEstado.text = estado
            cambiarColorEstado(estado)
        })

        viewModel.guardarMensaje.observe(this, Observer { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        })

        viewModel.errorValidacion.observe(this, Observer { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupListeners() {
        binding.btnCalcular.setOnClickListener {
            try {
                val n1 = binding.etCalificacion1.text.toString().toDoubleOrNull() ?: 0.0
                val n2 = binding.etCalificacion2.text.toString().toDoubleOrNull() ?: 0.0
                val n3 = binding.etCalificacion3.text.toString().toDoubleOrNull() ?: 0.0
                viewModel.calcularPromedio(n1, n2, n3)
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Por favor ingresa números válidos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRedondear.setOnClickListener {
            viewModel.redondearPromedio()
        }

        binding.btnLimpiar.setOnClickListener {
            binding.etCalificacion1.text?.clear()
            binding.etCalificacion2.text?.clear()
            binding.etCalificacion3.text?.clear()
            viewModel.limpiarResultados()
            binding.tvRedondeado.text = ""
        }
    }

    private fun setupValidaciones() {
        val editTexts = listOf(binding.etCalificacion1, binding.etCalificacion2, binding.etCalificacion3)

        editTexts.forEach { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val texto = s.toString()
                    if (texto.isNotEmpty()) {
                        val esValido = viewModel.validarCalificacion(texto)
                        // Mostrar error visualmente cambiando el fondo
                        if (!esValido) {
                            editText.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_light))
                        } else {
                            editText.setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.transparent))
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun cambiarColorEstado(estado: String) {
        val colorRes = when {
            estado.contains("Excelente") -> android.R.color.holo_green_dark
            estado.contains("Buen trabajo") -> android.R.color.holo_blue_dark
            estado.contains("Aprobado") -> android.R.color.holo_orange_dark
            else -> android.R.color.holo_red_dark
        }
        binding.tvEstado.setTextColor(ContextCompat.getColor(this, colorRes))
    }
}