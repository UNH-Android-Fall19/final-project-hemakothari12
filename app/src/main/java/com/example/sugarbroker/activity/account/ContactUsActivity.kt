package com.example.sugarbroker.activity.account

import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.sugarbroker.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        setUpToolbar()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        callAdmin.setOnClickListener {
            callAdmin()
        }

        emailAdmin.setOnClickListener {
            sendEmail()
        }

        call_cv.setOnClickListener { callAdmin() }
        call2_cv.setOnClickListener { callAdminAlternate() }
        email_cv.setOnClickListener { sendEmail() }
    }

    private fun callAdmin() {
        val mobNum = "+919422003575"
        val phoneIntent = Intent(
            Intent.ACTION_DIAL, Uri.fromParts(
                "tel", mobNum, null
            )
        )
        startActivity(phoneIntent)
    }

    private fun callAdminAlternate() {
        val mobNum = "+912024270525"
        val phoneIntent = Intent(
            Intent.ACTION_DIAL, Uri.fromParts(
                "tel", mobNum, null
            )
        )
        startActivity(phoneIntent)
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/html"
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { "jitendrarkothari@gmail.com" })
        intent.putExtra(Intent.EXTRA_SUBJECT, "Inquire about sugar price")
        intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val zoomLevel = 4f

            val address = "126 Nana Peth, Kamal Mohan Society, Pune - 411002"

        val add = Geocoder(this).getFromLocationName(address, 5)

        val location = add[0]
        val homeLatLng = LatLng(location.getLatitude(), location.getLongitude())

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))
        map.addMarker(MarkerOptions().position(homeLatLng).title("JituPune"))
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.elevation = 4.0F
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
        val count = supportFragmentManager.backStackEntryCount
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            if (count == 0)
                super.onBackPressed()
            else
                supportFragmentManager.popBackStack()
        })
    }
}
