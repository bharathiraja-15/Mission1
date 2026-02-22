package ca.sheridancollege;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import ca.sheridancollege.beans.Mission;
import ca.sheridancollege.database.DatabaseAccess;

@SpringBootTest
@AutoConfigureMockMvc
public class TestController {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private DatabaseAccess database;  // Changed from MissionRepository to DatabaseAccess

    @BeforeEach
    void setUp() {
        // Clear all existing missions for Johnny English
        // You might need a deleteAll method in DatabaseAccess
        // For now, let's just add a test mission
        
        Mission mission = new Mission();
        mission.setTitle("Rescue the Queen");
        mission.setAgent("Johnny English");
        mission.setGadget1("Exploding Cigar");
        mission.setGadget2("Voice Controlled Rolls Royce");
        
        database.addMission(mission);
    }

    @Test
    void testHomePage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }

    @Test
    void testAddMission() throws Exception {
        mockMvc.perform(get("/addMission"))
            .andExpect(status().isOk())
            .andExpect(view().name("create_mission"))
            .andExpect(model().attributeExists("mission"));
    }

    @Test
    void testCreateMission() throws Exception {
        mockMvc.perform(post("/createMission")
            .param("title", "New Mission")
            .param("agent", "Johnny English")
            .param("gadget1", "New Gadget 1")
            .param("gadget2", "New Gadget 2"))
            .andExpect(status().isOk())  // Your method returns "view_mission" not redirect
            .andExpect(view().name("view_mission"));
    }

    @Test
    void testViewMissions() throws Exception {
        mockMvc.perform(get("/viewMissions").param("agent", "Johnny English"))
            .andExpect(status().isOk())
            .andExpect(view().name("view_mission"));
    }

    @Test
    void testEditMission() throws Exception {
        // Get the mission we created in setUp
        // Since we don't have a findAll method, we need to get by agent
        // You might need to add a method to get the first mission
        Mission mission = database.getMissions("Johnny English").get(0);
        
        mockMvc.perform(get("/editMission/" + mission.getId()))
            .andExpect(status().isOk())
            .andExpect(view().name("edit_mission"))
            .andExpect(model().attributeExists("mission"));
    }

    @Test
    void testUpdateMission() throws Exception {
        // Get the mission we created in setUp
        Mission mission = database.getMissions("Johnny English").get(0);
        
        mockMvc.perform(post("/updateMission")
            .param("id", mission.getId().toString())
            .param("title", "Updated Title")
            .param("agent", "Johnny English")
            .param("gadget1", "Updated Gadget 1")
            .param("gadget2", "Updated Gadget 2"))
            .andExpect(status().isOk())  // Your method returns "view_mission"
            .andExpect(view().name("view_mission"));
    }

    @Test
    void testDeleteMission() throws Exception {
        // First create a mission to delete
        Mission newMission = new Mission();
        newMission.setTitle("Mission to Delete");
        newMission.setAgent("Johnny English");
        newMission.setGadget1("Delete Gadget 1");
        newMission.setGadget2("Delete Gadget 2");
        database.addMission(newMission);
        
        // Get the mission we just created
        Mission missionToDelete = database.getMissions("Johnny English")
            .stream()
            .filter(m -> m.getTitle().equals("Mission to Delete"))
            .findFirst()
            .orElse(null);
        
        if (missionToDelete != null) {
            mockMvc.perform(get("/deleteMission/" + missionToDelete.getId()))
                .andExpect(status().isOk())  // Your method returns "view_mission"
                .andExpect(view().name("view_mission"));
        }
    }
}
