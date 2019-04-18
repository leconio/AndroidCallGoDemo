package io.lecon.callgo

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import mobile.Mobile

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val task  = RunTask()
        task.execute()

    }


    internal class RunTask : AsyncTask<Void, Void, Any?>() {
        override fun doInBackground(vararg params: Void): Any? {
            Mobile.sayHello()
            Mobile.sayHelloWithParams("lecon")
            val result = Mobile.sayHelloWithParamsAndReturn("spawn")
            Log.d("AndroidGo",result)
            try {
                Mobile.sayHelloWithParamsAndReturnAndException("liucl")
            } catch (e:Exception) {
                e.printStackTrace()
            }
            return null
        }

    }

}
