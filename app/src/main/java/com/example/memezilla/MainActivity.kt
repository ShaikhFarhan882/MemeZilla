package com.example.memezilla

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
import javax.sql.DataSource

class MainActivity : AppCompatActivity() {

     val MemeTitle:TextView
        get() = findViewById(R.id.Meme_title)

     val MemeImage: ImageView
      get() = findViewById(R.id.MemeImage)

     val shareButton : Button
      get() = findViewById(R.id.ShareButton)

     val nextButton : Button
        get() = findViewById(R.id.NextButton)

    var urlMeme : String = ""

    val progressBar : ProgressBar
    get() = findViewById(R.id.progressBar)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar?
        actionBar = supportActionBar
        val colorDrawable = ColorDrawable(Color.parseColor("#3700B3"))
        actionBar!!.setBackgroundDrawable(colorDrawable)
        actionBar!!.title = "MemeZilla"




        fetchMemes()

        nextButton.setOnClickListener(View.OnClickListener { view ->
            fetchMemes()
        })

        shareButton.setOnClickListener(View.OnClickListener { view ->
            val intent= Intent()
            intent.action=Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT,urlMeme)
            intent.type="text/plain"
            startActivity(Intent.createChooser(intent,"Share To:"))

        })

    }

    private fun fetchMemes(){
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "https://meme-api.herokuapp.com/gimme"

        //string Request
        val request = JsonObjectRequest(Request.Method.GET,url,null, { response ->
            val url = response.getString("url")
            val title = response.getString("title")

            Glide.with(this)
                .load(url)
                .listener(object : com.bumptech.glide.request.RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE

                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }

                }).into(MemeImage)

            MemeTitle.setText(title)
            urlMeme = url

        }, {

        })

        queue.add(request)

    }


}