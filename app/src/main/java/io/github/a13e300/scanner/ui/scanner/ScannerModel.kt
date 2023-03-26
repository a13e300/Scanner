package io.github.a13e300.scanner.ui.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerModel : ViewModel() {

    val isScanning: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(true)
    }

    val scanResult: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>(null)
    }
}