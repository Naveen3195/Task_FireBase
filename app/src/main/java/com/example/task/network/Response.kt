package com.example.task.network

class Response private constructor(val status: Status, val data: Any?, val error: Throwable?) {
    companion object {

        fun loading(): Response {
            return Response(Status.LOADING, null, null)
        }

        fun success(data: Any): Response {
            return Response(Status.SUCCESS, data, null)
        }

        fun error(error: Throwable): Response {
            return Response(Status.ERROR, null, error)
        }
    }
}