/* 
Space Rover Express
Copyright (C) 2017 Codernauti
Eduard Bicego, Federico Ghirardelli

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.codernauti.spaceroverexpress.navigator;

/**
 * Created by Eduard on 23-Nov-17.
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 *Classe che si occupa di gestire i dati ricavabili dai sensori e calcolare l'orientamento del device
 */
class Compass implements SensorEventListener {

    private static final String TAG = "Compass";


    private boolean mActive;

    interface CompassListener {
        void changed(float orientation);
    }

    private CompassListener mListener;

    /**
     * Sensore che misura l'accelerazione del device sui tre assi fisici
     */
    private Sensor accelerometer;

    /**
     * variabile di guardia per accertarsi il reperimento di almeno un dato dall'accelerometro
     */
    private boolean lastAccelerometerSet = false;

    /**
     * variabile di guardia per accertarsi il reperimento di almeno un dato dal magnetometro
     */
    private boolean lastMagnetometerSet = false;

    /**
     * array che contiene gli ultimi dati ricevuti dal magnetometro
     */
    private float[] lastMagnetometerData = new float[3];

    /**
     * array che contiene gli ultimi dati ricevuti dall'accelerometro
     */
    private float[] lastAccelerometerData = new float[3];

    /**
     * Sensore che misura il campo magnetico per i tre assi fisici
     */
    private Sensor magnetometer;

    /**
     * gradi di orientamento sui tre assi fisici
     */
    private float[] orientation = new float[3];

    /**
     * matrice di rotazione ottenuta dai dati rilevati dai sensori
     */
    private float[] rotationMatrix = new float[9];

    /**
     * oggetto fornito dal sistema Android per ottenere le istanze dei sensori
     */
    private SensorManager mSensorManager;


    Compass(SensorManager sensorManager) {
        mSensorManager = sensorManager;

        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    /**
     * Metodo che restituisce l'ultimo dato calcolato dai dati ricavati dai sensori che indica l'orientamento del dispositivo.
     * @return  float
     */
    private float getLastCoordinate(float[] orientation) {
        return (float) Math.toDegrees(orientation[0]);
    }

    /**
     * Metodo che viene chiamato nel caso in cui l'accuratezza dei sensori è cambiata. Attualmente non viene utilizzato dall'applicativo
     * @param sensor Riferimento al sensore che ha scatenato l'evento
     * @param accuracy Nuova accuratezza impostata al sensore
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // NON UTILIZZATO
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == accelerometer) {
            lastAccelerometerData = event.values.clone(); //lowPass(event.values.clone(), lastAccelerometerData);
            //System.arraycopy(event.values, 0, lastAccelerometerData, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            lastMagnetometerData = event.values.clone(); //lowPass(event.values.clone(), lastMagnetometerData);
            //System.arraycopy(event.values, 0, lastMagnetometerData, 0, event.values.length);
            lastMagnetometerSet = true;
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometerData, lastMagnetometerData);
            SensorManager.getOrientation(rotationMatrix, orientation);

            if (mListener != null) {
                //Log.d(TAG, "Raw azimuth (degree): " + getLastCoordinate(orientation));
                updateLowPassFilter(getLastCoordinate(orientation));

                //Log.d(TAG, "Low Pass Filter (degree): " + mFilteredValue);

                mListener.changed(mFilteredValue);
            }
        }


        // thank you http://www.codingforandroid.com/2011/01/using-orientation-sensors-simple.html
        /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone(); //lowPass( event.values.clone(), mGravity );

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone(); //lowPass( event.values.clone(), mGeomagnetic );

        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

            if (success) {
                //float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                // at this point, orientation contains the azimuth, pitch and roll values.
                if (mListener != null) {
                    mListener.changed(getLastCoordinate());
                }
            }
        }*/
    }

    private float mFilteredValue;

    private void updateLowPassFilter(float rawValue) {
        // increase -> increase stability of data but increase the delay
        // decrease -> decrease stability of data but decrease the delay
        final float alpha = 0.8f;

        mFilteredValue = alpha * mFilteredValue + (1.0f - alpha) * rawValue;
    }

    /**
     * Metodo che permette all'oggetto Compass di ricevere dati dai sensori e quindi accenderli
     */
    public void registerListeners(){
        mActive = true;
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Metodo che permette all'oggetto Compass di smettere di ricevere dati dai sensori e quindi spegnerli
     */
    public void unregisterListeners() {
        mActive = false;
        mSensorManager.unregisterListener(this, accelerometer);
        mSensorManager.unregisterListener(this, magnetometer);
    }

    public void setListener(CompassListener listener) {
        mListener = listener;
    }

    public boolean isActive() {
        return mActive;
    }
}


