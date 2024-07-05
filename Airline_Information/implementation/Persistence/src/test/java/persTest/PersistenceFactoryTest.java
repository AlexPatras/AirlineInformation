package persTest;

import org.junit.jupiter.api.Test;
import persistence.PersistenceFactory;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PersistenceFactoryTest {
    private PersistenceFactory persistenceFactory;

    @Test
    public void PersistenceFactoryTest() {
        assertThat(PersistenceFactory.getImplementation()).isNotNull();
    }
}
