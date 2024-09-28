package com.scriptsquad.unitalk.Marketplace.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.Marketplace.adapter.AdapterImagePicked
import com.scriptsquad.unitalk.databinding.ActivityAdCreateBinding
import com.scriptsquad.unitalk.Marketplace.model.ModelImagePicked
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream


class Create_Ad_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityAdCreateBinding

    private companion object {
        private const val TAG = "ADD_CREATE_TAG"
    }

    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth

    // image Uri to hold uri of the image(picked/captured using Gallery/camera) to add in Ad Images
    private var imageUri: Uri? = null

    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>

    private lateinit var adapterImagePicked: AdapterImagePicked

    private var isEditMode = false
    private var adIdForEditing = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdCreateBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        // Setup and set the categories adapter to the Category Input Field i.e. categoryAct
        val adapterCategories =
            ArrayAdapter<String>(this@Create_Ad_Activity, R.layout.row_category_act, Utils.categories)
        binding.categoryAct.setAdapter(adapterCategories)

        // Setup and set the categories adapter to the Condition Input Field i.e. ConditionAct
        val adapterCondition = ArrayAdapter<String>(
            this@Create_Ad_Activity,
            R.layout.row_condtition_act,
            Utils.condition
        )
        binding.conditionAct.setAdapter(adapterCondition)

        isEditMode = intent.getBooleanExtra("isEditMode", false)
        Log.d(TAG, "onCreate: isEditable: $isEditMode")

        if (isEditMode) {

            adIdForEditing = intent.getStringExtra("adId") ?: ""

            loadAdDetails()

            binding.toolbarTitleTv.text = getString(R.string.update_ad)
            binding.postAd.text = getString(R.string.update_ad)
        } else {
            binding.toolbarTitleTv.text = getString(R.string.create_ad)
            binding.postAd.text = getString(R.string.post_ad)
        }

        //init imagePickedArrayList
        imagePickedArrayList = ArrayList()
        loadImages()

        binding.toolbarBackBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.toolbarAdImageBtn.setOnClickListener {
            showImagePickOptions()
        }

        //handle location click

        binding.locationAct.setOnClickListener {
            val intent = Intent(this@Create_Ad_Activity, Location_Picker_Activity::class.java)
            locationPickerActivityResultLauncher.launch(intent)
        }

        //handle postAdBtn click, validate data and publish Ad
        binding.postAd.setOnClickListener {
            validateData()
        }

    }

    private val locationPickerActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            Log.d(TAG, "locationPickerActivityResultLauncher: ")

            if (result.resultCode == Activity.RESULT_OK){

                val data = result.data

                if (data!=null){
                    latitude = data.getDoubleExtra("latitude", 0.0)
                    longitude = data.getDoubleExtra("longitude", 0.0)
                    address = data.getStringExtra("address") ?: ""

                    Log.d(TAG, "locationPickerActivityResultLauncher: latitude: $latitude ")
                    Log.d(TAG, "locationPickerActivityResultLauncher: longitude: $longitude ")
                    Log.d(TAG, "locationPickerActivityResultLauncher: address: $address ")

                    binding.locationAct.setText(address)

                }


            }

            else{

                Log.d(TAG, "locationPickerActivityResultLauncher: cancelled")
                Utils.toast(this@Create_Ad_Activity,"Cancelled...")

            }

        }

    private fun loadImages() {
        Log.d(TAG, "loadImages: ")
        // setAdapter to RecyclerView
        adapterImagePicked =
            AdapterImagePicked(this@Create_Ad_Activity, imagePickedArrayList, adIdForEditing)
        binding.imagesRv.adapter = adapterImagePicked
    }

    private fun showImagePickOptions() {
        Log.d(TAG, "showImagePickOptions: ")

        //s how popUp on Button
        val popupMenu = PopupMenu(this@Create_Ad_Activity, binding.toolbarAdImageBtn)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
// get the id of the item clicked in popup menu
            val itemId = item.itemId
            // check which item is clicked from popUp menu, 1=Camera. 2=Gallery as we defined
            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    val cameraPermission = arrayOf(android.Manifest.permission.CAMERA)
                    requestCameraPermission.launch(cameraPermission)
                } else {
                    // Device version is TIRAMISU,We need Storage permission to launch Camera
                    val cameraPermissions = arrayOf(
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestCameraPermission.launch(cameraPermissions)
                }
            } else if (itemId == 2) {
                // Gallery is clicked we need to check if we have permission of Storage before launching Gallery to Pick image
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pickImageGallery()
                } else {
                    // Device version is TIRAMISU,We need Storage permission to launch Gallery
                    val storagePermission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    requestStoragePermission.launch(storagePermission)
                }
            }

            true
        }
    }

    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "requestStoragePermission: isGranted: $isGranted")

        if (isGranted) {
            // Let's check if permission is granted or not
            pickImageGallery()
        } else {
            //Storage Permission denied, we can't launch Socials_Page to pick images
            Utils.toast(this@Create_Ad_Activity, "Storage Permission denied.... ")
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        Log.d(TAG, "requestCameraPermission: result: $result")
        //let's check if permissions are granted or not
        var areAllGranted = true
        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }
        if (areAllGranted) {
            //All Permission Camera,Storage are granted,we can now launch camera to capture image
            pickImageCamera()
        } else {
            //Camera or Storage or Both permission are denied, Can't launch camera to capture image
            Utils.toast(this@Create_Ad_Activity, "Camera or Storage or both permission denied...")
        }

    }

    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery:")
        val intent = Intent(Intent.ACTION_PICK)
        // we only want to pick Images
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera:")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_IMAGE_DESCRIPTION")
        // Uri of image to be captured from camera
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        //Intent to launch Camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "galleryActivityResultLauncher :")
        // Check if image is picked or not
        if (result.resultCode == Activity.RESULT_OK) {

            //get data from result param
            val data = result.data
            //get uri of image picked
            imageUri = data!!.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imageUri")

            //timestamp will be used as id of image picked
            val timestamp = "${Utils.getTimestamp()}"

            //setup model for image,Param 1 is id,Param 2 is imageUri, Param 3 is imageUrl , from internet
            val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
            imagePickedArrayList.add(modelImagePicked)
            //reload image
            loadImages()
        } else {
            // Cancelled
            Utils.toast(this@Create_Ad_Activity, "Cancelled...!")
        }
