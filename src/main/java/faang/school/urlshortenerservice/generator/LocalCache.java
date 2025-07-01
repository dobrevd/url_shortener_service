package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class LocalCache {
    private final Queue<String> hashes;
    private final AtomicBoolean isRefilling;
    private final HashGenerator hashGenerator;
    private final Executor refillExecutor;
    @Value("${app.local_hash.capacity:9000}")
    private int capacity;
    @Value("${app.local_hash.min_value:1000}")
    private int minValue;
    @Value("${app.async.local_hash_refill.pool_size:10}")
    private int threadsNumber;

    public LocalCache(HashGenerator hashGenerator, @Qualifier("refillExecutor") Executor refillExecutor) {
        this.hashGenerator = hashGenerator;
        this.refillExecutor = refillExecutor;
        this.hashes = new ConcurrentLinkedQueue<>();
        this.isRefilling = new AtomicBoolean(false);
    }

    @PostConstruct
    public void init() {
        try {
            hashGenerator.generateAndSaveHashes();
            hashes.addAll(hashGenerator.getHashes(capacity));
            log.info("LocalCache initialized with {} hashes", hashes.size());
        } catch (Exception e) {
            log.error("Error initializing LocalCache: ", e);
            throw e;
        }
    }

    public String getHash() {
        if (hashes.size() <= minValue) {
            refillAsync();
        }
        var hash = hashes.poll();
        log.info("Get new hash: {} from local cache storage", hash);
        return hash;
    }

    public void refillAsync() {
        if (isRefilling.compareAndSet(false, true)) {
            CompletableFuture
                    .runAsync(hashGenerator::generateAndSaveHashes, refillExecutor)
                    .thenCompose(v -> hashGenerator.getHashesAsync(capacity))
                    .thenAccept(newHashes -> {
                        hashes.addAll(newHashes);
                        log.info("Successfully refilled LocalCache with {} hashes", newHashes.size());
                    })
                    .exceptionally(ex -> {
                        log.error("Error refilling LocalCache: ", ex);
                        return null;
                    })
                    .whenComplete((result, ex) -> isRefilling.set(false));
        }
    }
}