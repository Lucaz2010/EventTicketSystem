package easv.dk.eventticketsystem.dal.db;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import easv.dk.eventticketsystem.be.Event;
import easv.dk.eventticketsystem.dal.IEventDAO;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.List;

public class EventDAODB implements IEventDAO {
    private DBConnection con = new DBConnection();

    @Override
    public List<Event> getAllEvents() throws IOException {
        List<Event> allEvents = new ArrayList<>();
        String sql = "SELECT * FROM event";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("event_name");
                // Use getTimestamp to retrieve both date and time
                Timestamp startTimestamp = rs.getTimestamp("start_datetime");
                Timestamp endTimestamp = rs.getTimestamp("end_datetime");
                LocalDateTime startDatetime = startTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                LocalDateTime endDatetime = endTimestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                String eventLocation = rs.getString("location");
                String eventNotes = rs.getString("notes");
                String eventImagePath = rs.getString("event_image_path");
                String assignedUser = rs.getString("assigned_user");

                Event event = new Event(eventId, eventName, startDatetime, endDatetime, eventLocation, eventNotes, eventImagePath, assignedUser);
                allEvents.add(event);
            }
        } catch (SQLServerException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allEvents;
    }

    @Override
    public void createNewEvent(Event event) throws IOException {
        String sql = "INSERT INTO Event (event_name, start_datetime, end_datetime, location, notes, event_image_path, assigned_user) VALUES (?, ?, ?, ?, ?, ?,?)";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setTimestamp(2, Timestamp.valueOf(event.getStartDatetime()));
            ps.setTimestamp(3, Timestamp.valueOf(event.getEndDatetime()));
            ps.setString(4, event.getLocation());
            ps.setString(5, event.getNotes());
            ps.setString(6, event.getEventImagePath());
            ps.setString(7, event.getAssignedUser());
            ps.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Error adding event to the database: " + e.getMessage(), e);


        }
    }

    @Override
    public void deleteEvent(Event event) throws IOException {
        String sql = "DELETE FROM event where event_id = ?";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
                 ps.setInt(1, event.getEventId());
                 ps.executeUpdate();

                }
                catch (SQLException e) {
            throw new IOException("Error deleting event and its dependencies: " + e.getMessage(), e);

        }

    }

    @Override
    public void updateEvent(Event event) throws IOException {
        String sql = "UPDATE Event SET event_name = ?, start_datetime = ?, end_datetime = ?, location = ?, notes = ?, event_image_path = ?, assigned_user = ? WHERE event_id = ? ";
        try (Connection connection = con.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, event.getEventName());
            ps.setTimestamp(2, Timestamp.valueOf(event.getStartDatetime()));
            ps.setTimestamp(3, Timestamp.valueOf(event.getEndDatetime()));
            ps.setString(4, event.getLocation());
            ps.setString(5, event.getNotes());
            ps.setString(6, event.getEventImagePath());
            ps.setString(7, event.getAssignedUser());
            ps.setInt(8, event.getEventId());
//            ps.executeUpdate();

            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Record updated successfully!");
            }
        } catch (SQLException e) {
            throw new IOException("Error updating event in the database: " + e.getMessage(), e);
        }
    }
}

