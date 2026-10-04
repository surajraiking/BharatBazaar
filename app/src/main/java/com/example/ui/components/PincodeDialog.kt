package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.FlipkartBlue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChangePincodeDialog(
  currentPincode: String,
  onDismiss: () -> Unit,
  onPincodeSubmit: (String) -> Unit
) {
  var pinInput by remember { mutableStateOf(currentPincode) }
  val quickPincodes = listOf(
    "110001" to "Delhi",
    "400001" to "Mumbai",
    "560001" to "Bengaluru",
    "700001" to "Kolkata",
    "600001" to "Chennai"
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = Color.White,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("pincode_dialog")
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = FlipkartBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Choose Delivery Location",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Enter 6-digit Indian PIN code to check delivery speed and payment options at your doorstep.",
          fontSize = 12.sp,
          color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
          value = pinInput,
          onValueChange = { if (it.length <= 6) pinInput = it },
          label = { Text("Enter 6-digit PIN code") },
          placeholder = { Text("e.g. 110001") },
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pincode_input_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Popular Metro Hubs:",
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          quickPincodes.forEach { (code, city) ->
            AssistChip(
              onClick = {
                pinInput = code
                onPincodeSubmit(code)
                onDismiss()
              },
              label = { Text("$city ($code)", fontSize = 11.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            onPincodeSubmit(pinInput)
            onDismiss()
          },
          enabled = pinInput.length == 6,
          colors = ButtonDefaults.buttonColors(containerColor = FlipkartBlue),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("apply_pincode_button")
        ) {
          Text("Check & Apply", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
