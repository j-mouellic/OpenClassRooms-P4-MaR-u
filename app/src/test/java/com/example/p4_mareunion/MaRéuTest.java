package com.example.p4_mareunion;

import static org.junit.Assert.assertFalse;

import com.example.p4_mareunion.api.FakeApiService;
import com.example.p4_mareunion.model.Reunion;
import com.example.p4_mareunion.repository.ReunionRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class MaRéuTest {

    private ReunionRepository reunionRepository;

    @Before
    public void setup() {
        reunionRepository = ReunionRepository.getInstance(new FakeApiService());
    }

    @Test
    public void deleteReunionWithSuccess() {
        Reunion reunionToDelete = reunionRepository.getReunions().getValue().get(0);
        reunionRepository.deleteReunion(reunionToDelete);
        assertFalse(reunionRepository.getReunions().getValue().contains(reunionToDelete));
    }
}