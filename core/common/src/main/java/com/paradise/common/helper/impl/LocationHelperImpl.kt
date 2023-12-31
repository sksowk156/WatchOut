package com.paradise.common.helper.impl

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.paradise.common.helper.LocationHelper
import com.paradise.common.helper.method.checkPermissions
import javax.inject.Inject

class LocationHelperImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val geocoder: Geocoder,
) : LocationHelper {

    /**
     * Set last location event listener
     * 지금 위치 확인 후 이벤트 처리
     * @param lastLocationListener
     * @receiver
     */
    @SuppressLint("MissingPermission")
    override fun setLastLocationEventListener(lastLocationListener: (Location) -> Unit) {
        checkPermissions( // 위치 권한이 되어 있는지 확인
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? -> //
                if (location != null) {
                    lastLocationListener(location)
                }
            }
        }
    }

    /**
     * Get reverse geocoding
     * 위, 경도 정보를 주소 정보로 변환후 이벤트 처리
     * @param latitude
     * @param longitude
     * @param reverseGeocoderListener
     * @receiver
     */
    override fun getReverseGeocoding(
        latitude: Double,
        longitude: Double,
        reverseGeocoderListener: (Address) -> Unit, // 가져온 주소로 이벤트 처리
    ) {
        if (Build.VERSION.SDK_INT >= 33) {
            geocoder.getFromLocation(latitude,
                longitude,
                2,
                @RequiresApi(33) object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if (addresses.size > 0) reverseGeocoderListener(addresses.first())
                    }

                    override fun onError(errorMessage: String?) {
                        super.onError(errorMessage)
                        Log.e("whatisthis", errorMessage.toString())
                    }
                })
        } else {
            val addresses = geocoder.getFromLocation(
                latitude, longitude, 2
            )
            if (addresses != null) {
                if (addresses.size > 0) reverseGeocoderListener(addresses.first())
            }
        }
    }

}