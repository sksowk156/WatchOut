package com.paradise.drowsydetector.data.remote.parkinglot

import com.paradise.drowsydetector.utils.ApiKey
import com.paradise.drowsydetector.utils.DEFAULTNUMOFROWS
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ParkingLotInterface {
    @GET("tn_pubr_prkplce_info_api?serviceKey=${ApiKey.API_KEY}")
    suspend fun getAllParkingLot(
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = DEFAULTNUMOFROWS,
        @Query("type") type: String = "json",
        @Query("parkingchrgeInfo") parkingchrgeInfo : String
    ): Response<ParkingLot>
}