package com.scriptsquad.unitalk.Materials.activities

import android.app.Activity
import android.app.AlertDialog
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
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.scriptsquad.unitalk.R
import com.scriptsquad.unitalk.Utilities.Utils
import com.scriptsquad.unitalk.databinding.ActivityAddPapersBinding
import com.scriptsquad.unitalk.Materials.model.ModelPaperCategory
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.ByteArrayOutputStream
import java.util.ArrayList
// Class for Add Papers Activity
class AddPapersActivity : AppCompatActivity() {

    // Private lateinit variable for binding
    private lateinit var binding: ActivityAddPapersBinding

    // Private lateinit variable for Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    // Private lateinit variable for Progress Dialog
    private lateinit var progressDialog: ProgressDialog

    // Uri of picked image
    private var imageUri: Uri? = null

    // Uri of picked PDF
    private var pdfUri: Uri? = null

    // Companion object for TAG
    private companion object {
        private const val TAG = "PAPER_ADD_TAG"
    }
    // Private lateinit variable for category array list
    private lateinit var categoryArrayList: ArrayList<ModelPaperCategory>
    // Override onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {

        // Inflate layout and set content view
        binding = ActivityAddPapersBinding.inflate(layoutInflater)

        // Call super onCreate method
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance()
        // Load categories
        loadCategories()

