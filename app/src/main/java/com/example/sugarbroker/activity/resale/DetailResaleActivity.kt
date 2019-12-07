package com.example.sugarbroker.activity.resale

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.activity_detail_resale.*
import androidx.appcompat.app.AlertDialog
import com.example.sugarbroker.activity.userType


class DetailResaleActivity : AppCompatActivity(), OnMapReadyCallback {

    private val TAG = "DetailResaleActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    var address: String? = null
    var mapTitle: String? = null
    var resaleImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_resale)

        firestoreDB = FirebaseFirestore.getInstance()

        setUpToolbar()

        if (userType == "User" || userType == "Seller") {
            millemail_textview.visibility = View.GONE
            editResale.visibility = View.GONE
        } else {
            millemail_textview.visibility = View.VISIBLE
            editResale.visibility = View.VISIBLE
        }

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateResaleId")
            address = bundle.getString("UpdateResaleAddress")
            mapTitle = bundle.getString("UpdateResaleMillName")
            resaleImageUrl = bundle.getString("UpdateResaleUrl")

            mill_name_textview.setText(bundle.getString("UpdateResaleMillName"))
            price_textview.setText(bundle.getString("UpdateResalePrice"))
            milladdress_textview.setText(bundle.getString("UpdateResaleAddress"))
            millcontact_textview.setText(bundle.getString("UpdateResaleContact"))
            millemail_textview.setText(bundle.getString("UpdateResaleEmail"))
            Glide.with(this@DetailResaleActivity).load(bundle.getString("UpdateResaleUrl"))
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

            val intent = Intent(applicationContext, BuyResaleActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("UpdateResaleId", id)
            intent.putExtra("UpdateResaleMillName", millName)
            intent.putExtra("UpdateResalePrice", price)
            applicationContext.startActivity(intent)

            enableMyLocation()
            finish()

        }

        detail_sugar_image.setOnClickListener(View.OnClickListener {
            val mBuilder = AlertDialog.Builder(this@DetailResaleActivity)
            val mView = layoutInflater.inflate(R.layout.dialog_custom_layout, null)
            val photoView = mView.findViewById<View>(R.id.imageView) as ImageView
            Glide.with(this@DetailResaleActivity).load(bundle!!.getString("UpdateResaleUrl"))
                .placeholder(R.drawable.photoplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    photoView
                )
            mBuilder.setView(mView)
            val mDialog = mBuilder.create()
            mDialog.show()
        })

        callSeller.setOnClickListener {
            val mobNum = bundle!!.getString("UpdateResaleContact")
            val phoneIntent = Intent(
                Intent.ACTION_DIAL, Uri.fromParts(
                    "tel", mobNum, null
                )
            )
            startActivity(phoneIntent)
        }

        editResale.setOnClickListener { updateResale() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        //These coordinates represent the latitude and longitude of the Googleplex.
        val zoomLevel = 4f

        if (address == null || address!!.length == 0) {
            address = "126 Nana Peth, Kamal Mohan Society, Pune - 411002"
        }

        var add = Geocoder(this).getFromLocationName(address!!, 5)

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

    private fun updateResale() {
        val intent = Intent(applicationContext, AddResaleActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateResaleId", id)
        intent.putExtra("UpdateResaleMillName", mill_name_textview.text.toString())
        intent.putExtra("UpdateResalePrice", price_textview.text.toString())
        intent.putExtra("UpdateResaleAddress", milladdress_textview.text.toString())
        intent.putExtra("UpdateResaleContact", millcontact_textview.text.toString())
        intent.putExtra("UpdateResaleEmail", millemail_textview.text.toString())
        intent.putExtra("UpdateResaleUrl", resaleImageUrl)
        applicationContext.startActivity(intent)
    }

}
