package com.example.myclickerapp.Core

interface Mapper<S,R> {

    fun map(s: S): R

    interface List<S, R> : Mapper<S, R> {
        fun mapList(s : kotlin.collections.List<S>) : kotlin.collections.List<R> {
            return s.map { map(it) }
        }
    }

    interface Unit<S> : Mapper<S, kotlin.Unit>

    interface Default<R> {
        fun map() : R

    }
}