// Check if image is picked or not
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "cameraActivityResultLauncher:")
        //Check if image is picked or not
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "cameraActivityResultLauncher: imageUri $imageUri")

            val timestamp = "${Utils.getTimestamp()}"

            val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
            imagePickedArrayList.add(modelImagePicked)

            loadImages()
        } else {
            Utils.toast(this, "Cancelled...!")
        }
    }

    private var brand = ""
    private var category = ""
    private var condition = ""
    private var address = ""
    private var price = ""
    private var title = ""
    private var description = ""
    private var latitude = 0.0
    private var longitude = 0.0


    private fun validateData() {
        Log.d(TAG, "validateData: ")

        brand = binding.brandEt.text.toString().trim()
        category = binding.categoryAct.text.toString().trim()
        condition = binding.conditionAct.text.toString().trim()
        address = binding.locationAct.text.toString().trim()
        price = binding.priceEt.text.toString().trim()
        title = binding.titileEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        if (brand.isEmpty()) {
            // no brand entered in brandEt show error in brandEt and Focus
            binding.brandEt.error = "Enter Brand"
            binding.brandEt.requestFocus()
        } else if (category.isEmpty()) {
            // no category  entered in brandEt show error in brandEt and Focus
            binding.categoryAct.error = "Select Category"
            binding.categoryAct.requestFocus()
        } else if (condition.isEmpty()) {
            // no condition entered in brandEt show error in brandEt and Focus
            binding.conditionAct.error = "Select Condition"
            binding.conditionAct.requestFocus()
        } else if (title.isEmpty()) {
            // no address entered in brandEt show error in brandEt and Focus
            binding.titileEt.error = "Enter Title"
            binding.titileEt.requestFocus()
        } else if (description.isEmpty()) {
            binding.descriptionEt.error = "Enter Description"
            binding.descriptionEt.requestFocus()
        } else {
            // All data is validated, we can proceed further now
            if (isEditMode) {
                updateAd()
            } else {
                postAd()
            }

        }

    }

    private fun postAd() {
        Log.d(TAG, "postAd: ")

        progressDialog.setMessage("Posting Ad")
        progressDialog.show()

        // get currentTimeStamp
        val timestamp = Utils.getTimestamp()
        // firebase database Ads reference to store new ads
        val refAds = FirebaseDatabase.getInstance().getReference("Ads")
        // key id from the reference to use as Ad id
        val keyId = refAds.push().key

        //setup data to add in firebase database
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$keyId"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["brand"] = brand
        hashMap["category"] = category
        hashMap["condition"] = condition
        hashMap["address"] = address
        hashMap["price"] = price
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["status"] = Utils.AD_STATUS_AVAILABLE
        hashMap["timestamp"] = timestamp
        hashMap["latitude"] = latitude
        hashMap["longitude"] = longitude


        // set data to firebase database. Ads -> AdId ->AdDataJSON
        refAds.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "postAd: Ad Published")
                uploadImageStorage(keyId)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "postAd:", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed due to ${e.message}")
            }
    }

    private fun updateAd() {
        Log.d(TAG, "updateAd:")
        progressDialog.setMessage("Updating Ad....")
        progressDialog.show()


        val hashMap = HashMap<String, Any>()
        hashMap["brand"] = brand
        hashMap["category"] = category
        hashMap["condition"] = condition
        hashMap["address"] = address
        hashMap["price"] = price
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["latitude"] = latitude
        hashMap["longitude"] = longitude

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adIdForEditing)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "updateAd: Ad Updated...")
                progressDialog.setMessage("Updated Successfully")
                progressDialog.dismiss()

                uploadImageStorage(adIdForEditing)
            }.addOnFailureListener { e ->
                Log.e(TAG, "updateAd: ", e)
                progressDialog.dismiss()
                Utils.toast(this@Create_Ad_Activity, "Failed to update due to ${e.message}")
            }
    }

    private fun uploadImageStorage(adId: String) {
        //there are multiple images in imagePickedArrayList, loop to upload all
        for (i in imagePickedArrayList.indices) {
            // get model from the current position of imagePickedArrayList
            val modelImagePicked = imagePickedArrayList[i]

            // Upload Images only picked from Gallery and Camera
            if (!modelImagePicked.fromInternet) {
                val imageName = modelImagePicked.id

                val filePathAndName = "Ads/$imageName"

                val imageIndexForProgress = i + 1

                //Storage reference with filePathAndName
                val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)

                //compress images
                val bitmap =
                    MediaStore.Images.Media.getBitmap(contentResolver, modelImagePicked.imageUri)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
                val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()

                //modelImagePicked.imageUri!!

                storageReference.putBytes(reducedImage)
                    .addOnProgressListener { snapshot ->
                        //calculate the current progress of the image being uploaded
                        val progress = 100.0 * snapshot.bytesTransferred / snapshot.totalByteCount
                        Log.d(TAG, "uploadImageStorage: progress: $progress")
                        val message =
                            "Uploading $imageIndexForProgress of ${imagePickedArrayList.size} images... Progress ${progress.toInt()}"
                        Log.d(TAG, "uploadImageStorage: message: $message")


                        progressDialog.setMessage(message)
                        progressDialog.show()

                    }
                    .addOnSuccessListener { taskSnapshot ->
                        //image uploaded get url of uploaded image
                        Log.d(TAG, "uploadImageStorage: onSuccess")

                        val uriTask = taskSnapshot.storage.downloadUrl
                        while (!uriTask.isSuccessful);
                        val uploadImageUrl = uriTask.result

                        if (uriTask.isSuccessful) {
                            val hashMap = HashMap<String, Any>()
                            hashMap["id"] = modelImagePicked.id
                            hashMap["imageUrl"] = "$uploadImageUrl"

                            val ref = FirebaseDatabase.getInstance().getReference("Ads")
                            ref.child(adId).child("Images")
                                .child(imageName)
                                .updateChildren(hashMap)
                        }
                        progressDialog.dismiss()
                        MotionToast.createColorToast(
                            this, "Successfully", "Ad Uploaded",
                            MotionToastStyle.SUCCESS,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(
                                this,
                                www.sanju.motiontoast.R.font.helvetica_regular
                            )
                        )

                    }
                    .addOnFailureListener { e ->
                        // failed to upload image
                        Log.e(TAG, "uploadImageStorage", e)
                        progressDialog.dismiss()
                    }
            }


            // for name of the images in firebase Storage


        }


    }

    private fun loadAdDetails() {
        Log.d(TAG, "loadAdDetails: ")

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adIdForEditing)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

}



