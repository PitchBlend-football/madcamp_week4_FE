// SignUpActivity.kt

package com.example.pitchblend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.pitchblend.databinding.ActivitySignUpBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var completeBtn: Button
    val api = RetrofitInterface.create()
    private var selectedTeam = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initiation()
//        binding.chelseaBtn.setOnClickListener {
//            selectedTeam = 9
//            Log.e("chelsea", "chelsea")
//            complete(selectedTeam)
//        }
        complete()
    }

    private fun initiation() {
        completeBtn = binding.completeBtn

        binding.liverpoolBtn.setOnClickListener {
            selectedTeam = 1
            binding.liverpoolBtn.setImageResource(R.drawable.liverpool_click)
            binding.mancityBtn.setImageResource(R.drawable.choose_mancity)
            binding.arsenalBtn.setImageResource(R.drawable.choose_arsenal)
            binding.tottenhamBtn.setImageResource(R.drawable.choose_tottenham)
            binding.manutdBtn.setImageResource(R.drawable.choose_manutd)
            binding.chelseaBtn.setImageResource(R.drawable.choose_chelsea)
            //complete(selectedTeam)
        }
        binding.mancityBtn.setOnClickListener {
            selectedTeam = 2
            Log.e("mancity", "mancity")
            binding.liverpoolBtn.setImageResource(R.drawable.choose_liverpool)
            binding.mancityBtn.setImageResource(R.drawable.mancity_click)
            binding.arsenalBtn.setImageResource(R.drawable.choose_arsenal)
            binding.tottenhamBtn.setImageResource(R.drawable.choose_tottenham)
            binding.manutdBtn.setImageResource(R.drawable.choose_manutd)
            binding.chelseaBtn.setImageResource(R.drawable.choose_chelsea)
            //complete(selectedTeam)
        }
        binding.arsenalBtn.setOnClickListener {
            selectedTeam = 4
            binding.liverpoolBtn.setImageResource(R.drawable.choose_liverpool)
            binding.mancityBtn.setImageResource(R.drawable.choose_mancity)
            binding.arsenalBtn.setImageResource(R.drawable.arsenal_click)
            binding.tottenhamBtn.setImageResource(R.drawable.choose_tottenham)
            binding.manutdBtn.setImageResource(R.drawable.choose_manutd)
            binding.chelseaBtn.setImageResource(R.drawable.choose_chelsea)
            //complete(selectedTeam)
        }
        binding.tottenhamBtn.setOnClickListener {
            selectedTeam = 5
            binding.liverpoolBtn.setImageResource(R.drawable.choose_liverpool)
            binding.mancityBtn.setImageResource(R.drawable.choose_mancity)
            binding.arsenalBtn.setImageResource(R.drawable.choose_arsenal)
            binding.tottenhamBtn.setImageResource(R.drawable.tottenham_click)
            binding.manutdBtn.setImageResource(R.drawable.choose_manutd)
            binding.chelseaBtn.setImageResource(R.drawable.choose_chelsea)
            //complete(selectedTeam)
        }
        binding.manutdBtn.setOnClickListener {
            selectedTeam = 7
            binding.liverpoolBtn.setImageResource(R.drawable.choose_liverpool)
            binding.mancityBtn.setImageResource(R.drawable.choose_mancity)
            binding.arsenalBtn.setImageResource(R.drawable.choose_arsenal)
            binding.tottenhamBtn.setImageResource(R.drawable.choose_tottenham)
            binding.manutdBtn.setImageResource(R.drawable.manutd_click)
            binding.chelseaBtn.setImageResource(R.drawable.choose_chelsea)
            //complete(selectedTeam)
        }
        binding.chelseaBtn.setOnClickListener {
            selectedTeam = 8
            binding.liverpoolBtn.setImageResource(R.drawable.choose_liverpool)
            binding.mancityBtn.setImageResource(R.drawable.choose_mancity)
            binding.arsenalBtn.setImageResource(R.drawable.choose_arsenal)
            binding.tottenhamBtn.setImageResource(R.drawable.choose_tottenham)
            binding.manutdBtn.setImageResource(R.drawable.choose_manutd)
            binding.chelseaBtn.setImageResource(R.drawable.chelsea_click)
            Log.e("chelsea", "chelsea")
            //complete(selectedTeam)
        }

    }

    private fun complete() {
        completeBtn.setOnClickListener {

            val call = api.postSignUp(
                binding.signUpName.text.toString(),
                binding.signUpPhone.text.toString(),
                binding.signUpPassword.text.toString(),
                binding.signUpEmail.text.toString(),
                binding.signUpNickname.text.toString(),
                selectedTeam
            )
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.e("Lets go", "$result")
                        Log.e("Lets go", "success!! good!!")
                    } else {
                        Log.e("Lets go", "what's wrong...")
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.e("mad..nn", "so sad plz")
                }
            })

            Log.e("sign_up_name", binding.signUpName.text.toString())
            Log.e("sign_up_name", binding.signUpPhone.text.toString())
            Log.e("sign_up_name", binding.signUpPassword.text.toString())
            Log.e("sign_up_name", binding.signUpEmail.text.toString())
            Log.e("sign_up_name", binding.signUpNickname.text.toString())
            Log.e("sign_up_name", selectedTeam.toString())

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }
}