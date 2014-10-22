package edu.vuum.mocca;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

/**
 * @class SimpleSemaphoreUnitTest
 *
 * @brief Simple unit test for the SimpleSemaphore that just tests
 *        single-threaded logic.
 */
public class SimpleSemaphoreUnitTest {
    @Test
    public void testSimpleSemaphore() {
        SimpleSemaphore simpleSemaphore = new SimpleSemaphore(2, true);
        assertNotNull(simpleSemaphore);
    }

    @Test
    public void testAcquire() throws InterruptedException {
        SimpleSemaphore simpleSemaphore = new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 0);
    }

    @Test
    public void testAcquireUninterruptibly() throws InterruptedException {
        SimpleSemaphore simpleSemaphore = new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquireUninterruptibly();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquireUninterruptibly();
        assertEquals(simpleSemaphore.availablePermits(), 0);
    }

    @Test
    public void testRelease() throws InterruptedException {
        SimpleSemaphore simpleSemaphore = new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 0);
        simpleSemaphore.release();
        assertEquals(simpleSemaphore.availablePermits(), 1);
        simpleSemaphore.release();
        assertEquals(simpleSemaphore.availablePermits(), 2);
    }
	
    @Test
    public void testAvailablePermits() throws InterruptedException{
        SimpleSemaphore simpleSemaphore = new SimpleSemaphore(2, true);
        assertEquals(simpleSemaphore.availablePermits(), 2);
        simpleSemaphore.acquire();
        assertEquals(simpleSemaphore.availablePermits(), 1);
    }
}