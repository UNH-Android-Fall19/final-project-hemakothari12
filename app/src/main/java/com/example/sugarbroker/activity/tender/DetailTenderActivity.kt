package com.example.sugarbroker.activity.tender

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sugarbroker.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail_tender.*



class DetailTenderActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "DetailTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    var address: String? = null
    var mapTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()
            address = bundle.getString("UpdateTenderAddress")
            mapTitle = bundle.getString("UpdateTenderMillName")

            mill_name_textview.setText(bundle.getString("UpdateTenderMillName"))
            price_textview.setText(bundle.getString("UpdateTenderPrice"))
            milladdress_textview.setText(bundle.getString("UpdateTenderAddress"))
            millcontact_textview.setText(bundle.getString("UpdateTenderContact"))
            Glide.with(this@DetailTenderActivity).load(bundle.getString("UpdateTenderUrl"))
                .placeholder(R.drawable.photoplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    detail_sugar_image
                )
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        buy_button.setOnClickListener {
            val millName = mill_name_textview.text.toString()
            val price = price_textview.text.toString()

            val intent = Intent(applicationContext, BuyTenderActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("UpdateTenderId", id)
            intent.putExtra("UpdateTenderMillName", millName)
            intent.putExtra("UpdateTenderPrice", price)
            applicationContext.startActivity(intent)

            enableMyLocation()
            finish()

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //These coordinates represent the latitude and longitude of the Googleplex.
        val zoomLevel = 4f

        val add = Geocoder(this).getFromLocationName(address!!, 5)

        val location = add[0]
        val homeLatLng = LatLng(location.getLatitude(), location.getLongitude())

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        map.addMarker(MarkerOptions().position(homeLatLng).title(mapTitle))
    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.setMyLocationEnabled(true)
        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }

}
