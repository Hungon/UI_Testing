package com.example.android.architecture.blueprints.todoapp.test.chapter1.data

object TestData {

    val toDoTitle: String
        get() = "item " + System.currentTimeMillis()

    val toDoDescription: String
        get() = "description " + System.currentTimeMillis()
}
