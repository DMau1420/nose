package com.example.a

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.ListView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.Timer
import java.util.TimerTask

class RelojMundial : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var timezoneListAdapter: ArrayAdapter<String>
    private val timezones = mutableSetOf<String>()
    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE))
        setContentView(R.layout.activity_reloj_mundial)
        mapView = findViewById(R.id.map)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.controller.setZoom(5.0)
        mapView.controller.setCenter(GeoPoint(20.0, 0.0))
        timezoneListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ArrayList(timezones))
        findViewById<ListView>(R.id.timezone_list).adapter = timezoneListAdapter
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    Log.d("RelojMundial", "Touch at: ${p.latitude}, ${p.longitude}")
                    val timezone = getTimezoneFromGeoPoint(p)
                    if (timezone.isNotEmpty() && !timezones.contains(timezone)) {
                        timezones.add(timezone)
                        updateTimes()

                        val marker = Marker(mapView)
                        marker.position = p
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = timezone
                        mapView.overlays.add(marker)
                        mapView.invalidate()
                    }
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        })
        mapView.overlays.add(mapEventsOverlay)
        // Optional: Add location overlay to show user's location
        val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)

        // Timer to update times every second
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    updateTimes()
                }
            }
        }, 0, 1000)
    }
    private fun getTimezoneFromGeoPoint(geoPoint: GeoPoint): String {
        return when {
            // América
            geoPoint.latitude in 58.0..72.0 && geoPoint.longitude in -180.0..-130.0 -> "America/Anchorage" // Alaska
            geoPoint.latitude in 24.0..49.0 && geoPoint.longitude in -125.0..-66.0 -> "America/New_York" // Este de EE.UU.
            geoPoint.latitude in -12.0..2.0 && geoPoint.longitude in -79.0..-65.0 -> "America/Lima" // Perú
            geoPoint.latitude in 13.0..29.0 && geoPoint.longitude in -118.0..-86.0 -> "America/Mexico_City" // Ciudad de México
            geoPoint.latitude in -56.0..-18.0 && geoPoint.longitude in -75.0..-65.0 -> "America/Santiago" // Chile
            geoPoint.latitude in -10.0..12.0 && geoPoint.longitude in -78.0..-66.0 -> "America/Bogota" // Colombia
            geoPoint.latitude in -12.0..12.0 && geoPoint.longitude in -72.0..-63.0 -> "America/Caracas" // Venezuela
            geoPoint.latitude in -22.0..-9.0 && geoPoint.longitude in -69.0..-57.0 -> "America/La_Paz" // Bolivia
            geoPoint.latitude in -27.0..-14.0 && geoPoint.longitude in -62.0..-54.0 -> "America/Asuncion" // Paraguay
            geoPoint.latitude in -35.0..-30.0 && geoPoint.longitude in -57.0..-53.0 -> "America/Montevideo" // Uruguay
// Canadá
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Toronto" // Toronto
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Montreal" // Montreal
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Vancouver" // Vancouver
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Edmonton" // Edmonton
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Winnipeg" // Winnipeg
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Halifax" // Halifax
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/St_Johns" // St. John's
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Yellowknife" // Yellowknife
            geoPoint.latitude in 42.0..84.0 && geoPoint.longitude in -141.0..-52.0 -> "America/Iqaluit" // Iqaluit
// Europa
            geoPoint.latitude in 36.0..61.0 && geoPoint.longitude in -8.0..32.0 -> "Europe/London" // Reino Unido
            geoPoint.latitude in 42.0..52.0 && geoPoint.longitude in -5.0..11.0 -> "Europe/Paris" // Francia
            geoPoint.latitude in 47.0..55.0 && geoPoint.longitude in 5.0..15.0 -> "Europe/Berlin" // Alemania
            geoPoint.latitude in 36.0..46.0 && geoPoint.longitude in 6.0..19.0 -> "Europe/Rome" // Italia
            geoPoint.latitude in 37.0..44.0 && geoPoint.longitude in -10.0..5.0 -> "Europe/Madrid" // España
            geoPoint.latitude in 50.0..54.0 && geoPoint.longitude in 2.0..6.0 -> "Europe/Amsterdam" // Países Bajos
            geoPoint.latitude in 49.0..51.0 && geoPoint.longitude in 2.0..6.0 -> "Europe/Brussels" // Bélgica
            geoPoint.latitude in 55.0..59.0 && geoPoint.longitude in 11.0..18.0 -> "Europe/Stockholm" // Suecia
            geoPoint.latitude in 37.0..41.0 && geoPoint.longitude in 22.0..28.0 -> "Europe/Athens" // Grecia
            geoPoint.latitude in 49.0..54.0 && geoPoint.longitude in 14.0..24.0 -> "Europe/Warsaw" // Polonia
            geoPoint.latitude in 47.0..51.0 && geoPoint.longitude in 16.0..23.0 -> "Europe/Prague" // República Checa
            geoPoint.latitude in 47.0..49.0 && geoPoint.longitude in 15.0..18.0 -> "Europe/Vienna" // Austria
            geoPoint.latitude in 46.0..48.0 && geoPoint.longitude in 6.0..10.0 -> "Europe/Zurich" // Suiza
// África
            geoPoint.latitude in -35.0..35.0 && geoPoint.longitude in -20.0..60.0 -> "Africa/Johannesburg" // Sudáfrica
            geoPoint.latitude in -30.0..10.0 && geoPoint.longitude in 15.0..45.0 -> "Africa/Nairobi" // Kenia
// Asia
            geoPoint.latitude in 5.0..35.0 && geoPoint.longitude in 60.0..105.0 -> "Asia/Kolkata" // India
            geoPoint.latitude in 25.0..45.0 && geoPoint.longitude in 100.0..140.0 -> "Asia/Shanghai" // China
            geoPoint.latitude in -10.0..10.0 && geoPoint.longitude in 100.0..150.0 -> "Asia/Singapore" // Singapur
            geoPoint.latitude in 30.0..40.0 && geoPoint.longitude in 30.0..50.0 -> "Asia/Jerusalem" // Israel
            geoPoint.latitude in 35.0..45.0 && geoPoint.longitude in 35.0..45.0 -> "Asia/Baghdad" // Irak
// Australia
            geoPoint.latitude in -40.0..-10.0 && geoPoint.longitude in 110.0..155.0 -> "Australia/Sydney" // Australia (Este)
            geoPoint.latitude in -40.0..-10.0 && geoPoint.longitude in 110.0..155.0 -> "Australia/Brisbane" // Australia (Este)
// Oceanía
            geoPoint.latitude in -10.0..10.0 && geoPoint.longitude in 140.0..180.0 -> "Pacific/Guam" // Guam
            geoPoint.latitude in -50.0..-10.0 && geoPoint.longitude in 140.0..180.0 -> "Pacific/Auckland" // Nueva Zelanda
            geoPoint.latitude in -20.0..0.0 && geoPoint.longitude in -130.0..-70.0 -> "Pacific/Honolulu" // Hawái
// Japón
            geoPoint.latitude in 24.0..46.0 && geoPoint.longitude in 122.0..153.0 -> "Asia/Tokyo" // Japón
// Corea del Sur
            geoPoint.latitude in 33.0..39.0 && geoPoint.longitude in 124.0..130.0 -> "Asia/Seoul" // Corea del Sur
// Taiwán
            geoPoint.latitude in 21.0..26.0 && geoPoint.longitude in 118.0..123.0 -> "Asia/Taipei" // Taiwán
// Filipinas
            geoPoint.latitude in 4.0..21.0 && geoPoint.longitude in 115.0..127.0 -> "Asia/Manila" // Filipinas
// Tailandia
            geoPoint.latitude in 5.0..21.0 && geoPoint.longitude in 97.0..105.0 -> "Asia/Bangkok" // Tailandia
// Indonesia (Yakarta)
            geoPoint.latitude in -12.0..0.0 && geoPoint.longitude in 105.0..120.0 -> "Asia/Jakarta" // Indonesia
// Nueva Zelanda
            geoPoint.latitude in -47.0..0.0 && geoPoint.longitude in 165.0..180.0 -> "Pacific/Auckland" // Nueva Zelanda
// Sudáfrica
            geoPoint.latitude in -35.0..-22.0 && geoPoint.longitude in 16.0..33.0 -> "Africa/Johannesburg" // Sudáfrica
// Egipto
            geoPoint.latitude in 22.0..32.0 && geoPoint.longitude in 25.0..35.0 -> "Africa/Cairo" // Egipto
// Arabia Saudita
            geoPoint.latitude in 15.0..32.0 && geoPoint.longitude in 34.0..56.0 -> "Asia/Riyadh" // Arabia Saudita
// Emiratos Árabes Unidos
            geoPoint.latitude in 22.0..26.0 && geoPoint.longitude in 51.0..57.0 -> "Asia/Dubai" // Emiratos Árabes Unidos
            geoPoint.latitude in -55.0..-20.0 && geoPoint.longitude in -75.0..-53.0 -> "America/Argentina/Buenos_Aires" // Buenos Aires
            geoPoint.latitude in -33.0..5.0 && geoPoint.longitude in -75.0..-35.0 -> "America/Sao_Paulo" // São Paulo
            // Rusia
            geoPoint.latitude in 55.0..70.0 && geoPoint.longitude in 20.0..45.0 -> "Europe/Moscow" // Moscú
            geoPoint.latitude in 56.0..69.0 && geoPoint.longitude in 30.0..41.0 -> "Europe/Saint_Petersburg" // San Petersburgo
            geoPoint.latitude in 56.0..69.0 && geoPoint.longitude in 65.0..85.0 -> "Asia/Yekaterinburg" // Ekaterimburgo
            geoPoint.latitude in 53.0..56.0 && geoPoint.longitude in 87.0..105.0 -> "Asia/Omsk" // Omsk
            geoPoint.latitude in 51.0..56.0 && geoPoint.longitude in 56.0..87.0 -> "Asia/Novosibirsk" // Novosibirsk
            geoPoint.latitude in 54.0..57.0 && geoPoint.longitude in 36.0..41.0 -> "Europe/Volgograd" // Volgogrado
            geoPoint.latitude in 44.0..55.0 && geoPoint.longitude in 38.0..56.0 -> "Europe/Samara" // Samara
            geoPoint.latitude in 43.0..55.0 && geoPoint.longitude in 42.0..49.0 -> "Europe/Saratov" // Saratov
            geoPoint.latitude in 45.0..58.0 && geoPoint.longitude in 35.0..51.0 -> "Europe/Ulyanovsk" // Ulán-Udé
            geoPoint.latitude in 56.0..58.0 && geoPoint.longitude in 64.0..67.0 -> "Asia/Chita" // Chita
            geoPoint.latitude in 46.0..52.0 && geoPoint.longitude in 48.0..55.0 -> "Europe/Samara" // Kazán
            geoPoint.latitude in 51.0..55.0 && geoPoint.longitude in 55.0..60.0 -> "Europe/Samara" // Nizhni Nóvgorod
            geoPoint.latitude in 48.0..55.0 && geoPoint.longitude in 135.0..150.0 -> "Asia/Khabarovsk" // Jabárovsk
            geoPoint.latitude in 53.0..55.0 && geoPoint.longitude in 158.0..165.0 -> "Asia/Magadan" // Magadán
            geoPoint.latitude in 53.0..56.0 && geoPoint.longitude in 60.0..71.0 -> "Asia/Yakutsk" // Yakutsk
            geoPoint.latitude in 57.0..59.0 && geoPoint.longitude in 65.0..68.0 -> "Asia/Irkutsk" // Irkutsk
            geoPoint.latitude in 67.0..70.0 && geoPoint.longitude in 135.0..148.0 -> "Asia/Anadyr" // Anádyr
            geoPoint.latitude in 51.0..53.0 && geoPoint.longitude in 157.0..165.0 -> "Asia/Srednekolymsk" // Srednekolimsk
            geoPoint.latitude in 53.0..57.0 && geoPoint.longitude in 142.0..156.0 -> "Asia/Ust-Nera" // Ust-Nera
            geoPoint.latitude in 61.0..67.0 && geoPoint.longitude in 129.0..141.0 -> "Asia/Vladivostok" // Vladivostok
            geoPoint.latitude in 61.0..67.0 && geoPoint.longitude in 151.0..163.0 -> "Asia/Sakhalin" // Sajalín
            geoPoint.latitude in 60.0..62.0 && geoPoint.longitude in 56.0..60.0 -> "Europe/Kaliningrad" // Kaliningrado
            geoPoint.latitude in 67.0..71.0 && geoPoint.longitude in 170.0..180.0 -> "Asia/Anadyr" // Norilsk
            // Default
            else -> ""
        }
    }
    private fun updateTimes() {
        val updatedTimes = timezones.map { timezone ->
            val cal = Calendar.getInstance(TimeZone.getTimeZone(timezone))
            dateFormat.timeZone = TimeZone.getTimeZone(timezone)  // Set the formatter's timezone
            val time = dateFormat.format(cal.time)
            Log.d("RelojMundial", "Timezone: $timezone, Time: $time")
            "$timezone: $time"
        }
        timezoneListAdapter.clear()
        timezoneListAdapter.addAll(updatedTimes)
        timezoneListAdapter.notifyDataSetChanged()
    }
}