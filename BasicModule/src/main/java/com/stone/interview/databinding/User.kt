package com.stone.interview.databinding

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.stone.interview.BR

class User : BaseObservable() {
    @get:Bindable
    var name: String = "wyx"
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var pwd: String = "123456"
        set(value) {
            field = value
            notifyPropertyChanged(BR.pwd)
        }
}
