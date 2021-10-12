package com.asknsolve.tiltsensor

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.asknsolve.tiltsensor.databinding.ActivityMainBinding

// Listener를 this로 지정했으므로 MainActivity class가 SensorEventListener를 구현하도록 추가
// 미구현 method가 있음: onAccuracyChanged(), onSensorChanged()
class MainActivity : AppCompatActivity(), SensorEventListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    // 센서 준비
    private val sensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // TiltView를 화면에 배치하기 위하여 늦은 초기화 선언
    private lateinit var tiltView: TiltView

    override fun onCreate(savedInstanceState: Bundle?) {
        // 화면 꺼짐 방지
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        // 가로 모드 고정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
        // setContentView(binding.root)

        // TiltView를 화면에 배치
        // 생성자에 this를 넘겨서 TiltView를 초기화
        tiltView = TiltView(this)
        // 기존의 setContentView(binding.root) 대신에 tiltView를 setContentView()method에 전달
        // tiltView가 전체 레이아웃이 됨
        setContentView(tiltView)
    }

    // 센서 등록
    override fun onResume() {
        super.onResume()
        // Listener를 this로 지정했으므로 MainActivity class가 SensorEventListener를 구현하도록 추가
        sensorManager.registerListener(this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL)
        // 첫 번째 인자: 센서값, 여기서는 this를 지정하여 activity에서 센서값 받음
        // 두 번째 인자: 사용할 센서 종류 지정
        // 세 번째 인자: 센서값을 얼마나 자주 받을지를 지정, SensorManager class에 정의된 상수 중 하나를 선택
            // SENSOR_DELAY_FASTEST: 가능한 자주
            // SENSOR_DELAY_GAME: 게임에 적합한 정도
            // SENSOR_DELAY_NORMAL: 화면 방향이 전환될 때 적합한 정도
            // SENSOR_DELAY_UI: 사용자 인터페이스를 표시하기에 적합한 정도
    }

    // 센서 정밀도가 변경되면 호출됨
    override fun onSensorChanged(event: SensorEvent?) {
        // 센서값이 변경되면 호출됨
        // values[0]: x축 값: 위로 기울이면 -10~0, 아래로 기울이면 0~10
        // values[1]: y축 값: 왼쪽으로 기울이면 -10~0, 오른쪽으로 기울이면 0~10
        // values[2]: z축 값: 미사용
        event?.let {
            Log.d("MainActivity",
                "onSensorChanged: x: ${event.values[0]}, "
                        + "y: ${event.values[1]}, " +
                        "z: ${event.values[2]}")
        }
        tiltView.onSensorEvent(event)
    }

    // 센서값이 변경되면 호출됨
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

    // 센서 해제
    // Activity가 동작 중일때만 센서를 사용하려면
    // 화면이 꺼지기 직전인 onPause() method에서 센서를 해제
    // unregisterListener() method를 이용하여 센서 사용을 해제
    // 인자로 SensorEventListener 객체를 지정
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }


}