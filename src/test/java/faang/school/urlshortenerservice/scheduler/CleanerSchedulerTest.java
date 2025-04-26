package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.mapper.HashMapper;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CleanerSchedulerTest {
    @Mock
    private  UrlRepository urlRepository;
    @Mock
    private  HashRepository hashRepository;
    @Mock
    private  HashMapper hashMapper;
    @InjectMocks
    private CleanerScheduler cleanerScheduler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cleanerScheduler, "monthsBeforeExpiration", 6);
    }

    @Test
    void shouldRemoveExpiredUrlsAndSaveHashes() {
        // given - precondition
        var deletedHashes = List.of("abc123", "def456");

        when(urlRepository.deleteUrlsOlderThan(any())).thenReturn(deletedHashes);
        when(hashMapper.toEntity(anyString()))
                .thenAnswer(invocation -> new Hash(invocation.getArgument(0)));

        // when - action
        cleanerScheduler.removeExpiredUrls();

        // then - verify the output
        verify(urlRepository).deleteUrlsOlderThan(any());
        verify(hashRepository).saveAll(argThat(entities -> ((Collection<?>) entities).size() == 2));
    }
}