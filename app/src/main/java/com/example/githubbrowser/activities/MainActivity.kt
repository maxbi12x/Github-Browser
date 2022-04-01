package com.example.githubbrowser.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubbrowser.callingservice.RepoService
import com.example.githubbrowser.database.DatabaseHandler
import com.example.githubbrowser.databinding.ActivityMainBinding
import com.example.githubbrowser.responsemodels.ItemType
import com.example.githubbrowser.responsemodels.RepoModel
import com.example.githubbrowser.utils.Constants
import com.example.githubbrowser.utils.InternetCheck
import com.example.githubbrowser.utils.MyAdapter
import retrofit.*

class MainActivity : AppCompatActivity() {

    private var itemList: ArrayList<ItemType> = ArrayList()
    private var desCrip: String? = null
    private var curOwner: String? = null
    private var curRepo: String? = null
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val returnString: String? = data?.getStringExtra("owner")
                curOwner = returnString
                val returns: String? = data?.getStringExtra("repo")
                curRepo = returns
                val sreturn: String? = data?.getStringExtra("desc")
                desCrip = sreturn
                itemList.add(ItemType(curOwner!!, curRepo!!, desCrip!!))
                startRV()

            }

        }

    private var resultLauncherForDetails =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val returnString: String? = data?.getStringExtra("pos")
                val pos = returnString!!
                itemList.removeAt(pos.toInt())
                startRV()
            }

        }

    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        refreshRVonStart()
        startRV()
        binding?.button?.setOnClickListener {
            openAddRepoIntent()
        }
        binding?.mainAddRepo?.setOnClickListener {
            openAddRepoIntent()
        }

    }

    private fun refreshRVonStart() {
        val db = DatabaseHandler(this@MainActivity)
        val list = db.getRepo()

        for (i in list) {
            val list = i.value.split("+")
            val owner: String = list[0]
            val repo: String = list[1]
            var repoList: RepoModel?
            if (InternetCheck.isConnected(this)) {


                val retrofit: Retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)

                    .addConverterFactory(GsonConverterFactory.create())

                    .build()


                val service: RepoService =
                    retrofit.create(RepoService::class.java)


                val listCall: Call<RepoModel> = service.getService(owner, repo)
                listCall.enqueue(object : Callback<RepoModel> {
                    override fun onResponse(
                        response: Response<RepoModel>,
                        retrofit: Retrofit
                    ) {


                        if (response.isSuccess) {
                            repoList = response.body()
                            val k: String = repoList!!.description
                            itemList.add(ItemType(owner, repo, k))
                            startRV()
                            Log.i("HyperX", "$repoList")

                        } else {
                            Toast.makeText(
                                this@MainActivity,
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
                            return
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        Toast.makeText(
                            this@MainActivity,
                            "Unable to fetch data",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun startRV() {
        if (itemList.size > 0) {
            binding?.repoList?.visibility = View.VISIBLE
            binding?.llayout?.visibility = View.GONE
            setRecyclerViewOnStart()
        } else {
            binding?.repoList?.visibility = View.GONE
            binding?.llayout?.visibility = View.VISIBLE
        }
    }

    fun setRecyclerViewOnStart() {
        binding?.repoList?.layoutManager = LinearLayoutManager(this)
        binding?.repoList?.setHasFixedSize(true)
        val iAdapter = MyAdapter(this, itemList)
        binding?.repoList?.adapter = iAdapter
        iAdapter.setOnClickListener(object : MyAdapter.OnClickListener {

            override fun onClick(position: Int, model: ItemType, QueryNo: Int) {
                if (QueryNo == 1) {
                    sendTextToOtherApps("Owner : ${model.owner}\nRepo : ${model.repo}\nDescription : ${model.desc}")
                } else if (QueryNo == 0) {
                    openDetailsActivity(model.owner, model.repo, model.desc, position)
                }
            }
        })

    }

    private fun openAddRepoIntent() {


        val intent = Intent(this@MainActivity, AddRepoActivity::class.java)
        resultLauncher.launch(intent)
    }

    fun sendTextToOtherApps(text: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    fun openDetailsActivity(owner: String, repo: String, desc: String, pos: Int) {
        val intent = Intent(this@MainActivity, DetailsActivity::class.java)
        intent.putExtra("owner", owner)
        intent.putExtra("repo", repo)
        intent.putExtra("desc", desc)
        intent.putExtra("pos", pos.toString())
        resultLauncherForDetails.launch(intent)
    }
}