package com.nosh.appchi.`fun`.jokes

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.nosh.appchi.R
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class JokesMainFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, saveInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.f_f_jokes_main, container, false)}
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreference = requireActivity().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()

        createNotificationChannel()
        val scheduleButton : Button = view.findViewById(R.id.gotTime)
        val timeEditText = view.findViewById<EditText>(R.id.time_picker)
        timeEditText.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireActivity(),
                { _, hourOfDay, minute ->
                    val hourString = if (hourOfDay < 10) "0$hourOfDay" else "$hourOfDay"
                    val minuteString = if (minute < 10) "0$minute" else "$minute"
                    timeEditText.setText("$hourString:$minuteString")
                },
                0,
                0,
                true
            )
            timePickerDialog.show()
        }
        val timeText: TextView = view.findViewById(R.id.textView7)
        timeText.text = sharedPreference.getString("jokeTime", getString(R.string.not_selected))

        if (sharedPreference.getBoolean("jf", true)){
            val assetManager = requireActivity().assets
            val inputStream = assetManager.open("defJokes.txt")
            val inputString = inputStream.bufferedReader().use { it.readText() }
            writeJokes(inputString)
        }
        editor.putBoolean("jf", false)
        editor.apply()

        scheduleButton.setOnClickListener {
            val timeString = timeEditText.text.toString()
            if (isValidTime(timeString)){
                timeText.text = timeString
                editor.putString("jokeTime", timeString)
                editor.apply()
                val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"))
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, time.hour)
                    set(Calendar.MINUTE, time.minute)
                    set(Calendar.SECOND, 0)
                }
                val notification = NotificationPublisher().createNotification(requireContext())
                scheduleNotification(notification, calendar.timeInMillis)
            }
        }

        val btn: Button = view.findViewById(R.id.button4)
        btn.setOnClickListener{
            showJoke(getRandomJoke())
        }
    }

    private fun writeJokes(jokes: String) {
        val path = requireActivity().filesDir
        val letDirectory = File(path, "LET")
        letDirectory.mkdirs()
        val file = File(letDirectory, "jokes.txt")
        file.writeText(jokes)
    }


    private fun getRandomJoke(): String {
        val path = requireActivity().filesDir
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "jokes.txt")
        return FileInputStream(file).bufferedReader().use(BufferedReader::readText).lines()
            .shuffled().first()
    }

    private fun showJoke(joke: String){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.your_joke)
            .setMessage(joke)
        builder.setPositiveButton("OK") { _, _ ->
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun isValidTime(time: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(time)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("Jokes", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun scheduleNotification(notification: Notification, timeInMillis: Long) {
        val alarmManager =requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireActivity(), NotificationPublisher::class.java).apply {
            putExtra(NotificationPublisher.NOTIFICATION_ID, 1)
            putExtra(NotificationPublisher.NOTIFICATION, notification)
        }
        val pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

}

class NotificationPublisher : BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val NOTIFICATION = "notification"
    }

    private fun getRandomJoke(context: Context): String {
        val path = context.filesDir
        val letDirectory = File(path, "LET")
        val file = File(letDirectory, "jokes.txt")
        return FileInputStream(file).bufferedReader().use(BufferedReader::readText).lines()
            .shuffled().first()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(context: Context): Notification {
        val intent = Intent(context, Jokes::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val bigTextStyle = NotificationCompat.BigTextStyle().bigText(getRandomJoke(context))
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,  0)
        val builder = NotificationCompat.Builder(context, "Jokes")
            .setSmallIcon(R.drawable.ic_baseline_cruelty_free_24)
            .setContentTitle(context.resources.getString(R.string.your_joke))
            .setStyle(bigTextStyle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        return builder.build()
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)
        val notification = createNotification(context)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
}