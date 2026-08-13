package com.example.watertracker

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionParametersOf
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class WaterWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val healthManager = HealthConnectManager(context)

        val totalTodayMl = try {
            healthManager.getTodayWaterTotalMl()
        } catch (e: Exception) {
            0.0
        }

        provideContent {
            WidgetContent(totalTodayMl.toInt())
        }
    }

    @Composable
    private fun WidgetContent(totalMl: Int) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(12.dp)
                .background(ColorProvider(Color(0xFFE3F2FD))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Oggi: $totalMl ml",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(Color(0xFF0277BD))
                )
            )

            Spacer(modifier = GlanceModifier.height(8.dp))

            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuickAddButton("+250ml", 250.0)
                Spacer(modifier = GlanceModifier.width(4.dp))
                QuickAddButton("+330ml", 330.0)
                Spacer(modifier = GlanceModifier.width(4.dp))
                QuickAddButton("+500ml", 500.0)
            }
        }
    }

    @Composable
    private fun QuickAddButton(label: String, amountMl: Double) {
        Button(
            text = label,
            onClick = actionRunCallback<AddWaterAction>(
                actionParametersOf(AmountMlKey to amountMl)
            )
        )
    }
}
