package com.example.watertracker

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

val AmountMlKey = ActionParameters.Key<Double>("amount_ml")

class AddWaterAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val amountMl = parameters[AmountMlKey] ?: return
        val healthManager = HealthConnectManager(context)

        try {
            healthManager.logWater(amountMl)
            WaterWidget().update(context, glanceId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
