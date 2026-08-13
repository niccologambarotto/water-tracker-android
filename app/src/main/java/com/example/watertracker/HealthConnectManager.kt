package com.example.watertracker

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Volume
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class HealthConnectManager(private val context: Context) {
    val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissions = setOf(
        HealthPermission.getWritePermission(HydrationRecord::class),
        HealthPermission.getReadPermission(HydrationRecord::class)
    )

    suspend fun hasPermissions(): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    suspend fun logWater(amountMl: Double) {
        val now = Instant.now()
        val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(now)

        val record = HydrationRecord(
            startTime = now.minusSeconds(60),
            startZoneOffset = zoneOffset,
            endTime = now,
            endZoneOffset = zoneOffset,
            volume = Volume.milliliters(amountMl)
        )

        healthConnectClient.insertRecords(listOf(record))
    }

    suspend fun getTodayWaterTotalMl(): Double {
        val zoneId = ZoneId.systemDefault()
        val startTime = LocalDate.now(zoneId).atStartOfDay(zoneId).toInstant()
        val endTime = Instant.now()

        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(HydrationRecord.VOLUME_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )

        return response[HydrationRecord.VOLUME_TOTAL]?.inMilliliters ?: 0.0
    }
}