        // Initialize Progress Dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        // Set on click listener for back button
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set on click listener for category text view
        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }
        // Set on click listener for PDF upload button
        binding.pdfUploadBtn.setOnClickListener {
            pdfPickIntent()
        }
        // Set on click listener for submit button
        binding.submitBtn.setOnClickListener {

            validateData()
        }
        // Set on click listener for image pick button
        binding.ImagePickBtn.setOnClickListener {
            showImagePickOptions()
        }
        // Set on click listener for close button
        binding.closeBtn.setOnClickListener {
            try {

                Log.d(TAG, "onBindViewHolder: imageUri: $imageUri")
                // Clear image view
                Glide.with(this)
                    .clear(binding.imageIv)
            } catch (e: Exception) {
                Log.e(TAG, "onBindViewHolder:", e)
            }
        }


    }
    // Private variables to store title, category, and upload image URL
    private var title = ""
    private var category = ""
    private var uploadImageUrl = ""
    // Private function to validate data
    private fun validateData() {
        // Log debug message
        Log.d(TAG, "validateData: validating data")
        // Get title and category from edit text and text view
        title = binding.titleEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        // Validate data
        if (title.isEmpty()) {
            // Show error message if title is empty
            binding.titleEt.requestFocus()
            binding.titleEt.error = "Field Cannot be empty"
        } else if (category.isEmpty()) {
            // Show error message if category is empty
            binding.titleEt.requestFocus()
            binding.categoryTv.error = "Pick Category"
        } else if (pdfUri == null) {
            // Show warning toast if PDF is not picked
            MotionToast.createColorToast(
                this@AddPapersActivity,
                "Warning",
                "Pick PDF",
                MotionToastStyle.WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
            )
        } else if (imageUri == null) {
            // Show warning toast if image is not picked
            MotionToast.createColorToast(
                this@AddPapersActivity,
                "Warning",
                "Pick Image",
                MotionToastStyle.WARNING,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
            )
        } else {
            //data validate begin upload
            uploadBookImageToStorage()
            uploadPdfToStorage()
        }

    }
    // Private function to pick PDF intent
    private fun pdfPickIntent() {
        // Log debug message
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        // Create intent to pick PDF
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        // Launch intent
        pdfActivityResultLauncher.launch(intent)

    }
    // Private variable to store PDF activity result launcher
    private val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()


    ) { result ->
        // Check if PDF is picked
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "PDF Picked: ")
            // Get PDF Uri
            pdfUri = result.data!!.data
        } else {
            // Log debug message
            Log.d(TAG, "PDF picked Cancelled: ")
            // Show error toast
            MotionToast.createColorToast(
                this@AddPapersActivity,
                "Failed",
                "Failed to Get Pdf",
                MotionToastStyle.ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
            )
        }

    }
    // Private function to upload PDF to storage
    private fun uploadPdfToStorage() {
        // Log debug message
        Log.d(TAG, "uploadPdfToStorage: uploading to storage....")
        // Show progress dialog
        progressDialog.setMessage("Uploading PDF...")
        progressDialog.show()

        // Get timestamp
        val timestamp = Utils.getTimestamp()

        // Create path for PDF in Firebase storage
        val filePathAndName = "Papers/$timestamp"

        // Get storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        // Upload PDF to storage
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // Log debug message
                Log.d(TAG, "uploadPdfToStorage: pdf uploaded now getting url ...")

                // Get url of uploaded PDF
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                // Upload PDF info to database
                val uploadPdfUrl = "${uriTask.result}"
                uploadPdfInfoToDb(uploadPdfUrl, timestamp)

            }
            .addOnFailureListener { e ->
                // Log debug message
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${e.message}")
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Show error toast
                MotionToast.createColorToast(
                    this@AddPapersActivity,
                    "Pick Again",
                    "Failed to Get Pdf due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
                )

            }
            .addOnProgressListener {
                // Calculate progress
                val dataTransferred = (100 * it.bytesTransferred / it.totalByteCount)
                // Update progress dialog
                progressDialog.setMessage("Uploading $dataTransferred%")
            }

    }
    // Private function to upload book image to storage
    private fun uploadBookImageToStorage() {
        // Log debug message
        Log.d(TAG, "uploadPdfToStorage: uploading to storage....")

        // Show progress dialog
        progressDialog.setMessage("Uploading Image...")
        progressDialog.show()

        // Get timestamp
        val timestamp = Utils.getTimestamp()

        // Create path for image in Firebase storage
        val filePathAndName = "PapersImage/$timestamp"
        // Get storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        // Compressing image
        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
        val reducedImage: ByteArray = byteArrayOutputStream.toByteArray()

        // Upload image to storage
        storageReference.putBytes(reducedImage)
            .addOnSuccessListener { taskSnapshot ->

                Log.d(TAG, "uploadPdfToStorage: Image uploaded now getting url ...")

                // Get url of uploaded Image
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                // Get upload image URL
                uploadImageUrl = "${uriTask.result}"
            }
            .addOnFailureListener { e ->
                // Log debug message
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${e.message}")
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Show error toast
                MotionToast.createColorToast(
                    this@AddPapersActivity,
                    "Pick Again",
                    "Failed to Get Image due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
                )


                Log.i("xxx", "Failed uploading image to server")

            }
            .addOnProgressListener {
                // Calculate progress
                val dataTransferred = (100 * it.bytesTransferred / it.totalByteCount)
                // Update progress dialog
                progressDialog.setMessage("Uploading $dataTransferred%")
            }

    }
    // Private function to upload PDF info to database
    private fun uploadPdfInfoToDb(uploadPdfUrl: String, timestamp: Long) {
        // Log debug message
        Log.d(TAG, "uploadPdfInfoToDb: uploading to db")
        // Show progress dialog
        progressDialog.setMessage("Uploading pdf info....")

        // Create hash map to store PDF info
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        // Put PDF info into hash map
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["categoryId"] = "$selectedCategoryId"
        hashMap["url"] = "$uploadPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["imageUrl"] = "$uploadImageUrl"

        // Get reference to Firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Papers")
        // Upload PDF info to database
        ref.child("$timestamp")
            .setValue(hashMap)
            // Log debug message
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb: uploaded to db")
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Show success toast
                MotionToast.createColorToast(
                    this@AddPapersActivity,
                    "Success",
                    "Successfully Uploaded",
                    MotionToastStyle.SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
                )
                // Reset PDF Uri
                pdfUri = null

            }
            .addOnFailureListener { e ->
                // Log debug message
                Log.d(TAG, "uploadPdfToStorage: failed to upload due to ${e.message}")
                // Dismiss progress dialog
                progressDialog.dismiss()
                // Show error toast
                MotionToast.createColorToast(
                    this@AddPapersActivity,
                    "Pick Again",
                    "Failed to Get Pdf due to ${e.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helveticabold)
                )
            }

    }
    // Private function to load categories
    private fun loadCategories() {
        // Log debug message
        Log.d(TAG, "loadCategories: Loading pdf categories")

        // Initialize category array list
        categoryArrayList = ArrayList()

        // Get reference to Firebase database
        val ref = FirebaseDatabase.getInstance().getReference("CategoriesPapers")

        // Add value event listener to database reference
        ref.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear category array list
                categoryArrayList.clear()
                // Iterate through data snapshot
                for (ds in snapshot.children) {
                    // Get model from data snapshot
                    val model = ds.getValue(ModelPaperCategory::class.java)

                    // Add model to category array list
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
    // Private variables to store selected category ID and title
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    // Private function to show category pick dialog

    private fun categoryPickDialog() {
        // Log debug message
        Log.d(TAG, "categoryPickDialog: showing pdf category pick dialog")
        // Get string array of categories from category array list
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        // Iterate through category array list
        for (i in categoryArrayList.indices) {
            categoriesArray[i] = categoryArrayList[i].category
        }
        // Create alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray) { _, which ->
                // Get selected category title and ID
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id
                // Set category to text view
                binding.categoryTv.text = selectedCategoryTitle
                // Log debug message
                Log.d(TAG, "categoryPickDialog: Selected category Id: $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected Category Title: $selectedCategoryTitle")

            }
            .show()

    }
    // Private function to load images
    private fun loadImages() {
        // Log debug message
        Log.d(TAG, "loadImages: ")
        try {
            // Log debug message
            Log.d(TAG, "onBindViewHolder: imageUri: $imageUri")
            // Load image into image view
            Glide.with(this)
                .load(imageUri)
                .placeholder(R.drawable.ic_image_gray)
                .into(binding.imageIv)
        } catch (e: Exception) {
            // Log error message
            Log.e(TAG, "onBindViewHolder:", e)
        }

    }
    // Private function to show image pick options
    private fun showImagePickOptions() {
        // Log debug message
        Log.d(TAG, "showImagePickOptions: ")

        // Create pop-up menu
        val popupMenu = PopupMenu(this, binding.ImagePickBtn)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()
        // Set on item click listener to pop-up menu
        popupMenu.setOnMenuItemClickListener { item ->
        // Get the id of the item clicked in popup menu
            val itemId = item.itemId
            // Check which item is clicked from popUp menu, 1=Camera. 2=Gallery as we defined
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
    // Private variable to store storage permission request
    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Log debug message
        Log.d(TAG, "requestStoragePermission: isGranted: $isGranted")
        // Check if permission is granted
        if (isGranted) {
            // Check if permission is granted or not
            pickImageGallery()
        } else {
            // Storage Permission denied, we can't launch Socials_Page to pick images
            Utils.toast(this, "Storage Permission denied.... ")
        }
    }
    // Private variable to store camera permission request
    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // Log debug message
        Log.d(TAG, "requestCameraPermission: result: $result")
        // Check if all permissions are granted
        var areAllGranted = true
        for (isGranted in result.values) {
            areAllGranted = areAllGranted && isGranted
        }
        // Check if all permissions are granted
        if (areAllGranted) {
            //All Permission Camera,Storage are granted,we can now launch camera to capture image
            pickImageCamera()
        } else {
            //Camera or Storage or Both permission are denied, Can't launch camera to capture image
            Utils.toast(this, "Camera or Storage or both permission denied...")
        }

    }
    // Private function to pick image from gallery
    private fun pickImageGallery() {
        // Log debug message
        Log.d(TAG, "pickImageGallery:")
        // Create intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        // Set type to image
        intent.type = "image/*"
        // Launch intent
        galleryActivityResultLauncher.launch(intent)
    }
    // Private function to pick image from camera
    private fun pickImageCamera() {
        // Log debug message
        Log.d(TAG, "pickImageCamera:")
        // Create content values
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "TEMP_IMAGE_TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "TEMP_IMAGE_DESCRIPTION")
        // Uri of image to be captured from camera
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        // Intent to launch Camera
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }
    // Private variable to store gallery activity result launcher
    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "galleryActivityResultLauncher :")
        // Check if image is picked or not
        if (result.resultCode == Activity.RESULT_OK) {

            // Get data from result param
            val data = result.data
            // Get uri of image picked
            imageUri = data!!.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imageUri")

            // Timestamp will be used as id of image picked

            // Setup model for image,Param 1 is id,Param 2 is imageUri, Param 3 is imageUrl , from internet
            loadImages()
        } else {
            // Cancelled
            Utils.toast(this, "Cancelled...!")
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

            // Load image loadImages()
            loadImages()
        } else {
            // Show toast message
            Utils.toast(this, "Cancelled...!")
        }
    }


}