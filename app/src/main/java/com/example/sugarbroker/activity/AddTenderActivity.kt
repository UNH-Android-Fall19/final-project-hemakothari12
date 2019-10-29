package com.example.sugarbroker.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sugarbroker.R
import com.example.sugarbroker.model.Tender
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_tender.*
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoFileUtil
import kotlinx.android.synthetic.main.activity_add_tender.add_button

class AddTenderActivity : AppCompatActivity() {

    private val TAG = "AddTenderActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tender)

        firestoreDB = FirebaseFirestore.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateTenderId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateTenderMillName"))
            price_edittext.setText(bundle.getString("UpdateTenderPrice"))
            milladdress_edittext.setText(bundle.getString("UpdateTenderAddress"))
            millcontact_edittext.setText(bundle.getString("UpdateTenderContact"))
//            sugar_image.setText(bundle.getString("UpdateTenderUrl"))
        }

        prepareCroperino()
        if (title.isNotEmpty()) {
            if (id!!.isNotEmpty()) {
                add_button.text = "Update"
            } else {
                add_button.text = "Add"
            }
        }

        sugar_image.setOnClickListener {
            System.out.println("camera in use")
            addSugarButtonClicked()
        }

        add_button.setOnClickListener {
            val millName = mill_name_edittext.text.toString()
            val price = price_edittext.text.toString()
            val millAddress = milladdress_edittext.text.toString()
            val millContact = millcontact_edittext.text.toString()

            if (title.isNotEmpty()) {
                if (id!!.isNotEmpty()) {
                    updateTender(id!!, millName, price, millAddress, millContact)
                } else {
                    addTender(millName, price, millAddress, millContact)
                }
            }


            finish()

        }
    }

    private fun updateTender(id: String, millName: String, price: String, millAddress: String, millContact: String) {
        val tender = Tender(id, millName, price, millAddress, millContact)

        firestoreDB!!.collection("tender")
            .document(id)
            .set(tender)
            .addOnSuccessListener {
                Log.e(TAG, "Tender document update successful!")
                Toast.makeText(applicationContext, "Tender has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Tender document", e)
                Toast.makeText(applicationContext, "Tender could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addTender(millName: String, price: String, millAddress: String, millContact: String) {
        val tender = Tender(millName, price, millAddress, millContact)

        firestoreDB!!.collection("tender")
            .add(tender)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Tender has been added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Tender could not be added!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun prepareCroperino() {
        //prepare camera, gallery and ask for storage permissions.
        CroperinoConfig(
            "IMG_" + System.currentTimeMillis() + ".jpg",
            "/SugarMerchant/Pictures",
            "/sdcard/SugarMerchant/Pictures"
        )
        CroperinoFileUtil.verifyStoragePermissions(this@AddTenderActivity)
        CroperinoFileUtil.setupDirectory(this@AddTenderActivity)

    }

    private fun addSugarButtonClicked() {
        Log.d("Camera in use", "Camera in use")
        Croperino.prepareChooser(
            this@AddTenderActivity,
            "" + resources.getString(R.string.capture_photo),
            ContextCompat.getColor(this@AddTenderActivity, android.R.color.background_dark)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CroperinoConfig.REQUEST_TAKE_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    /* Parameters of runCropImage = File, Activity Context, Image is Scalable or Not, Aspect Ratio X, Aspect Ratio Y, Button Bar Color, Background Color */
                    Croperino.runCropImage(
                        CroperinoFileUtil.getTempFile(),
                        this@AddTenderActivity,
                        true,
                        1,
                        1,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_PICK_FILE ->
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, this@AddTenderActivity)
                    Croperino.runCropImage(
                        CroperinoFileUtil.getTempFile(),
                        this@AddTenderActivity,
                        true,
                        0,
                        0,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_CROP_PHOTO ->
                if (resultCode == Activity.RESULT_OK) {
                    //cropped image returned is set to the imageview on  register layout
                    var i = Uri.fromFile(CroperinoFileUtil.getTempFile())
                    Glide.with(this@AddTenderActivity).load(i)
                        .placeholder(R.drawable.photoplaceholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(
                            sugar_image
                        )
                }
        }
    }

}
