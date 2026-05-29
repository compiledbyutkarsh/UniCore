package com.unicore.dao;

import com.unicore.database.DBConnection;
import com.unicore.models.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public boolean add(Course c) {
        String sql = "INSERT INTO courses (course_code, course_name, credits, department_id, faculty_id, semester, max_students) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setInt(3, c.getCredits());
            ps.setInt(4, c.getDepartmentId());
            ps.setInt(5, c.getFacultyId());
            ps.setInt(6, c.getSemester());
            ps.setInt(7, c.getMaxStudents());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean update(Course c) {
        String sql = "UPDATE courses SET course_code=?, course_name=?, credits=?, department_id=?, faculty_id=?, semester=?, max_students=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setInt(3, c.getCredits());
            ps.setInt(4, c.getDepartmentId());
            ps.setInt(5, c.getFacultyId());
            ps.setInt(6, c.getSemester());
            ps.setInt(7, c.getMaxStudents());
            ps.setInt(8, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM courses WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<Course> getAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, d.name as dept_name, CONCAT(f.first_name,' ',f.last_name) as faculty_name FROM courses c LEFT JOIN departments d ON c.department_id=d.id LEFT JOIN faculty f ON c.faculty_id=f.id ORDER BY c.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<Course> getByDepartment(int deptId) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.*, d.name as dept_name, CONCAT(f.first_name,' ',f.last_name) as faculty_name FROM courses c LEFT JOIN departments d ON c.department_id=d.id LEFT JOIN faculty f ON c.faculty_id=f.id WHERE c.department_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, deptId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setCredits(rs.getInt("credits"));
        c.setDepartmentId(rs.getInt("department_id"));
        c.setDepartmentName(rs.getString("dept_name"));
        c.setFacultyId(rs.getInt("faculty_id"));
        c.setFacultyName(rs.getString("faculty_name"));
        c.setSemester(rs.getInt("semester"));
        c.setMaxStudents(rs.getInt("max_students"));
        return c;
    }
}
