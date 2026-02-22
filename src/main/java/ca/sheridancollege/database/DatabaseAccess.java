package ca.sheridancollege.database;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.beans.Mission;

/**
 * Database access class
 */
@Repository
public class DatabaseAccess {

	private NamedParameterJdbcTemplate jdbc;

	public DatabaseAccess(NamedParameterJdbcTemplate jdbc) {
		this.jdbc = jdbc;
	}

	/* =====================================================
	   ✅ INITIAL DATA FOR JENKINS TEST ENVIRONMENT
	   ===================================================== */
	@PostConstruct
	public void init() {

		MapSqlParameterSource params =
				new MapSqlParameterSource();

		String countQuery =
				"SELECT COUNT(*) FROM missions";

		Integer count =
				jdbc.queryForObject(
						countQuery,
						params,
						Integer.class);

		// insert default data only if table empty
		if (count == 0) {

			String insert =
				"INSERT INTO missions (agent, title, gadget1, gadget2) "
				+ "VALUES (:agent, :title, :gadget1, :gadget2)";

			jdbc.update(insert,
					new MapSqlParameterSource()
					.addValue("agent", "Johnny English")
					.addValue("title", "Rescue the Queen")
					.addValue("gadget1", "Exploding Cigar")
					.addValue("gadget2", "Rolls Royce"));

			jdbc.update(insert,
					new MapSqlParameterSource()
					.addValue("agent", "Natasha Romanova")
					.addValue("title", "Secret Mission")
					.addValue("gadget1", "Widow Bite")
					.addValue("gadget2", "Tracker"));
		}
	}

	/* ===================================================== */

	public int addMission(Mission mission) {

		MapSqlParameterSource namedParameters =
				new MapSqlParameterSource();

		String query =
			"INSERT INTO missions (agent, title, gadget1, gadget2) "
			+ "VALUES (:agent, :title, :gadget1, :gadget2)";

		namedParameters
			.addValue("agent", mission.getAgent())
			.addValue("title", mission.getTitle())
			.addValue("gadget1", mission.getGadget1())
			.addValue("gadget2", mission.getGadget2());

		return jdbc.update(query, namedParameters);
	}

	public Mission getMission(Long id) {

		MapSqlParameterSource namedParameters =
				new MapSqlParameterSource();

		String query =
				"SELECT * FROM missions WHERE id = :id";

		namedParameters.addValue("id", id);

		BeanPropertyRowMapper<Mission> mapper =
				new BeanPropertyRowMapper<>(Mission.class);

		List<Mission> missions =
				jdbc.query(query, namedParameters, mapper);

		if (missions.isEmpty()) {
			return null;
		}

		return missions.get(0);
	}

	public int updateMission(Mission mission) {

		MapSqlParameterSource namedParameters =
				new MapSqlParameterSource();

		String query =
			"UPDATE missions SET agent=:agent, title=:title, "
			+ "gadget1=:gadget1, gadget2=:gadget2 "
			+ "WHERE id=:id";

		namedParameters
			.addValue("agent", mission.getAgent())
			.addValue("title", mission.getTitle())
			.addValue("gadget1", mission.getGadget1())
			.addValue("gadget2", mission.getGadget2())
			.addValue("id", mission.getId());

		return jdbc.update(query, namedParameters);
	}

	public int deleteMission(Long id) {

		MapSqlParameterSource namedParameters =
				new MapSqlParameterSource();

		String query =
				"DELETE FROM missions WHERE id=:id";

		namedParameters.addValue("id", id);

		return jdbc.update(query, namedParameters);
	}

	public List<Mission> getMissions(String agent) {

		MapSqlParameterSource namedParameters =
				new MapSqlParameterSource();

		String query =
				"SELECT * FROM missions WHERE agent=:agent";

		namedParameters.addValue("agent", agent);

		BeanPropertyRowMapper<Mission> mapper =
				new BeanPropertyRowMapper<>(Mission.class);

		return jdbc.query(query, namedParameters, mapper);
	}
}
