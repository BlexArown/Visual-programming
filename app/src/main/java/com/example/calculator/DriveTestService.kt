package com.example.calculator

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.TrafficStats
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.telephony.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import org.json.JSONArray
import org.json.JSONObject
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.io.File
import java.io.RandomAccessFile
import java.util.concurrent.atomic.AtomicBoolean

class DriveTestService : Service(), LocationListener {

    companion object {
        const val ACTION_DRIVE_TEST_UPDATE = "com.example.calculator.ACTION_DRIVE_TEST_UPDATE"
        const val EXTRA_STATUS = "status"
        const val EXTRA_LAST_JSON = "last_json"

        const val NOTIFICATION_ID = 2020
        const val CHANNEL_ID = "drive_test_channel"

        private const val PREFS_NAME = "drive_test_sync_prefs"
        private const val KEY_LAST_SENT_OFFSET = "last_sent_offset"
    }

    // !!! ПОМЕНЯЙ IP ПОД СВОЙ СЕРВЕР
    private val serverIp = "192.168.0.11"
    private val serverPort = 5555
    private val endpoint = "tcp://$serverIp:$serverPort"

    private val running = AtomicBoolean(false)

    @Volatile
    private var lastLocation: Location? = null

    private lateinit var locationManager: LocationManager
    private lateinit var telephonyManager: TelephonyManager

    private val logFileName = "drive_test_log.jsonl"
    private var workerThread: Thread? = null

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startAsForeground("Drive-test запущен")

        if (running.get()) {
            sendStatus("Сервис уже работает")
            return START_STICKY
        }

        running.set(true)
        subscribeLocation()

        workerThread = Thread {
            zmqSyncLoop()
        }.apply {
            isDaemon = true
            start()
        }

