package com.example.aarongram.fragments

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import com.example.aarongram.Post
import com.example.aarongram.R
import com.example.aarongram.activities.MainActivity
import com.parse.ParseFile
import com.parse.ParseUser
import java.io.File

class ComposeFragment : Fragment() {
    private val photoFileName = "photo.jpg"
    private var photoFile: File? = null
    private lateinit var ivPreview: ImageView
    private lateinit var pb: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById(R.id.imageView)
        pb = view.findViewById<View>(R.id.pbLoading) as ProgressBar

        // Set onClickListeners and set up logic
        view.findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // Send post to server
            val description = view.findViewById<EditText>(R.id.description).text.toString()
            val user = ParseUser.getCurrentUser()
            hideKeyboard(view.findViewById(R.id.composeView))
            if (photoFile != null) {
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(MainActivity.TAG, "photoFile is null")
                Toast.makeText(requireContext(), "Please take a picture", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onLaunchCamera() {
        // Create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }

    private fun submitPost(description: String, user: ParseUser, file: File) {
        if (description == "") {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }

        pb.visibility = ProgressBar.VISIBLE

        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        post.saveInBackground { e ->
            if (e != null) {
                Log.e(MainActivity.TAG, "Error while saving post")
                e.printStackTrace()
                Toast.makeText(requireContext(), "Post unsuccessful", Toast.LENGTH_SHORT).show()
            } else {
                Log.i(MainActivity.TAG, "Successfully saved post")
                requireView().findViewById<EditText>(R.id.description).text = null
                requireView().findViewById<ImageView>(R.id.imageView).setImageResource(android.R.color.transparent)
                Toast.makeText(requireContext(), "Post successful", Toast.LENGTH_SHORT).show()
                pb.visibility = ProgressBar.INVISIBLE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // By this point we have the camera photo on disk
                val rawTakenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
//                val resizedBitmap: Bitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 200)
//                val bytes = ByteArrayOutputStream()
//                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes)
//                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
//                val resizedFile = getPhotoFileUri(photoFileName + "_resized")
//                resizedFile.createNewFile()
//                val fos = FileOutputStream(resizedFile)
//                // Write the bytes of the bitmap to file
//                fos.write(bytes.toByteArray())
//                fos.close()
//                photoFile = resizedFile
                // Load the taken image into a preview
                ivPreview.setImageBitmap(rawTakenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private fun hideKeyboard(v: View) {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    companion object {
        const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    }
}