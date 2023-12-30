package org.playground.ws.factory;

import io.helidon.microprofile.testing.junit5.HelidonTest;
import jakarta.ws.rs.NotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.playground.ws.Playground;
import org.playground.ws.services.CacheServiceImpl;
import org.playground.ws.utils.JedisPooledMocked;

import java.util.ArrayList;
import java.util.Arrays;

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

                final Playground playground = PlaygroundFactory.getPlayground();

                assertEquals(playground.getId(), MOCKED_PLAYGROUND_ID);
                assertEquals(playground.getHistory().size(), 0);
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
