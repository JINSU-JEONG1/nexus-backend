package com.nexus.backend.api.shortUrl.repository;

import com.nexus.backend.api.shortUrl.entity.ShortUrlStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ShortUrlStatsRepository extends JpaRepository<ShortUrlStats, Long> {
    Optional<ShortUrlStats> findByShortUrlId(Long shortUrlId);
    List<ShortUrlStats> findByShortUrlIdIn(Collection<Long> shortUrlIds);
}
