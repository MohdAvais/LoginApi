package e.avinaqvi.loginapiwithkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val Url = "https://mekvahan.com/api/user/login"
    val TAG   = this::class.java.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loginBtn.setOnClickListener {
            userLogin()
        }

    }

    private fun userLogin() {

        var  etPassword = passwordEt.text.toString()
        var etmobileNumber = mobileNoEt.text.toString()

        if (etmobileNumber.isEmpty() || etPassword.isEmpty()) {
            val toast = Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show()
        }

        if (etPassword.length < 6) {

            val toast = Toast.makeText(this, "your password length must be atleast 6", Toast.LENGTH_LONG).show()

        }

        val intent = Intent(this, HomeActivity::class.java)

        appLogin()
    }



    private fun appLogin() {

      val stringRequest = object : StringRequest(Request.Method.POST,Url,Response.Listener<String>{response ->

          var  etPassword = passwordEt.text.toString()
          var etmobileNumber = mobileNoEt.text.toString()
        try {

            val jsonObject = JSONObject(response)
            val status = jsonObject.getInt("status")
            if (status != 1) {

                val toast = Toast.makeText(this,response.toString(), Toast.LENGTH_SHORT).show()
                Log.e(TAG,response.toString())
                return@Listener
            }
                val resp = jsonObject.getJSONObject("response")
                val tokenType = resp.getString("token_type")
                val accessToken = resp.getString("access_token")

                val user =resp.getJSONObject("user")
                Log.e(TAG,user.toString())

                val userId = user.getInt("id")
                val name = user.getString("name")
                val mobile = user.getString("mobile")
                val email = user.getString("email")
                val referralCode = user.getString("referral_code")
                val rc = user.getString("rc")
                val profile = user.getString("profile")
                val others = user.getString("others")

                val toast = Toast.makeText(this, "Login Successfull!!", Toast.LENGTH_SHORT).show()

            this.onBackPressed()
        }
            catch (e : Exception){
                Log.e(TAG, "catch block"+e.toString())
                val toast = Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
      }, Response.ErrorListener{error ->

          Log.e(TAG,"error listener"+ error.toString())
      })
      {
          override fun getHeaders() : MutableMap<String, String> {

              var headers = mutableMapOf<String,String>()
              headers.put("accept","application/json")

              return headers
          }

          override fun getParams(): MutableMap<String, String> {

              var param = mutableMapOf<String,String>()
              param.put("mobile",mobileNoEt.toString())
              param.put("password",passwordEt.toString())
              param.put("provider","user")
              return param
          }
        }
        stringRequest.retryPolicy = DefaultRetryPolicy(5000, 6, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        MySingleton.getInstance(this).addToRequestQueue(stringRequest)
    }
}
