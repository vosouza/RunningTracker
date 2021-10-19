package com.evosouza.myapplication.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.evosouza.myapplication.R
import com.evosouza.myapplication.util.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject lateinit var sharedPref : SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNameOnTextView()
        btnApplyChanges.setOnClickListener {
            val success = writeDataToSharedPreferences()
            if(success){
                Snackbar.make(requireView(), "Success on apply changes", Snackbar.LENGTH_SHORT).show()
                setNameOnTextView()
            }else{
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setNameOnTextView()
    }

    private fun setNameOnTextView(){
        etName.setText(sharedPref.getString(Constants.KEY_NAME, "") ?: "")
        etWeight.setText(sharedPref.getFloat(Constants.KEY_WEIGHT, 80f).toString())
    }

    private fun writeDataToSharedPreferences(): Boolean{
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedPref.edit()
            .putString(Constants.KEY_NAME, name)
            .putFloat(Constants.KEY_WEIGHT, weight.toFloat())
            .apply()

        var txt = "Let's Go $name!"
        requireActivity().tvToolbarTitle.text = txt
        return true
    }
}