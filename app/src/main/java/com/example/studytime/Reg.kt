package com.example.studytime

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Reg : AppCompatActivity() {
    //variables
    private lateinit var edUserName: EditText
    private lateinit var edPassword: EditText
    private lateinit var confirmPass: EditText
    private lateinit var btnReg: Button
   private lateinit var btnCancel: Button
    var tvHeading: TextView? = null

    //declare the instance of FB auth
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)
        tvHeading = findViewById(R.id.textView2)
        edUserName = findViewById(R.id.editTextTextEmailAddress2)
        edPassword = findViewById(R.id.editTextTextPassword2)
        confirmPass = findViewById(R.id.editTextTextPassword3)
        btnReg = findViewById(R.id.button3)
        btnCancel = findViewById(R.id.button4)

        //set the get instance for FB
        mAuth = FirebaseAuth.getInstance()

        btnReg.setOnClickListener(View.OnClickListener { view -> createUser(view) })
        btnCancel.setOnClickListener(View.OnClickListener { view ->cancel(view)  })
    }
fun cancel(view: View){
    val inty = Intent(this, Login::class.java)

    startActivity(inty)


}
    fun createUser(view: View) {
        if (view.id == R.id.button3) {
            val email = edUserName.text.toString().trim { it <= ' ' }
            val password = edPassword.text.toString().trim { it <= ' ' }
            val conPass = confirmPass.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Username can't be blank", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Password can't be blank", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(conPass)) {
                Toast.makeText(this, "Confirm password can't be blank", Toast.LENGTH_SHORT).show()
            }
            if (password == conPass) {
                mAuth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        this
                    ) { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // closes up the FB instance
                        } else {
                            Toast.makeText(this, "Failed to register", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Passwords are not the same", Toast.LENGTH_SHORT).show()

            }
        }
    }
}