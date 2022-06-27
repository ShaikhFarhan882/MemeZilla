package com.example.memezilla

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.memezilla.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.sql.DataSource

class MainActivity : AppCompatActivity() {

    var urlMeme: String = ""

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (!verifyAvailableNetwork(this)) {
            Toast.makeText(this, "No Network Available", Toast.LENGTH_LONG).show()
        }

        GlobalScope.launch(Dispatchers.Main) {
            fetchMemes()
        }


        binding.NextButton.setOnClickListener(View.OnClickListener {

            GlobalScope.launch(Dispatchers.Main) {
                fetchMemes()
            }
        })

        binding.ShareButton.setOnClickListener(View.OnClickListener { view ->
            GlobalScope.launch {
                withContext(Dispatchers.IO){
                    shareMeme()
                }
            }

        })

    }

    suspend fun shareMeme() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, urlMeme)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, "Share To:"))
    }

    suspend private fun fetchMemes() {
        binding.progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.herokuapp.com/gimme"

        //string Request
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            val url = response.getString("url")
            val title = response.getString("title")

            loadImage(url)

            binding.MemeTitle.setText(title)
            urlMeme = url

        }, {
            Toast.makeText(this, "Failed to Load!", Toast.LENGTH_SHORT).show()

        })

        MySingleton.getInstance(applicationContext).requestQueue.add(request)

    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.progressBar.visibility = View.GONE

                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    return false
                }

            }).into(binding.MemeImage)

    }

    fun verifyAvailableNetwork(activity: AppCompatActivity): Boolean {
        val connectivityManager =
            activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }


}