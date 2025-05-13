package com.es.trackmyrideapp.ui.screens.vehiclesScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.PedalBike
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.es.trackmyrideapp.R
import com.es.trackmyrideapp.ui.components.CustomButton
import com.es.trackmyrideapp.ui.components.VehicleFilter
import com.es.trackmyrideapp.ui.components.VehicleFilterSelector
import com.es.trackmyrideapp.ui.components.VehicleIcon
import com.es.trackmyrideapp.ui.components.VehicleType

@Composable
fun VehiclesScreen(
    modifier: Modifier = Modifier
){
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf("") }
    var tankCapacity by remember { mutableStateOf("") }
    var efficiency by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var bikeType by remember { mutableStateOf("") }

    // TODO: Obtenerlo del viewmodel
    // val selectedFilter by viewModel.selectedFilter.collectAsState()
    var selectedFilter by remember { mutableStateOf<VehicleFilter>(VehicleFilter.Type(VehicleType.Car)) }

    /*
    Si después quiero que al seleccionar un tipo de vehículo se actualice otra info (ej. una lista filtrada, o los datos de un formulario), lo podés observar en el ViewModel usando un combine() o reaccionando a los cambios de selectedFilter.
     */


    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 30.dp)
            .padding(top = 16.dp)
            .navigationBarsPadding()
    ){

        VehicleFilterSelector(
            selectedFilter = selectedFilter,
            onFilterSelected = { selectedFilter = it },
            paddingScreen = 60.dp
        )


        Spacer(modifier = Modifier.height(32.dp))

        when (val filter = selectedFilter) {
            is VehicleFilter.Type -> when (filter.type) {
                VehicleType.Car -> EngineVehicleScreen(
                    icon = VehicleIcon.Vector(Icons.Default.DirectionsCar),
                    vehicleLabel = "Carro wey",
                    name = name,
                    brand = brand,
                    model = model,
                    year = year,
                    fuelType = fuelType,
                    tankCapacity = tankCapacity,
                    efficiency = efficiency,
                    notes = notes,
                    onNameChange = { name = it },
                    onBrandChange = { brand = it },
                    onModelChange = { model = it },
                    onYearChange = { year = it },
                    onFuelTypeChange = { fuelType = it },
                    onTankCapacityChange = { tankCapacity = it },
                    onEfficiencyChange = { efficiency = it },
                    onNotesChange = { notes = it }
                )

                VehicleType.MotorCycle -> EngineVehicleScreen(
                    icon = VehicleIcon.PainterIcon(R.drawable.motocicleta),
                    vehicleLabel = "MotorCycle wey",
                    name = name,
                    brand = brand,
                    model = model,
                    year = year,
                    fuelType = fuelType,
                    tankCapacity = tankCapacity,
                    efficiency = efficiency,
                    notes = notes,
                    onNameChange = { name = it },
                    onBrandChange = { brand = it },
                    onModelChange = { model = it },
                    onYearChange = { year = it },
                    onFuelTypeChange = { fuelType = it },
                    onTankCapacityChange = { tankCapacity = it },
                    onEfficiencyChange = { efficiency = it },
                    onNotesChange = { notes = it }
                )

                VehicleType.Bike -> BikeScreen(
                    icon = Icons.Default.PedalBike,
                    vehicleLabel = "Bike",
                    name = name,
                    brand = brand,
                    model = model,
                    year = year,
                    bikeType = bikeType,
                    notes = notes,
                    onNameChange = { name = it },
                    onBrandChange = { brand = it },
                    onModelChange = { model = it },
                    onYearChange = { year = it },
                    onBikeTypeChange = { bikeType = it },
                    onNotesChange = { notes = it }
                )
            }

            VehicleFilter.All -> {
                // Nada en este caso
            }

        }


        Spacer(Modifier.weight(1f))

        Column(
            Modifier.padding(vertical = 32.dp)
        ) {
            CustomButton(
                onclick = {/*TODO: Save changes vehicle*/},
                text = "Save changes",
                buttonColor = MaterialTheme.colorScheme.primary,
                fontColor = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun <T> IconSelectorBar(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    itemIcon: @Composable (T) -> Unit,
    paddingScreen: Dp,
    modifier: Modifier = Modifier
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemPadding = 4.dp
    val itemWidth = (screenWidth - paddingScreen - itemPadding * (items.count() * 2)) / 3f

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    RoundedCornerShape(50)
                )
                .padding(itemPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                val isSelected = item == selectedItem
                Button(
                    onClick = { onItemSelected(item) },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentColor = colorResource(R.color.black)
                    ),
                    elevation = null,
                    modifier = Modifier
                        .width(itemWidth)
                        .height(40.dp)
                ) {
                    itemIcon(item)
                }
            }
        }
    }
}



@Composable
@Preview
fun testest(){
    VehiclesScreen()
}