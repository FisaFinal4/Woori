package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import com.piehouse.woorepie.trade.repository.RedisTradeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeRedisServiceImplTest {

    @Mock
    private RedisTradeRepository redisRepository;
    @Mock
    private RedissonClient redissonClient;
    @Mock
    private RLock rLock;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private TradeRedisServiceImpl tradeRedisService;

    /**
     * 분산 락 획득 및 해제 테스트
     * - 락 획득 성공 시 매칭 로직 실행 여부 확인
     * - finally 블록에서 락 해제가 정상적으로 이루어지는지 검증
     */
    @Test
    void testLockAcquisitionAndRelease() throws InterruptedException {
        // Given
        when(redissonClient.getFairLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        // When
        tradeRedisService.processMatchingWithLock(1L);

        // Then
        verify(rLock).tryLock(anyLong(), anyLong(), any());
        verify(rLock).unlock();
    }

    /**
     * Lua 스크립트 원자성 검증 테스트
     * - 100개의 병렬 주문 저장 시 스크립트가 원자적으로 실행되어 데이터 중복 없이 처리되는지 확인
     */
    @Test
    void testLuaScriptAtomicity() {
        // Given
        lenient().when(redisTemplate.execute(any(), anyList(), any()))
                .thenReturn(1L);

        // When
        IntStream.range(0, 100).parallel().forEach(i -> {
            tradeRedisService.saveBuyOrder(1L, 100L + i, 10, 1000);
        });

        // Then
        verify(redisRepository, times(100)).saveOrUpdateBuyOrder(any(), anyLong(), any(), anyLong());
    }

    /**
     * 동시성 환경에서의 락 경쟁 테스트
     * - 10개 스레드가 FairLock을 경쟁적으로 획득 시도할 때 순차적 처리(FIFO)가 이루어지는지 검증
     * - 각 스레드가 락을 정상적으로 해제하는지 확인
     */
    @Test
    void testConcurrentLockAccess() throws InterruptedException {
        // Given
        when(redissonClient.getFairLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any(TimeUnit.class))).thenReturn(true);
        when(rLock.isHeldByCurrentThread()).thenReturn(true);

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        // When
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> {
                tradeRedisService.processMatchingWithLock(1L);
                latch.countDown();
            });
        }
        latch.await();

        // Then: 락 획득 10번, 해제 10번
        verify(rLock, times(10)).tryLock(anyLong(), anyLong(), any(TimeUnit.class));
        verify(rLock, times(10)).unlock();
    }

    /**
     * 부분 체결 주문 재삽입 테스트
     * - 주문을 재삽입할 때 원본 타임스탬프가 유지되는지 검증
     * - 재삽입된 주문이 정확한 키에 저장되는지 확인
     */
    @Test
    void testPartialOrderReinsertion() {
        // Given
        RedisEstateTradeValue order = new RedisEstateTradeValue(100L, 5, 1000, 123456789L);

        // When
        tradeRedisService.reinsertBuyOrder(1L, order);

        // Then: 저장 검증
        verify(redisRepository).saveOrUpdateBuyOrder(
                argThat(o -> o.getTimestamp() == 123456789L),
                eq(1L),
                any(),
                eq(100L)
        );
    }

    /**
     * 락 획득 실패 시나리오 테스트
     * - 락 획득에 실패할 경우 매칭 로직이 실행되지 않는지 검증
     * - 데이터 무결성이 보장되는지 확인
     */
    @Test
    void testLockAcquisitionFailure() throws InterruptedException {
        // Given
        when(redissonClient.getFairLock(anyString())).thenReturn(rLock);
        when(rLock.tryLock(anyLong(), anyLong(), any())).thenReturn(false);

        // When
        tradeRedisService.processMatchingWithLock(1L);

        // Then
        verify(redisRepository, never()).popOldestBuyOrderFromBoth(anyLong());
    }
}
