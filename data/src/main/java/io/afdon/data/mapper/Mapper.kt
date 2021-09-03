package io.afdon.data.mapper

interface Mapper<in I, out O> {

    fun map(input: I?) : O?
}