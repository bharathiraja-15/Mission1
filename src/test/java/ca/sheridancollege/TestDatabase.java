package ca.sheridancollege;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.sheridancollege.beans.Mission;
import ca.sheridancollege.database.DatabaseAccess;

@SpringBootTest
public class TestDatabase {

    @Autowired
    private DatabaseAccess database;  // Changed from MissionRepository to DatabaseAccess

    @BeforeEach
    void setUp() {
        // Clear data for Johnny English (if you have a delete method)
        // For now, let's add a test mission
        
        Mission mission = new Mission();
        mission.setTitle("Test Mission");
        mission.setAgent("Johnny English");
        mission.setGadget1("Test Gadget 1");
        mission.setGadget2("Test Gadget 2");
        
        database.addMission(mission);
    }

    @Test
    void testDatabaseSaveMission() {
        // Create a new mission
        Mission newMission = new Mission();
        newMission.setTitle("New Mission");
        newMission.setAgent("Austin Powers");
        newMission.setGadget1("New Gadget 1");
        newMission.setGadget2("New Gadget 2");
        
        // Save it
        int result = database.addMission(newMission);
        
        // Verify it was saved (addMission returns number of rows affected)
        assertEquals(1, result);
        
        // Verify we can retrieve it
        List<Mission> missions = database.getMissions("Austin Powers");
        assertFalse(missions.isEmpty());
        assertEquals("New Mission", missions.get(0).getTitle());
    }

    @Test
    void testDatabaseGetMissions() {
        List<Mission> missions = database.getMissions("Johnny English");
        assertFalse(missions.isEmpty());
        assertTrue(missions.size() >= 1);
    }

    @Test
    void testDatabaseGetMission() {
        // Get the mission from setUp
        Mission mission = database.getMissions("Johnny English").get(0);
        
        // Test getMission by id
        Mission found = database.getMission(mission.getId());
        
        assertNotNull(found);
        assertEquals(mission.getTitle(), found.getTitle());
    }

    @Test
    void testDatabaseUpdateMission() {
        // Get the mission from setUp
        Mission mission = database.getMissions("Johnny English").get(0);
        
        // Update it
        mission.setGadget1("Updated Gadget");
        int result = database.updateMission(mission);
        
        // Verify update was successful
        assertEquals(1, result);
        
        // Verify the update persisted
        Mission updated = database.getMission(mission.getId());
        assertEquals("Updated Gadget", updated.getGadget1());
    }

    @Test
    void testDatabaseDeleteMission() {
        // Create a mission specifically for deletion
        Mission missionToDelete = new Mission();
        missionToDelete.setTitle("Delete Me");
        missionToDelete.setAgent("Johnny English");
        missionToDelete.setGadget1("Delete Gadget 1");
        missionToDelete.setGadget2("Delete Gadget 2");
        database.addMission(missionToDelete);
        
        // Get the mission we just created
        List<Mission> missions = database.getMissions("Johnny English");
        Mission toDelete = missions.stream()
            .filter(m -> m.getTitle().equals("Delete Me"))
            .findFirst()
            .orElse(null);
        
        assertNotNull(toDelete);
        
        // Delete it
        int result = database.deleteMission(toDelete.getId());
        
        // Verify deletion was successful
        assertEquals(1, result);
        
        // Verify it's gone
        Mission deleted = database.getMission(toDelete.getId());
        assertNull(deleted);  // Assuming getMission returns null if not found
    }
}
