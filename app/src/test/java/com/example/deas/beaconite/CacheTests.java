package com.example.deas.beaconite;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * Tutorial: http://www.makeinjava.com/convert-list-objects-to-from-json-using-jackson-objectmapper/
 * <p>
 * What to test: -) Was Cache correctly set up? --) Correct means: after Constructor: ---) name is
 * not null or empty ---) timeIntervals is an empty List ---) fingerprint is empty but not null.
 * <p>
 * -) adding of timestamp pairs
 * <p>
 * -) adding of fingerprint
 * <p>
 * -) calculating a fingerprint
 * <p>
 * -) getters
 * <p>
 * <p>
 * Created by deas on 05/10/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class CacheTests {

	String cacheName = "testCache";
	String emptyString = "";

	Cache cache;

	@Before
	public void setUp() throws Exception {

		PowerMockito.mockStatic(Log.class);
		cache = new Cache(cacheName);

	}

	@Test
	public void createCacheWithCorrectValuesTest() {
		String expectedCacheName = cacheName;
		List<TimeInterval> expectedTimeIntervalsList = new ArrayList<>();
		Fingerprint expectedFingerprint = new FingerprintMedian(null, expectedTimeIntervalsList,
				null);

		Assert.assertNotNull("Cache name was null.", cache.getCacheName());
		Assert.assertFalse("Cache has name was empty.", cache.getCacheName().isEmpty());
		Assert.assertEquals(String.format("Cache name is not %s .", expectedCacheName), expectedCacheName, cache
				.getCacheName());
		Assert.assertEquals(expectedTimeIntervalsList, cache.getTimeIntervals());
		Assert.assertEquals(expectedFingerprint, cache.getFingerprint());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createCacheWithEmptyStringTest() {
		Cache emptyNameCache = new Cache(emptyString);

	}

	@Test(expected = IllegalArgumentException.class)
	public void createCacheWithNullNamestringTest() {
		Cache nullStringCache = new Cache(null);
	}

}