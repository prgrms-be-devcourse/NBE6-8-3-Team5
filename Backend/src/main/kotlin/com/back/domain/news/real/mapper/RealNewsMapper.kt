package com.back.domain.news.real.mapper

import com.back.domain.news.real.dto.RealNewsDto
import com.back.domain.news.real.entity.RealNews
import org.springframework.stereotype.Component

@Component
class RealNewsMapper {
    fun toEntityList(realNewsDtoList: List<RealNewsDto>): List<RealNews> {
        return realNewsDtoList.map { toEntity(it) }.toList()
    }

    fun toEntity(realNewsDto: RealNewsDto): RealNews {
        return RealNews(
            title=realNewsDto.title,
            content=realNewsDto.content,
            description=realNewsDto.description,
            link=realNewsDto.link,
            imgUrl=realNewsDto.imgUrl,
            originCreatedDate=realNewsDto.originCreatedDate,
            createdDate=realNewsDto.createdDate,
            mediaName=realNewsDto.mediaName,
            journalist=realNewsDto.journalist,
            originalNewsUrl=realNewsDto.originalNewsUrl,
            newsCategory=realNewsDto.newsCategory,
        )
    }

    fun toDtoList(realNewsList: List<RealNews>): List<RealNewsDto> {
        return realNewsList.map { toDto(it)}.toList()
    }

    fun toDto(realNews: RealNews): RealNewsDto {
        return RealNewsDto(
        realNews.id,
        realNews.title,
        realNews.content,
        realNews.description,
        realNews.link,
        realNews.imgUrl,
        realNews.originCreatedDate,
        realNews.createdDate,
        realNews.mediaName,
        realNews.journalist,
        realNews.originalNewsUrl,
        realNews.newsCategory
        )
    }
}