        sendStatus("Drive-test сервис запущен. Endpoint = $endpoint")
        return START_STICKY
    }

    override fun onDestroy() {
        running.set(false)

        try {
            locationManager.removeUpdates(this)
        } catch (_: Exception) {
        }

        try {
            workerThread?.interrupt()
        } catch (_: Exception) {
        }

        workerThread = null
        sendStatus("Drive-test сервис остановлен")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startAsForeground(text: String) {
        createNotificationChannelIfNeeded()
        val notification = buildNotification(text)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or
                        android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Drive Test",
                NotificationManager.IMPORTANCE_LOW
            )
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Drive Test")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun sendStatus(status: String, lastJson: String? = null) {
        val intent = Intent(ACTION_DRIVE_TEST_UPDATE).apply {
            setPackage(packageName)
            putExtra(EXTRA_STATUS, status)
            if (lastJson != null) {
                putExtra(EXTRA_LAST_JSON, lastJson)
            }
        }
        sendBroadcast(intent)
    }

    private fun subscribeLocation() {
        val fineGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted && !coarseGranted) {
            sendStatus("Нет разрешений на локацию")
            return
        }

        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L,
                    1f,
                    this
                )
                lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                sendStatus("Подписка на GPS активна")
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    1f,
                    this
                )
                lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                sendStatus("Подписка на NETWORK_PROVIDER активна")
            } else {
                sendStatus("Провайдеры геолокации отключены")
            }
        } catch (e: Exception) {
            sendStatus("Ошибка подписки на локацию: ${e.message}")
        }
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
        sendStatus("Новая локация: lat=${location.latitude}, lon=${location.longitude}")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {
        sendStatus("Провайдер включён: $provider")
    }

    override fun onProviderDisabled(provider: String) {
        sendStatus("Провайдер выключен: $provider")
    }

    private fun zmqSyncLoop() {
        ZContext().use { ctx ->
            var socket = ctx.createSocket(SocketType.REQ)

            fun recreateSocket() {
                try {
                    socket.close()
                } catch (_: Exception) {
                }

                socket = ctx.createSocket(SocketType.REQ)
                socket.receiveTimeOut = 2000
                socket.sendTimeOut = 2000
                socket.connect(endpoint)
            }

            recreateSocket()
            sendStatus("ZMQ сокет подключён к $endpoint")
            updateNotification("Подключение к серверу...")

            while (running.get()) {
                try {
                    // 1. Собрали новую телеметрию
                    val payload = buildTelemetryJson().toString()

                    // 2. Записали её в локальный лог
                    appendPayloadToLog(payload)

                    // 3. Попробовали синхронизировать весь неотправленный хвост файла
                    syncPendingLog(socket)

                    updateNotification("Синхронизация выполнена")
                    Thread.sleep(5000)
                } catch (e: Exception) {
                    updateNotification("Нет связи: ${e.message}")
                    sendStatus("Ошибка сокета: ${e.message}. Переподключение...")

                    try {
                        recreateSocket()
                    } catch (_: Exception) {
                    }

                    try {
                        Thread.sleep(1500)
                    } catch (_: Exception) {
                    }
                }
            }

            try {
                socket.close()
            } catch (_: Exception) {
            }
        }
    }

    private fun appendPayloadToLog(payload: String) {
        File(filesDir, logFileName).appendText(payload + "\n")
        sendStatus("Новая запись добавлена в лог", payload)
    }

    private fun syncPendingLog(socket: ZMQ.Socket) {
        val file = File(filesDir, logFileName)
        if (!file.exists()) {
            sendStatus("Лог-файл ещё не создан")
            return
        }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        var lastSentOffset = prefs.getLong(KEY_LAST_SENT_OFFSET, 0L)

        if (lastSentOffset > file.length()) {
            lastSentOffset = 0L
            prefs.edit().putLong(KEY_LAST_SENT_OFFSET, 0L).apply()
        }

        RandomAccessFile(file, "r").use { raf ->
            raf.seek(lastSentOffset)

            while (running.get()) {
                val startOffset = raf.filePointer
                val line = raf.readLine() ?: break
                val endOffset = raf.filePointer

                if (line.isBlank()) {
                    prefs.edit().putLong(KEY_LAST_SENT_OFFSET, endOffset).apply()
                    continue
                }

                sendOneLineWithAck(socket, line)

                prefs.edit().putLong(KEY_LAST_SENT_OFFSET, endOffset).apply()

                updateNotification("Синхр. до байта $endOffset")
                sendStatus(
                    "Строка успешно отправлена на сервер. offset=$startOffset..$endOffset",
                    line
                )
            }
        }
    }

    private fun sendOneLineWithAck(socket: ZMQ.Socket, payload: String) {
        val okSend = socket.send(payload.toByteArray(ZMQ.CHARSET), 0)
        if (!okSend) {
            throw RuntimeException("Не удалось отправить сообщение")
        }

        val replyBytes = socket.recv(0)
        if (replyBytes == null) {
            throw RuntimeException("Сервер не ответил (timeout)")
        }

        val reply = String(replyBytes, ZMQ.CHARSET)
        if (reply != "OK") {
            throw RuntimeException("Неожиданный ответ сервера: $reply")
        }
    }

    private fun buildTelemetryJson(): JSONObject {
        val root = JSONObject()
        root.put("type", "telemetry")
        root.put("ts_client_ms", System.currentTimeMillis())
        root.put("location", collectLocationJson())
        root.put("cells", collectCellsJson())
        root.put("traffic", collectTrafficJson())
        return root
    }

    private fun collectLocationJson(): JSONObject {
        val locObj = JSONObject()
        val loc = lastLocation

        if (loc != null) {
            locObj.put("latitude", loc.latitude)
            locObj.put("longitude", loc.longitude)
            locObj.put("altitude", loc.altitude)
            locObj.put("time", loc.time)
            locObj.put("accuracy", loc.accuracy.toDouble())
            locObj.put("provider", loc.provider ?: "unknown")
        } else {
            locObj.put("status", "no_location")
        }

        return locObj
    }

    private fun collectTrafficJson(): JSONObject {
        val t = JSONObject()
        t.put("total_rx_bytes", TrafficStats.getTotalRxBytes())
        t.put("total_tx_bytes", TrafficStats.getTotalTxBytes())
        t.put("top_apps_2sigma", JSONArray())
        t.put("top_apps_note", "")
        return t
    }

    private fun collectCellsJson(): JSONArray {
        val arr = JSONArray()

        val phoneGranted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        if (!phoneGranted) return arr

        val list = try {
            telephonyManager.allCellInfo
        } catch (_: Exception) {
            null
        }

        if (list.isNullOrEmpty()) return arr

        for (cell in list) {
            when (cell) {
                is CellInfoLte -> {
                    val o = JSONObject()
                    val ci = cell.cellIdentity
                    val ss = cell.cellSignalStrength

                    o.put("radio", "LTE")
                    o.put("band", "-")
                    o.put("ci", ci.ci)
                    o.put("earfcn", ci.earfcn)
                    o.put("mcc", ci.mccString ?: "")
                    o.put("mnc", ci.mncString ?: "")
                    o.put("pci", ci.pci)
                    o.put("tac", ci.tac)

                    o.put("asuLevel", ss.asuLevel)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        o.put("cqi", ss.cqi)
                        o.put("rsrp", ss.rsrp)
                        o.put("rsrq", ss.rsrq)
                        o.put("rssi", ss.rssi)
                        o.put("rssnr", ss.rssnr)
                        o.put("timingAdvance", ss.timingAdvance)
                    }
                    arr.put(o)
                }

                is CellInfoGsm -> {
                    val o = JSONObject()
                    val ci = cell.cellIdentity
                    val ss = cell.cellSignalStrength

                    o.put("radio", "GSM")
                    o.put("cid", ci.cid)
                    o.put("bsic", ci.bsic)
                    o.put("arfcn", ci.arfcn)
                    o.put("lac", ci.lac)
                    o.put("mcc", ci.mccString ?: "")
                    o.put("mnc", ci.mncString ?: "")
                    o.put("psc", "-")

                    o.put("dbm", ss.dbm)
                    o.put("rssi", ss.dbm)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        o.put("timingAdvance", ss.timingAdvance)
                    }
                    arr.put(o)
                }

                is CellInfoNr -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val o = JSONObject()
                        val ci = cell.cellIdentity as CellIdentityNr
                        val ss = cell.cellSignalStrength as CellSignalStrengthNr

                        o.put("radio", "NR")
                        o.put("band", "-")
                        o.put("nci", ci.nci)
                        o.put("pci", ci.pci)
                        o.put("nrarfcn", ci.nrarfcn)
                        o.put("tac", ci.tac)
                        o.put("mcc", ci.mccString ?: "")
                        o.put("mnc", ci.mncString ?: "")

                        o.put("ssRsrp", ss.ssRsrp)
                        o.put("ssRsrq", ss.ssRsrq)
                        o.put("ssSinr", ss.ssSinr)
                        o.put("timingAdvance", "-")

                        arr.put(o)
                    }
                }
            }
        }

        return arr
    }
}