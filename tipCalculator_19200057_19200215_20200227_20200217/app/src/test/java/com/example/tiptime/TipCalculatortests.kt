package com.example.tiptime
import org.junit.Test
import java.text.NumberFormat
import org.junit.Assert.assertEquals

class TipCalculatorTests {

    // Esta es una función de prueba que verifica el cálculo de la propina cuando
    // el porcentaje de propina es del 20% y no se redondea.
    @Test
    fun calculateTip_20PercentNoRoundup() {

        // Se define el monto total de la factura como 10.00
        val amount = 10.00

        // Se define el porcentaje de propina como 20.00
        val tipPercent = 20.00

        // Se calcula el valor esperado de la propina utilizando NumberFormat para
        // formatearlo como una moneda con dos decimales. En este caso, 20% de $10.00 es $2.00.
        val expectedTip = NumberFormat.getCurrencyInstance().format(2)

        // Se llama a la función calculateTip con los valores de monto, porcentaje de propina
        // y la bandera "false" que indica que no se redondeará la propina.
        val actualTip = calculateTip(amount = amount, tipPercent = tipPercent, false)

        // Se utiliza assertEquals para verificar que el valor calculado de la propina
        // (actualTip) sea igual al valor esperado (expectedTip).
        assertEquals(expectedTip, actualTip)
    }
}