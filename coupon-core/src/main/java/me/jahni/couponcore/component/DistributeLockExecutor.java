package me.jahni.couponcore.component;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class DistributeLockExecutor {

    private final RedissonClient redissonClient;
    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    public void execute(String lockName, long waitMillis, long releaseMillis, Runnable logic) {
        RLock lock = redissonClient.getLock(lockName);

        if (tryAcquireLock(lock, lockName, waitMillis, releaseMillis)) {
            try {
                logic.run();
            } finally {
                releaseLock(lock);
            }
        }
    }

    public <T> T executeWithResult(String lockName, long waitMillis, long releaseMillis, Supplier<T> logic) {
        RLock lock = redissonClient.getLock(lockName);

        if (tryAcquireLock(lock, lockName, waitMillis, releaseMillis)) {
            try {
                return logic.get();
            } finally {
                releaseLock(lock);
            }
        }
        throw new IllegalStateException("[" + lockName + "] Lock을 획득하지 못했습니다.");
    }

    private boolean tryAcquireLock(RLock lock, String lockName, long waitMillis, long releaseMillis) {
        try {
            return lock.tryLock(waitMillis, releaseMillis, TIME_UNIT);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("[" + lockName + "] Lock 획득 시도 중 인터럽트 발생", e);
        }
    }

    private void releaseLock(RLock lock) {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
