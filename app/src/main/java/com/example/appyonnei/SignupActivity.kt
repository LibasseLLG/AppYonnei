package com.example.appyonnei

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appyonnei.databinding.ActivitySignupBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SignupActivity : AppCompatActivity() {

    //Déclaration de l'activité et des variables membres
    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("users")


        //Gestion du bouton d'inscription et redirection vers la connexion
        binding.signupButton.setOnClickListener {
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()

            if (signupUsername.isNotEmpty()&& signupPassword.isNotEmpty()){
                signupUser(signupUsername, signupPassword)
            } else {
                Toast.makeText(this@SignupActivity, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirect.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }

    }

    private fun signupUser(username: String, password: String){
      databaseReference.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
          override fun onDataChange(dataSnapshot: DataSnapshot) {
              if(!dataSnapshot.exists()){
                  val id = databaseReference.push().key
                  val userData = UserData(id, username, password)
                  databaseReference.child(id!!).setValue(userData)
                  Toast.makeText(this@SignupActivity, "Inscription réussie", Toast.LENGTH_SHORT).show()
                  startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                  finish()
              } else {
                  Toast.makeText(this@SignupActivity, "L'utilisateur existe déjà", Toast.LENGTH_SHORT).show()
              }
          }

          override fun onCancelled(databaseError: DatabaseError) {
              Toast.makeText(this@SignupActivity, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()

          }
      })
    }
}