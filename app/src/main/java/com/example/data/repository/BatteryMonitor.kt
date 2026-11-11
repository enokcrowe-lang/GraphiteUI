package com.example.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.example.data.model.BatteryInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BatteryMonitor(private val context: Context) {

    private val _batteryInfo = MutableStateFlow(fetchCurrentBattery())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            intent?.let { updateFromIntent(it) }
        }
    }

    private var isReceiverRegistered = false

    fun startListening() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            context.registerReceiver(batteryReceiver, filter)
            isReceiverRegistered = true
        }
        // Also refresh immediately
        fetchCurrentBattery()
    }

    fun stopListening() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(batteryReceiver)
            } catch (_: Exception) {}
            isReceiverRegistered = false
        }
    }

    fun fetchCurrentBattery(): BatteryInfo {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, intentFilter)
        return if (batteryIntent != null) {
            updateFromIntent(batteryIntent)
        } else {
            BatteryInfo()
        }
    }

    private fun updateFromIntent(intent: Intent): BatteryInfo {
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percentage = if (level != -1 && scale > 0) (level * 100 / scale) else 100

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        val chargeType = when {
            !isCharging -> "Discharging"
            chargePlug == BatteryManager.BATTERY_PLUGGED_USB -> "Charging via USB"
            chargePlug == BatteryManager.BATTERY_PLUGGED_AC -> "Fast Charging (AC)"
            chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless Charging"
            else -> "Charging"
        }

        val healthCode = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Optimal (Good)"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Warm (Overheat)"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Degraded"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            else -> "Normal"
        }

        val temperatureRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250)
        val temperatureCelsius = temperatureRaw / 10.0f

        val voltageMilliVolts = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 4100)

        val info = BatteryInfo(
            percentage = percentage,
            isCharging = isCharging,
            chargeType = chargeType,
            health = health,
            temperatureCelsius = temperatureCelsius,
            voltageMilliVolts = voltageMilliVolts
        )
        _batteryInfo.value = info
        return info
    }
}
