CREATE OR REPLACE VIEW view_emoticon_summary AS
SELECT
    news_id,
    CAST(SUM(IF(emoticon_enum = 'LIKE', 1, 0)) AS UNSIGNED)    AS like_count,
    CAST(SUM(IF(emoticon_enum = 'DISLIKE', 1, 0)) AS UNSIGNED)   AS dislike_count,
    CAST(SUM(IF(emoticon_enum = 'FUNNY', 1, 0)) AS UNSIGNED) AS funny_count,
    CAST(SUM(IF(emoticon_enum = 'SAD', 1, 0)) AS UNSIGNED)     AS sad_count,
    CAST(SUM(IF(emoticon_enum = 'ANGRY', 1, 0)) AS UNSIGNED)   AS angry_count,
    COUNT(*)                                                   AS total_count
FROM emoticon
GROUP BY news_id;
