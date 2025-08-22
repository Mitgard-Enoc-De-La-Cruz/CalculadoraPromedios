package com.example.calculadorapromedios.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.calculadorapromedios.viewmodel.data.FakeRepository

class MainViewModel : ViewModel() {

    private val repository = FakeRepository()

    private val _promedio = MutableLiveData<Double>(0.0)
    val promedio: LiveData<Double> get() = _promedio

    private val _promedioRedondeado = MutableLiveData<Double?>(null)
    val promedioRedondeado: LiveData<Double?> get() = _promedioRedondeado

    private val _estado = MutableLiveData<String>("Ingresa las calificaciones")
    val estado: LiveData<String> get() = _estado

    private val _guardarMensaje = MutableLiveData<String?>(null)
    val guardarMensaje: LiveData<String?> get() = _guardarMensaje

    private val _errorValidacion = MutableLiveData<String?>(null)
    val errorValidacion: LiveData<String?> get() = _errorValidacion

    fun calcularPromedio(n1: Double, n2: Double, n3: Double) {
        // Validar que las calificaciones estén entre 0 y 20
        val calificaciones = listOf(n1, n2, n3)
        val invalidas = calificaciones.any { it < 0 || it > 20 }

        if (invalidas) {
            _errorValidacion.value = "Las calificaciones deben estar entre 0 y 20"
            return
        }

        val result = (n1 + n2 + n3) / 3
        _promedio.value = result
        _promedioRedondeado.value = null
        _estado.value = determinarEstado(result)
        _guardarMensaje.value = repository.guardarPromedio(result)
        _errorValidacion.value = null
    }

    fun redondearPromedio() {
        val promedioActual = _promedio.value ?: 0.0
        val redondeado = Math.round(promedioActual * 100.0) / 100.0
        _promedioRedondeado.value = redondeado
    }

    fun limpiarResultados() {
        _promedio.value = 0.0
        _promedioRedondeado.value = null
        _estado.value = "Ingresa las calificaciones"
        _guardarMensaje.value = null
        _errorValidacion.value = null
    }

    private fun determinarEstado(promedio: Double): String {
        return when {
            promedio >= 16 -> "¡Excelente! 🎉"
            promedio >= 13 -> "Buen trabajo 👍"
            promedio >= 10 -> "Aprobado ✅"
            else -> "Necesitas mejorar 📚"
        }
    }

    fun validarCalificacion(calificacion: String): Boolean {
        return try {
            val valor = calificacion.toDouble()
            valor in 0.0..20.0
        } catch (e: NumberFormatException) {
            false
        }
    }
}