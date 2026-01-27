package com.nexus.backend.api.shortUrl;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.repository.ShortUrlQueryRepository;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import com.nexus.backend.api.shortUrl.service.ShortUrlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShortUrlServiceTest {

    @InjectMocks
    private ShortUrlService shortUrlService;

    @Mock
    private ShortUrlRepository shortUrlRepository;

    @Mock
    private ShortUrlQueryRepository shortUrlQueryRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private Cache mockCache;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SetOperations<String, String> setOperations;

    @Test
    @DisplayName("Short URL 생성 테스트 - 새로운 URL인 경우")
    void createShortUrl_New() {
        // given
        String originUrl = "https://www.google.com";
        ShortUrlRequestDTO req = new ShortUrlRequestDTO(originUrl, null);
        ShortUrl savedEntity = ShortUrl.builder()
                                    .id(12345L)
                                    .originUrl(originUrl)
                                    .shortUrl("b")
                                    .build();
        
        // baseUrl 주입
        ReflectionTestUtils.setField(shortUrlService, "baseUrl", "http://localhost/");

        given(cacheManager.getCache(anyString())).willReturn(mockCache);
        given(mockCache.get(originUrl)).willReturn(null);
        given(shortUrlRepository.findByOriginUrl(originUrl)).willReturn(Optional.empty());
        given(shortUrlRepository.save(any(ShortUrl.class))).willReturn(savedEntity);
        
        // when
        ShortUrlResponseDTO result = shortUrlService.createShortUrl(req);

        // then
        assertThat(result.getOriginUrl()).isEqualTo(originUrl);
        assertThat(result.getShortUrl()).contains("http://localhost/");
        verify(shortUrlRepository, times(1)).save(any(ShortUrl.class));
        verify(mockCache, atLeastOnce()).put(anyString(), anyString());
    }

    @Test
    @DisplayName("Short URL 생성 테스트 - 이미 존재하는 URL인 경우")
    void createShortUrl_Existing() {
        // given
        String originUrl = "https://www.google.com";
        ShortUrlRequestDTO req = new ShortUrlRequestDTO(originUrl, null);
        ShortUrl existingEntity = ShortUrl.builder().id(1L).originUrl(originUrl).shortUrl("b").build();
        
        ReflectionTestUtils.setField(shortUrlService, "baseUrl", "http://localhost/");

        given(cacheManager.getCache(anyString())).willReturn(mockCache);
        given(mockCache.get(originUrl)).willReturn(null);
        given(shortUrlRepository.findByOriginUrl(originUrl)).willReturn(Optional.of(existingEntity));
        
        // when
        ShortUrlResponseDTO result = shortUrlService.createShortUrl(req);

        // then
        assertThat(result.getOriginUrl()).isEqualTo(originUrl);
        assertThat(result.getShortUrl()).isEqualTo("http://localhost/b");
        verify(shortUrlRepository, never()).save(any(ShortUrl.class));
    }

    @Test
    @DisplayName("단축 URL로 원본 URL 조회 테스트")
    void getOriginUrlByShortUrl_Success() {
        // given
        String shortKey = "b";
        String originUrl = "https://www.google.com";
        ShortUrl entity = ShortUrl.builder().id(1L).originUrl(originUrl).shortUrl(shortKey).build();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForSet()).willReturn(setOperations);
        given(cacheManager.getCache(anyString())).willReturn(mockCache);
        given(mockCache.get(shortKey, String.class)).willReturn(null);
        given(shortUrlRepository.findByShortUrl(shortKey)).willReturn(Optional.of(entity));

        // when
        String result = shortUrlService.getOriginUrlByShortUrl(shortKey);

        // then
        assertThat(result).isEqualTo(originUrl);
        verify(valueOperations, times(1)).increment("click:cnt:" + shortKey);
        verify(setOperations, times(1)).add("dirty_short_urls", shortKey);
        verify(mockCache, times(1)).put(shortKey, originUrl);
    }
}
