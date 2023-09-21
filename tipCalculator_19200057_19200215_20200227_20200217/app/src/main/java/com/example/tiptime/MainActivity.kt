/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
//Esta función se encarga de crear la interfaz de usuario de la aplicación de cálculo de propina.
fun TipTimeLayout() {
    // Variables para almacenar la entrada del usuario
    var amountInput by remember { mutableStateOf("") } //cantidad factura
    var tipInput by remember { mutableStateOf("") } //porcentaje de propina
    var roundUp by remember { mutableStateOf(false) } //roundUp del switch

    // Convertir la entrada de usuario en valores numéricos
    //si falla la conversión, por defecto se pone 0.0
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0

    // Calcular la propina para almacenarla, llamando a la función private CalculateTIP
    val tip = calculateTip(amount, tipPercent, roundUp)

    // Obtener el administrador de enfoque para ocultar el teclado cuando sea necesario
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la aplicación
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(alignment = Alignment.Start)
        )

        // Campo de entrada para la cantidad de la factura
        EditNumberField(
            label = R.string.bill_amount,
            leadingIcon = R.drawable.money,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            value = amountInput,
            onValueChanged = { amountInput = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )

        // Campo de entrada para el porcentaje de propina
        EditNumberField(
            label = R.string.how_was_the_service,
            leadingIcon = R.drawable.percent,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            value = tipInput,
            onValueChanged = { tipInput = it },
            modifier = Modifier.padding(bottom = 32.dp).fillMaxWidth(),
        )

        // Opción para redondear la propina
        RoundTheTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it }, //GESTIONADOR DE CAMBIOS DEL PORCENTAJE
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Mostrar el resultado del cálculo de la propina
        Text(
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )

        // Espacio en blanco
        Spacer(modifier = Modifier.height(150.dp))
    }
}
//Compose diseñado para crear un campo de entrada de texto que se utiliza para ingresar números.
@Composable
fun EditNumberField(
    @StringRes label: Int,
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,
    //funcion gestiona los cambios de estado, se invoca cada vez que el usuario modifica el texto
    onValueChanged: (String) -> Unit, //gestiona el cambio que sucede en el string
    modifier: Modifier = Modifier
) {
    // Campo de entrada de texto
    TextField(
        value = value,
        singleLine = true,
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        modifier = modifier,
        onValueChange = onValueChanged, // funcion que verifica el cambio del estado
        label = { Text(stringResource(label)) },
        keyboardOptions = keyboardOptions
    )
}

@Composable
//funcion para crear el interruptor
fun RoundTheTipRow(
    roundUp: Boolean, //variable booleana
    onRoundUpChanged: (Boolean) -> Unit, //gestiona el cambio de valor del interruptor
    modifier: Modifier = Modifier
) {
    // Fila con interruptor para redondear la propina
    Row(
        modifier = modifier
            .fillMaxWidth()
            .size(48.dp),
    ) {
        Text(text = stringResource(R.string.round_up_tip))
        Switch(
            modifier = Modifier //modificaciones en el diseño
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp, //variable booleana
            onCheckedChange = onRoundUpChanged   //llama al gestionador de cambios para el interruptor
        )
    }
}

/**
 * Calcula la propina en función de la cantidad de la factura, el porcentaje de propina
 * y la opción de redondeo.
 */
@VisibleForTesting//testear
//Se le borra el private
internal fun calculateTip(amount: Double, tipPercent: Double = 15.0, roundUp: Boolean): String {
    var tip = tipPercent / 100 * amount
    if (roundUp) {
        tip = kotlin.math.ceil(tip) //funcion que redondea hacia arriba la propina
    }
    return NumberFormat.getCurrencyInstance().format(tip) //number.format retorna el tipo de moneda depende del lugar
}
//vista previa
@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}