package team9.ddang.walk.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import team9.ddang.walk.entity.Location;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LocationBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Location> locations) {
        String sql = "INSERT INTO location (latitude, longitude, time_stamp, walk_id) " +
                "VALUES (?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Location location = locations.get(i);
                        ps.setDouble(1, location.getPosition().getLatitude());
                        ps.setDouble(2, location.getPosition().getLongitude());
                        ps.setObject(3, location.getPosition().getTimeStamp());
                        ps.setLong(4, location.getWalk().getWalkId());
                    }

                    @Override
                    public int getBatchSize() {
                        return locations.size();
                    }
                });
    }

}
