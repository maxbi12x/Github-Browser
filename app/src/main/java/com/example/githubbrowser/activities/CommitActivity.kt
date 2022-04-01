package com.example.githubbrowser.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubbrowser.callingservice.CommitService
import com.example.githubbrowser.databinding.ActivityCommitBinding
import com.example.githubbrowser.responsemodels.*
import com.example.githubbrowser.utils.CommitAdapter
import com.example.githubbrowser.utils.Constants
import com.example.githubbrowser.utils.InternetCheck
import retrofit.*

class CommitActivity : AppCompatActivity() {
    private lateinit var branchName: String
    private lateinit var ownerName: String
    private lateinit var repoName: String
    private var commitAdapter: CommitAdapter? = null
    private var binding: ActivityCommitBinding? = null
    private var commitList: ArrayList<CommitModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommitBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        ownerName = intent.getStringExtra("owner")!!
        repoName = intent.getStringExtra("repo")!!
        branchName = intent.getStringExtra("branch")!!
        binding?.commitBack?.setOnClickListener {
            finish()
        }
        binding?.commitbranchname?.text = branchName
        getCommits(object : ApiCallback {
            override fun onSuccess(result: ArrayList<CommitModel>?) {
                Log.e("RESPONSE", result!!.toString())
                commitList = result
                setCommitRV()
            }
        })


    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }


    private fun getCommits(callbk: ApiCallback) {
        if (InternetCheck.isConnected(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: CommitService =
                retrofit.create(CommitService::class.java)
            val listCall: Call<ArrayList<CommitModel>> =
                service.getService(ownerName, repoName, branchName)
            listCall.enqueue(object : Callback<ArrayList<CommitModel>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    response: Response<ArrayList<CommitModel>>,
                    retrofit: Retrofit
                ) {
                    if (response.isSuccess) {

                        callbk.onSuccess(response.body())
                        Log.v("Paper", response.body().size.toString())
                    } else {
                        Toast.makeText(
                            this@CommitActivity,
                            "Unable to fetch data",
                            Toast.LENGTH_SHORT
                        ).show()

                        val sc = response.code()
                        when (sc) {
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
                        return
                    }
                }

                override fun onFailure(t: Throwable) {
                    Toast.makeText(this@CommitActivity, "Unable to fetch data", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("Error", t.message.toString())
                }
            })
        } else {
            Toast.makeText(
                this,
                "No internet connection available.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setCommitRV() {
        binding?.commitRv?.layoutManager = LinearLayoutManager(this)
        binding?.commitRv?.setHasFixedSize(true)
        commitAdapter = CommitAdapter(this, commitList)
        binding?.commitRv?.adapter = commitAdapter

    }

    interface ApiCallback {
        fun onSuccess(result: ArrayList<CommitModel>?)
    }
}