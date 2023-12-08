package com.paradise.domain.usecases

import com.paradise.data.repository.AnalyzerResultRepository
import javax.inject.Inject

class GetWinkItemListUseCase @Inject constructor(private val analyzerResultRepository: AnalyzerResultRepository) {
    operator fun invoke(recordId: Int) = analyzerResultRepository.getWinkCount(recordId)
}