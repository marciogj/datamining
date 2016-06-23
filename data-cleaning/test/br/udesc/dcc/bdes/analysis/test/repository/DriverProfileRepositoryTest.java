package br.udesc.dcc.bdes.analysis.test.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.jongo.MongoCollection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.udesc.dcc.bdes.model.DeviceId;
import br.udesc.dcc.bdes.model.DriverId;
import br.udesc.dcc.bdes.model.DriverProfile;
import br.udesc.dcc.bdes.repository.mongo.DriverProfileRepository;
import br.udesc.dcc.bdes.repository.mongo.MongoDBStatic;


////TODO: Replace with a mock instead of rely on mongodb
public class DriverProfileRepositoryTest {
	DriverProfileRepository repository = DriverProfileRepository.get();
	
	@Before
	public void setUp() throws Exception {
		MongoCollection collection = MongoDBStatic.getCollection(DriverProfileRepository.COLLECTION);
		collection.drop();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void itShouldInsertANewDataAndSaveChanges() {
		DriverProfile profile = new DriverProfile(new DriverId("123"), new DeviceId("abc"));
		profile.setAlerts(10);
		repository.save(profile);
		assertEquals(1, repository.count());
		
		Optional<DriverProfile> optProfile = repository.load(profile.getDriverId());
		assertTrue(optProfile.isPresent());
		assertEquals(profile.getAlerts(), optProfile.get().getAlerts());
		
		DriverProfile profileChanged = new DriverProfile(new DriverId("123"), new DeviceId("abc"));
		profileChanged.setAlerts(25);
		repository.save(profileChanged);
		assertEquals(1, repository.count());
		
		optProfile = repository.load(profile.getDriverId());
		assertEquals(profileChanged.getAlerts(), optProfile.get().getAlerts());
		
	}
	
}
