package com.unicore.dao;

import com.unicore.database.DBConnection;
import com.unicore.models.Faculty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public boolean add(Faculty f) {
        String sql = "INSERT INTO faculty (faculty_id, first_name, last_name, email, phone, department_id, designation, salary, join_date, status) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getFacultyId());
            ps.setString(2, f.getFirstName());
            ps.setString(3, f.getLastName());
            ps.setString(4, f.getEmail());
            ps.setString(5, f.getPhone());
            ps.setInt(6, f.getDepartmentId());
            ps.setString(7, f.getDesignation());
            ps.setDouble(8, f.getSalary());
            ps.setDate(9, new java.sql.Date(f.getJoinDate().getTime()));
            ps.setString(10, f.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean update(Faculty f) {
        String sql = "UPDATE faculty SET first_name=?, last_name=?, email=?, phone=?, department_id=?, designation=?, salary=?, status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getFirstName());
            ps.setString(2, f.getLastName());
            ps.setString(3, f.getEmail());
            ps.setString(4, f.getPhone());
            ps.setInt(5, f.getDepartmentId());
            ps.setString(6, f.getDesignation());
            ps.setDouble(7, f.getSalary());
            ps.setString(8, f.getStatus());
            ps.setInt(9, f.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM faculty WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public List<Faculty> getAll() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT f.*, d.name as dept_name FROM faculty f LEFT JOIN departments d ON f.department_id = d.id ORDER BY f.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public List<Faculty> search(String query) {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT f.*, d.name as dept_name FROM faculty f LEFT JOIN departments d ON f.department_id = d.id WHERE f.faculty_id LIKE ? OR f.first_name LIKE ? OR f.last_name LIKE ? OR f.email LIKE ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String q = "%" + query + "%";
            for (int i = 1; i <= 4; i++) ps.setString(i, q);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    public Faculty getById(int id) {
        String sql = "SELECT f.*, d.name as dept_name FROM faculty f LEFT JOIN departments d ON f.department_id = d.id WHERE f.id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private Faculty mapRow(ResultSet rs) throws SQLException {
        Faculty f = new Faculty();
        f.setId(rs.getInt("id"));
        f.setFacultyId(rs.getString("faculty_id"));
        f.setFirstName(rs.getString("first_name"));
        f.setLastName(rs.getString("last_name"));
        f.setEmail(rs.getString("email"));
        f.setPhone(rs.getString("phone"));
        f.setDepartmentId(rs.getInt("department_id"));
        f.setDepartmentName(rs.getString("dept_name"));
        f.setDesignation(rs.getString("designation"));
        f.setSalary(rs.getDouble("salary"));
        f.setJoinDate(rs.getDate("join_date"));
        f.setStatus(rs.getString("status"));
        return f;
    }
}
