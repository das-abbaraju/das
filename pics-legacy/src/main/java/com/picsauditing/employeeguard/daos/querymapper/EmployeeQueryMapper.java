package com.picsauditing.employeeguard.daos.querymapper;

import com.picsauditing.employeeguard.entities.Employee;
import com.picsauditing.search.QueryMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class EmployeeQueryMapper implements QueryMapper<Employee> {
	@Override
	public void mapObjectToPreparedStatement(Employee employee, PreparedStatement preparedStatement) throws SQLException {
		if (employee == null || preparedStatement == null) {
			throw new IllegalArgumentException("Employee and PreparedStatement cannot be null.");
		}

		preparedStatement.setInt(1, employee.getAccountId());
		preparedStatement.setString(2, employee.getSlug());
		preparedStatement.setString(3, employee.getFirstName());
		preparedStatement.setString(4, employee.getLastName());
		preparedStatement.setString(5, employee.getPositionName());
		preparedStatement.setString(6, employee.getEmail());
		preparedStatement.setString(7, employee.getPhone());
		preparedStatement.setString(8, employee.getEmailToken());

		preparedStatement.setInt(9, employee.getCreatedBy());
		preparedStatement.setInt(10, employee.getUpdatedBy());
		preparedStatement.setInt(11, employee.getDeletedBy());

		setDate(preparedStatement, 12, employee.getCreatedDate());
		setDate(preparedStatement, 13, employee.getUpdatedDate());
		setDate(preparedStatement, 14, employee.getDeletedDate());

		preparedStatement.setString(15, employee.getSlug());
		preparedStatement.setString(16, employee.getFirstName());
		preparedStatement.setString(17, employee.getLastName());
		preparedStatement.setString(18, employee.getPositionName());
		preparedStatement.setString(19, employee.getPhone());

		preparedStatement.setInt(20, employee.getCreatedBy());
		setDate(preparedStatement, 21, employee.getCreatedDate());
	}

	private void setDate(PreparedStatement preparedStatement, int position, java.util.Date date) throws SQLException {
		if (date == null) {
			preparedStatement.setNull(position, Types.INTEGER);
		} else {
			preparedStatement.setDate(position, new Date(date.getTime()));
		}
	}

}
