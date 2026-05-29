package com.unicore.dao;

import com.unicore.database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    public boolean enroll(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?,?,CURDATE())";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean updateGrade(int studentId, int courseId, String grade, double marks) {
        String sql = "UPDATE enrollments SET grade=?, marks=?, status='completed' WHERE student_id=? AND course_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, grade);
            ps.setDouble(2, marks);
            ps.setInt(3, studentId);
            ps.setInt(4, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean drop(int studentId, int courseId) {
        String sql = "UPDATE enrollments SET status='dropped' WHERE student_id=? AND course_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<String[]> getStudentEnrollments(int studentId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_name, c.credits, e.grade, e.marks, e.status FROM enrollments e JOIN courses c ON e.course_id=c.id WHERE e.student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    String.valueOf(rs.getInt("credits")),
                    rs.getString("grade") != null ? rs.getString("grade") : "-",
                    rs.getString("marks") != null ? rs.getString("marks") : "-",
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<String[]> getCourseEnrollments(int courseId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.first_name, s.last_name, e.grade, e.marks, e.status FROM enrollments e JOIN students s ON e.student_id=s.id WHERE e.course_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("student_id"),
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("grade") != null ? rs.getString("grade") : "-",
                    rs.getString("marks") != null ? rs.getString("marks") : "-",
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public int countEnrolled(int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id=? AND status='enrolled'";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }
}
