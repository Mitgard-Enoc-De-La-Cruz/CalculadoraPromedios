package com.example.calculadorapromedios.viewmodel.data

class FakeRepository {

    fun guardarPromedio(promedio: Double): String {

        return "Promedio ${String.format("%.2f", promedio)} guardado exitosamente"
    }
}