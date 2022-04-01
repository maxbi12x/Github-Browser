package com.example.githubbrowser.activities

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubbrowser.callingservice.BranchService
import com.example.githubbrowser.callingservice.IssueService
import com.example.githubbrowser.database.DatabaseHandler
import com.example.githubbrowser.databinding.ActivityDetailsBinding
import com.example.githubbrowser.responsemodels.Branches
import com.example.githubbrowser.responsemodels.IssueModel
import com.example.githubbrowser.utils.*
import retrofit.*


class DetailsActivity : AppCompatActivity() {

    private var binding: ActivityDetailsBinding? = null
    private lateinit var ownerName: String
    private lateinit var repoName: String
    var branchAdapter: BranchAdapter? = null
    var issueAdapter: IssueAdapter? = null
    private var branchList: ArrayList<Branches> = ArrayList()
    private var issuesList: ArrayList<IssueModel> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        ownerName = intent.getStringExtra("owner")!!
        repoName = intent.getStringExtra("repo")!!
        val desc = intent.getStringExtra("desc")!!


        binding?.tvTitledesc?.text = desc
        binding?.tvNameofrepo?.text = repoName


        binding?.detailsBack?.setOnClickListener {
            finish()
        }

        binding

        getBranches(object : ApiCallback {
            override fun onSuccess(result: ArrayList<Branches>?) {

                branchList = result!!
                setRecyclerViewOnStart()
            }
        }, ownerName, repoName)


        getIssues(object : ApiCallbackforIsu {
            @SuppressLint("SetTextI18n")
            override fun onSuccess(result: ArrayList<IssueModel>?) {
                if (result != null) {
                    binding?.IssuesButton?.text = "ISSUES(${result.size})"
                    issuesList = result
                    setRecyclerViewOnStartforIsu()
                }

            }
        }, ownerName, repoName)


        binding?.IssuesButton?.setOnClickListener {
            binding?.rvIssues?.visibility = View.VISIBLE
            binding?.rvBranch?.visibility = View.GONE
        }

        binding?.BranchesButton?.setOnClickListener {
            binding?.rvIssues?.visibility = View.GONE
            binding?.rvBranch?.visibility = View.VISIBLE
        }

        binding?.detailsPreview?.setOnClickListener {
            try {
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/${ownerName}/${repoName}")
                )
                startActivity(browserIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    this, "No application can handle this request."
                            + " Please install a web browser", Toast.LENGTH_LONG
                ).show()
            }
        }

        binding?.detailsDelete?.setOnClickListener {

            val dbHandler = DatabaseHandler(this)
            dbHandler.deleteRepo(ownerName, repoName, this)
            val inn = Intent()
            val position = intent.getStringExtra("pos")!!
            inn.putExtra("pos", position)
            setResult(RESULT_OK, inn)
            finish()
        }

    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    interface ApiCallback {
        fun onSuccess(result: ArrayList<Branches>?)
    }

    interface ApiCallbackforIsu {
        fun onSuccess(result: ArrayList<IssueModel>?)
    }


    fun setRecyclerViewOnStart() {
        binding?.rvBranch?.layoutManager = LinearLayoutManager(this)
        binding?.rvBranch?.setHasFixedSize(true)
        branchAdapter = BranchAdapter(this, branchList)
        binding?.rvBranch?.adapter = branchAdapter
        branchAdapter?.setOnClickListener(object : BranchAdapter.OnClickListener {

            override fun onClick(position: Int, model: Branches) {

                val intent = Intent(this@DetailsActivity, CommitActivity::class.java)
                intent.putExtra("owner", ownerName)
                intent.putExtra("repo", repoName)
                intent.putExtra("branch", model.name)
                startActivity(intent)
            }
        })

    }

    fun setRecyclerViewOnStartforIsu() {
        binding?.rvIssues?.layoutManager = LinearLayoutManager(this)
        binding?.rvIssues?.setHasFixedSize(true)
        issueAdapter = IssueAdapter(this, issuesList)
        binding?.rvIssues?.adapter = issueAdapter
        issueAdapter?.setOnClickListener(object : IssueAdapter.OnClickListener {

            override fun onClick(position: Int, model: IssueModel) {
            }
        })

    }

    private fun getBranches(callbk: ApiCallback, owner: String, repo: String) {
        if (InternetCheck.isConnected(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: BranchService =
                retrofit.create(BranchService::class.java)
            val listCall: Call<ArrayList<Branches>> = service.getService(owner, repo)
            listCall.enqueue(object : Callback<ArrayList<Branches>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    response: Response<ArrayList<Branches>>,
                    retrofit: Retrofit
                ) {
                    if (response.isSuccess) {
//                        branchList=response.body()
//                        setRecyclerViewOnStart()
                        callbk.onSuccess(response.body())
                        Log.v("Paper", response.body().size.toString())
                    } else {
                        Toast.makeText(
                            this@DetailsActivity,
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

    private fun getIssues(callbk: ApiCallbackforIsu, owner: String, repo: String) {
        if (InternetCheck.isConnected(this)) {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: IssueService =
                retrofit.create(IssueService::class.java)
            val listCall: Call<ArrayList<IssueModel>> = service.getService(owner, repo)
            listCall.enqueue(object : Callback<ArrayList<IssueModel>> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    response: Response<ArrayList<IssueModel>>,
                    retrofit: Retrofit
                ) {
                    if (response.isSuccess) {
                        callbk.onSuccess(response.body())
                    } else {
                        Toast.makeText(
                            this@DetailsActivity,
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
}