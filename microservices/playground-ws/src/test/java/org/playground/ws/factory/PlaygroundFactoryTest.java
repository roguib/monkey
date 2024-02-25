package org.playground.ws.factory;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.playground.ws.Playground;
import org.playground.ws.dao.TemplateDao;
import org.playground.ws.dto.CreatePlaygroundDto;
import org.playground.ws.repository.TemplateRepository;
import org.playground.ws.services.CacheServiceImpl;
import org.playground.ws.utils.JedisPooledMocked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@HelidonTest
public class PlaygroundFactoryTest extends PlaygroundFactory {

    @Test
    public void testNewPlayground() {
        final JedisPooledMocked jedisPooledMocked = new JedisPooledMocked();
        try (MockedStatic<CacheServiceImpl> cacheServiceMocked = mockStatic(CacheServiceImpl.class)) {
            cacheServiceMocked.when(() -> CacheServiceImpl.getCacheConnection()).thenAnswer((Answer<JedisPooledMocked>) invocation -> jedisPooledMocked);
            final String MOCKED_PLAYGROUND_ID = "abc-dfg-123";
            try (MockedStatic<PlaygroundFactory> playgroundFactoryMocked = mockStatic(PlaygroundFactory.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS))) {
                playgroundFactoryMocked.when(() -> PlaygroundFactory.generatePlaygroundUniqueId()).thenAnswer((Answer<String>) invocation -> MOCKED_PLAYGROUND_ID);

                final Playground playground = PlaygroundFactory.getPlayground(new CreatePlaygroundDto(), null);

                assertEquals(playground.getId(), MOCKED_PLAYGROUND_ID);
                assertEquals(playground.getHistory().size(), 0);
            }
        }
    }

    @Inject
    private TemplateRepository templateRepository;
    @Test
    public void testNewPlaygroundFromTemplate() {
        final JedisPooledMocked jedisPooledMocked = new JedisPooledMocked();
        try (MockedStatic<CacheServiceImpl> cacheServiceMocked = mockStatic(CacheServiceImpl.class)) {
            cacheServiceMocked.when(() -> CacheServiceImpl.getCacheConnection()).thenAnswer((Answer<JedisPooledMocked>) invocation -> jedisPooledMocked);
            final String MOCKED_PLAYGROUND_ID = "abc-dfg-123";
            try (MockedStatic<PlaygroundFactory> playgroundFactoryMocked =
                         mockStatic(PlaygroundFactory.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS))) {
                playgroundFactoryMocked
                        .when(() -> PlaygroundFactory.generatePlaygroundUniqueId()).thenAnswer((Answer<String>) invocation -> MOCKED_PLAYGROUND_ID);

                final String MOCKED_PROGRAM = "let a = 1; a;";
                TemplateRepository templateRepository = mock(TemplateRepository.class);
                final Optional<TemplateDao> mockedFindByResponse = Optional.of(
                        TemplateDao.of("Mocked title", "Mocked description", MOCKED_PROGRAM)
                );
                when(templateRepository.findById(any()))
                        .thenReturn(mockedFindByResponse);
                final Playground playground = PlaygroundFactory
                        .getPlayground(new CreatePlaygroundDto("Mocked Template Id"), templateRepository);

                assertEquals(playground.getId(), MOCKED_PLAYGROUND_ID);
                assertEquals(playground.getProgram(), MOCKED_PROGRAM);
                assertEquals(playground.getHistory().size(), 0);
            }
        }
    }

    @Test
    public void testNewPlaygroundFromTemplateNotFound() {
        final JedisPooledMocked jedisPooledMocked = new JedisPooledMocked();
        try (MockedStatic<CacheServiceImpl> cacheServiceMocked = mockStatic(CacheServiceImpl.class)) {
            cacheServiceMocked.when(() -> CacheServiceImpl.getCacheConnection())
                    .thenAnswer((Answer<JedisPooledMocked>) invocation -> jedisPooledMocked);
            final String MOCKED_PLAYGROUND_ID = "abc-dfg-123";
            try (MockedStatic<PlaygroundFactory> playgroundFactoryMocked =
                         mockStatic(PlaygroundFactory.class, Mockito.withSettings().defaultAnswer(Mockito.CALLS_REAL_METHODS))) {
                playgroundFactoryMocked
                        .when(() -> PlaygroundFactory.generatePlaygroundUniqueId())
                        .thenAnswer((Answer<String>) invocation -> MOCKED_PLAYGROUND_ID);

                TemplateRepository templateRepository = mock(TemplateRepository.class);
                final Optional<TemplateDao> mockedFindByResponse = Optional.empty();
                when(templateRepository.findById(any())).thenReturn(mockedFindByResponse);
                assertThrows(NotFoundException.class, () ->
                        PlaygroundFactory.getPlayground(new CreatePlaygroundDto("Mocked Template Id"), templateRepository)
                );
            }
        }
    }

    @Test
    public void testPlaygroundNotFound() {
        final JedisPooledMocked jedisPooledMocked = new JedisPooledMocked();
        try (MockedStatic<CacheServiceImpl> cacheServiceMocked = mockStatic(CacheServiceImpl.class)) {
            cacheServiceMocked.when(() -> CacheServiceImpl.getCacheConnection()).thenAnswer((Answer<JedisPooledMocked>) invocation -> jedisPooledMocked);
            assertThrows(NotFoundException.class, () -> PlaygroundFactory.getPlayground("abc-123-cdf"));
        }
    }

    @Test
    public void testPlaygroundExists() {
        final String MOCKED_PLAYGROUND_ID = "abc-842-123";
        final JedisPooledMocked jedisPooledMocked = new JedisPooledMocked();
        jedisPooledMocked.setValueForKey(MOCKED_PLAYGROUND_ID, "{ \"playgroundId\": \"abc-842-123\", \"program\": \"let a = 2;\\n a; \\n\", \"history\": [\"1\", \"2\"] }");
        try (MockedStatic<CacheServiceImpl> cacheServiceMocked = mockStatic(CacheServiceImpl.class)) {
            cacheServiceMocked.when(() -> CacheServiceImpl.getCacheConnection()).thenAnswer((Answer<JedisPooledMocked>) invocation -> jedisPooledMocked);

            final Playground playground = PlaygroundFactory.getPlayground(MOCKED_PLAYGROUND_ID);

            assertEquals(playground.getId(), MOCKED_PLAYGROUND_ID);
            assertEquals(playground.getProgram(), "let a = 2;\n a; \n");
            assertEquals(playground.getHistory(), new ArrayList<>(Arrays.asList(new String[]{"1", "2"})));
        }
    }
}
