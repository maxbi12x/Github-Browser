package com.example.githubbrowser.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.githubbrowser.callingservice.RepoService
import com.example.githubbrowser.database.DatabaseHandler
import com.example.githubbrowser.databinding.ActivityAddRepoBinding
import com.example.githubbrowser.responsemodels.RepoModel
import com.example.githubbrowser.utils.Constants
import com.example.githubbrowser.utils.InternetCheck
import retrofit.*

class AddRepoActivity : AppCompatActivity() {

    private var binding: ActivityAddRepoBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddRepoBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        binding?.addRepoBack?.setOnClickListener {
            finish()
        }


        binding?.addButton?.setOnClickListener {

            if (binding?.inputOwner?.text.toString()
                    .isEmpty() || binding?.inputRepo?.text.toString().isEmpty()
            )
                Toast.makeText(this, "These Fields cannot be empty", Toast.LENGTH_SHORT).show()
            else {

                var ret: String
                if (InternetCheck.isConnected(this@AddRepoActivity)) {


                    val retrofit: Retrofit = Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)

                        .addConverterFactory(GsonConverterFactory.create())

                        .build()


                    val service: RepoService =
                        retrofit.create(RepoService::class.java)


                    val listCall: Call<RepoModel> = service.getService(
                        binding?.inputOwner?.text.toString(),
                        binding?.inputRepo?.text.toString()
                    )
                    listCall.enqueue(object : Callback<RepoModel> {
                        @SuppressLint("SetTextI18n")

                        override fun onResponse(
                            response: Response<RepoModel>,
                            retrofit: Retrofit
                        ) {

                            if (response.isSuccess) {
                                val repoList = response.body()

                                val dbHandler = DatabaseHandler(this@AddRepoActivity)
                                ret = repoList.description
                                val result = dbHandler.addRepo(
                                    binding?.inputOwner?.text.toString(),
                                    binding?.inputRepo?.text.toString()
                                )
                                if (result > -2) {
                                    val inn = Intent()
                                    inn.putExtra("desc", ret)
                                    inn.putExtra("repo", binding?.inputRepo?.text.toString())
                                    inn.putExtra("owner", binding?.inputOwner?.text.toString())
                                    setResult(RESULT_OK, inn)
                                    Log.i("HyperX", "$repoList")
                                    finish()
                                }


                            } else {
                                Toast.makeText(
                                    this@AddRepoActivity,
                                    "Unable to fetch data",
                                    Toast.LENGTH_SHORT
                                ).show()
                                when (response.code()) {
                                    400 -> {
                                        Log.e("Error 400", "Bad Request")
                                    }
                                    404 -> {
                                        Log.e("Error 404", "Not Found")
                                    }
                                    else -> {
                                        Log.e("Error", "Generic Error")
                                    }
                                }
                                finish()
                            }
                        }

                        override fun onFailure(t: Throwable) {
                            Toast.makeText(
                                this@AddRepoActivity,
                                "Unable to fetch data",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("Error", t.message.toString())
                        }
                    })
                } else {
                    Toast.makeText(
                        this@AddRepoActivity,
                        "No internet connection available.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }

        //finish()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}