package com.example.sugarbroker.activity.resale

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sugarbroker.R
import com.example.sugarbroker.model.Resale
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.mikelau.croperino.Croperino
import com.mikelau.croperino.CroperinoConfig
import com.mikelau.croperino.CroperinoFileUtil
import kotlinx.android.synthetic.main.activity_add_resale.*
import java.io.ByteArrayOutputStream
import java.util.*

class AddResaleActivity : AppCompatActivity() {

    private val TAG = "AddResaleActivity"

    private var firestoreDB: FirebaseFirestore? = null
    internal var id: String? = ""

    lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resale)

        firestoreDB = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val bundle = intent.extras
        if (bundle != null) {
            id = bundle.getString("UpdateResaleId")
            Toast.makeText(applicationContext, "ID ${id}", Toast.LENGTH_SHORT).show()

            mill_name_edittext.setText(bundle.getString("UpdateResaleMillName"))
            price_edittext.setText(bundle.getString("UpdateResalePrice"))
            milladdress_edittext.setText(bundle.getString("UpdateResaleAddress"))
            millcontact_edittext.setText(bundle.getString("UpdateResaleContact"))
            Glide.with(this@AddResaleActivity).load(bundle.getString("UpdateResaleUrl"))
                .placeholder(R.drawable.photoplaceholder)
                .apply(RequestOptions.circleCropTransform())
                .into(
                    sugar_image
                )
        }

        prepareCroperino()
        if (title.isNotEmpty()) {
            if (id!!.isNotEmpty()) {
                add_button.text = "Update"
                pageTitle.text = "Update Resale Details"
            } else {
                add_button.text = "Add"
                pageTitle.text = "Add Resale Details"
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

            val storageRef = storage.reference
            var x = UUID.randomUUID()
            val mountainsRef = storageRef.child("" + (x) + ".jpg")
            val mountainImagesRef = storageRef.child("images/" + x + ".jpg")
            mountainsRef.name == mountainImagesRef.name // true
            mountainsRef.path == mountainImagesRef.path // false
            val bitmap = (sugar_image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var uploadTask = mountainsRef.putBytes(data)
            uploadTask = storageRef.child("images/" + x + ".jpg").putBytes(data)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation mountainImagesRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    if (title.isNotEmpty()) {
                        if (id!!.isNotEmpty()) {
                            Log.d("downloadUri", "downloadUri is ${downloadUri}")
                            updateResale(id!!, millName, price, millAddress, millContact, downloadUri.toString())
                        } else {
                            addResale(millName, price, millAddress, millContact, downloadUri.toString())
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Upload unsuccesfull", Toast.LENGTH_SHORT).show()
                }
            }

            finish()

        }
    }

    private fun updateResale(id: String, millName: String, price: String, millAddress: String, millContact: String, url: String? = null) {
        val resale = Resale(id, millName, price, millAddress, millContact, url)

        Log.d("downloadUri", "downloadUri is url ${url}")


        firestoreDB!!.collection("resale")
            .document(id)
            .set(resale)
            .addOnSuccessListener {
                Log.e(TAG, "Resale document update successful!")
                Toast.makeText(applicationContext, "Resale has been updated!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Resale document", e)
                Toast.makeText(applicationContext, "Resale could not be updated!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addResale(millName: String, price: String, millAddress: String, millContact: String, url: String? = null) {
        val resale = Resale(millName, price, millAddress, millContact, url)

        firestoreDB!!.collection("resale")
            .add(resale)
            .addOnSuccessListener { documentReference ->
                Log.e(TAG, "DocumentSnapshot written with ID: " + documentReference.id)
                Toast.makeText(applicationContext, "Resale has been added!", Toast.LENGTH_SHORT).show()
                firestoreDB!!.collection("resale").document(documentReference.id).update("id", documentReference.id)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating ID", e) }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error adding Note document", e)
                Toast.makeText(applicationContext, "Resale could not be added!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun prepareCroperino() {
        //prepare camera, gallery and ask for storage permissions.
        CroperinoConfig(
            "IMG_" + System.currentTimeMillis() + ".jpg",
            "/SugarMerchant/Pictures",
            "/sdcard/SugarMerchant/Pictures"
        )
        CroperinoFileUtil.verifyStoragePermissions(this@AddResaleActivity)
        CroperinoFileUtil.setupDirectory(this@AddResaleActivity)

    }

    private fun addSugarButtonClicked() {
        Log.d("Camera in use", "Camera in use")
        Croperino.prepareChooser(
            this@AddResaleActivity,
            "" + resources.getString(R.string.capture_photo),
            ContextCompat.getColor(this@AddResaleActivity, android.R.color.background_dark)
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
                        this@AddResaleActivity,
                        true,
                        1,
                        1,
                        R.color.gray,
                        R.color.gray_variant
                    )
                }
            CroperinoConfig.REQUEST_PICK_FILE ->
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, this@AddResaleActivity)
                    Croperino.runCropImage(
                        CroperinoFileUtil.getTempFile(),
                        this@AddResaleActivity,
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
                    Glide.with(this@AddResaleActivity).load(i)
                        .placeholder(R.drawable.photoplaceholder)
                        .apply(RequestOptions.circleCropTransform())
                        .into(
                            sugar_image
                        )
                }
        }
    }

}
