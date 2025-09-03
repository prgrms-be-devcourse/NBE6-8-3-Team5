package com.back.domain.quiz.detail.service

import com.back.domain.news.real.entity.RealNews
import com.back.domain.news.real.repository.RealNewsRepository
import com.back.domain.quiz.detail.event.DetailQuizCreatedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class DetailQuizEventService(
    private val detailQuizAsyncService: DetailQuizAsyncService,
    private val realNewsRepository: RealNewsRepository,
    private val publisher: ApplicationEventPublisher
) {
    fun generateDetailQuizzes(realNewsIds: List<Long>) {
        val realNewsList: List<RealNews> = realNewsRepository.findAllById(realNewsIds)

        if (realNewsList.isEmpty()) {
            log.info("there is no real news to generate quizzes for. Skipping quiz generation. News IDs: $realNewsIds")
            return
        }

        log.info("상세 퀴즈 생성 시작. 뉴스 개수: ${realNewsList.size}")

        // 모든 뉴스에 대해 비동기 처리 (Rate Limiter가 속도 조절)
        val futureMap: Map<Long, CompletableFuture<Void>> =
            realNewsList.associate { it.id to detailQuizAsyncService.generateAsync(it.id) }
        val allOf = CompletableFuture.allOf(*futureMap.values.toTypedArray())

        try {
            // 비동기 작업이 모두 끝날 때까지 대기
            allOf.join()
            log.info("모든 퀴즈 생성 작업이 완료되었습니다.")
            publisher.publishEvent(DetailQuizCreatedEvent())
        } catch (e: Exception) {
            val failedIds = futureMap.filter { (_, f) -> f.isCompletedExceptionally }.keys
            log.error("일부 퀴즈 생성 작업 실패. 실패 newsIds: {}", failedIds, e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DetailQuizEventService::class.java)
    }
}
