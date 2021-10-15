package com.evosouza.myapplication.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.evosouza.myapplication.R
import com.evosouza.myapplication.ui.viewmodel.MainViewModel
import com.evosouza.myapplication.ui.viewmodel.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModels()
}