package com.evosouza.myapplication.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evosouza.myapplication.db.Run
import com.evosouza.myapplication.repository.MainRepository
import com.evosouza.myapplication.util.SortType
import kotlinx.android.synthetic.main.fragment_run.*
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
): ViewModel() {


    private val runSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runSortedByCalories = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runSortedByTimeMillis = mainRepository.getAllRunsSortedByTimeMillis()
    private val runSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()

    val run = MediatorLiveData<List<Run>>()
    var sortType = SortType.DATE

    init {
        run.addSource(runSortedByDate){result ->
            if(sortType == SortType.DATE){
                result?.let {
                    run.value = it
                }
            }
        }
        run.addSource(runSortedByDistance){result ->
            if(sortType == SortType.DISTANCE){
                result?.let {
                    run.value = it
                }
            }
        }
        run.addSource(runSortedByCalories){result ->
            if(sortType == SortType.CALORIES){
                result?.let {
                    run.value = it
                }
            }
        }
        run.addSource(runSortedByTimeMillis){result ->
            if(sortType == SortType.RUNNING_TIME){
                result?.let {
                    run.value = it
                }
            }
        }
        run.addSource(runSortedByAvgSpeed){result ->
            if(sortType == SortType.AVG_SPEED){
                result?.let {
                    run.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runSortedByDate.value?.let { run.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeMillis.value?.let { run.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { run.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeed.value?.let { run.value = it }
        SortType.CALORIES -> runSortedByCalories.value?.let { run.value = it }
    }
    fun insertRun(run: Run) = viewModelScope.launch{
        mainRepository.insertRun(run)
    }

}