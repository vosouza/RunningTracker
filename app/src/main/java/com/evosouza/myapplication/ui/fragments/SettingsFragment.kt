package com.evosouza.myapplication.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.evosouza.myapplication.R
import com.evosouza.myapplication.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val viewModel: MainViewModel by viewModels()
}