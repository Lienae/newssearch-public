package com.tjeoun.newssearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tjeoun.newssearch.util.HtmlUtils.*;

@Service
@RequiredArgsConstructor
public class NewsSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<NewsDto> search(String keyword, String category, String mediaCompany, int page, int size) {

        // 1. 검색 조건 쿼리 구성
        Query keywordQuery = buildKeywordQuery(keyword);
        List<Query> filters = buildFilters(category, mediaCompany);

        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        if (keywordQuery != null) boolBuilder.must(keywordQuery);
        if (!filters.isEmpty()) boolBuilder.filter(filters);

        Query finalQuery = boolBuilder.build()._toQuery();

        // 2. Highlight 구성
        HighlightQuery highlightQuery = buildHighlightQuery();

        // 3. NativeQuery 구성
        PageRequest pageRequest = PageRequest.of(page, size);
        NativeQuery nativeQuery = NativeQuery.builder()
            .withQuery(finalQuery)
            .withHighlightQuery(highlightQuery)
            .withPageable(pageRequest)
            .withMinScore(2.0f)
            .build();

        // 4. 검색 실행 및 결과 매핑
        SearchHits<NewsDocument> searchHits = elasticsearchOperations.search(nativeQuery, NewsDocument.class);

        List<NewsDto> result = searchHits.getSearchHits().stream()
            .map(hit -> {
                NewsDto dto = NewsDto.fromDocument(hit.getContent());

                // 제목 highlight 처리
                List<String> titleHighlights = hit.getHighlightField("title");
                if (titleHighlights != null && !titleHighlights.isEmpty()) {
                    dto.setTitle(escapeAndConvertHighlight(titleHighlights.get(0)));
                }

                // 본문 highlight 처리
                List<String> contentHighlights = hit.getHighlightField("content");
                if (contentHighlights != null && !contentHighlights.isEmpty()) {
                    String raw = contentHighlights.get(0);
                    String highlighted = escapeAndConvertHighlight(raw);
                    dto.setContent(abbreviateHtml(highlighted, 80)); // HTML-aware 요약
                } else {
                    // fallback: 원문 요약
                    dto.setContent(abbreviate(hit.getContent().getContent(), 80)); // 80글자로 요약
                }

                return dto;
            })
            .collect(Collectors.toList());

        return new PageImpl<>(result, pageRequest, searchHits.getTotalHits());
    }

    private String abbreviate(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }


    private Query buildKeywordQuery(String keyword) {
        if (keyword == null || keyword.isEmpty()) return null;

        boolean isShortKeyword = keyword.length() < 5;
        int wordCount = keyword.trim().split("\\s+").length;

        return BoolQuery.of(b -> b
            // 1. 정확히 일치하는 경우
            .should(TermQuery.of(t -> t
                .field("title.keyword")
                .value(keyword)
                .boost(6.0f)
            )._toQuery())
            .should(TermQuery.of(t -> t
                .field("content.keyword")
                .value(keyword)
                .boost(5.0f)
            )._toQuery())

            // 2. 문장 유사 (phrase 기반)
            .should(MatchPhraseQuery.of(mp -> mp
                .field("title")
                .query(keyword)
                .boost(12.0f)
            )._toQuery())
            .should(MatchPhraseQuery.of(mp -> mp
                .field("content")
                .query(keyword)
                .boost(11.0f)
            )._toQuery())

            // 3. 일반 match (AND 필수)
            .should(MatchQuery.of(m -> m
                .field("title")
                .query(keyword)
                .operator(Operator.And)
                .boost(3.0f)
            )._toQuery())
            .should(MatchQuery.of(m -> m
                .field("content")
                .query(keyword)
                .operator(Operator.And)
                .boost(2.5f)
            )._toQuery())

            // 4. multi_match: 낮은 boost, 긴 키워드일 때만 fuzziness
            .should(MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("title^2", "content^1")
                .operator(Operator.And)
                .fuzziness((isShortKeyword || wordCount == 1) ? null : "AUTO")
                .boost(1.0f)
            )._toQuery())

            .minimumShouldMatch("1")
        )._toQuery();
    }


    private List<Query> buildFilters(String category, String mediaCompany) {
        List<Query> filters = new ArrayList<>();
        if (!"ALL".equalsIgnoreCase(category)) {
            filters.add(TermQuery.of(t -> t
                .field("category")
                .value(category)
            )._toQuery());
        }

        if (!"ALL".equalsIgnoreCase(mediaCompany)) {
            filters.add(TermQuery.of(t -> t
                .field("mediaCompany")
                .value(mediaCompany)
            )._toQuery());
        }

        return filters;
    }

    private HighlightQuery buildHighlightQuery() {
        HighlightParameters highlightParams = HighlightParameters.builder()
            .withPreTags("<b>")
            .withPostTags("</b>")
            .build();

        Highlight highlight = new Highlight(
            highlightParams,
            List.of(
                new HighlightField("title"),
                new HighlightField("content")
            )
        );

        return new HighlightQuery(highlight, NewsDocument.class);
    }
}
