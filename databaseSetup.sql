DROP DOMAIN employeeLogin CASCADE;
DROP DOMAIN employeeName CASCADE;

DROP TABLE employees CASCADE;
DROP TABLE BossManager CASCADE;
DROP TABLE employeeShifts CASCADE;

CREATE DOMAIN employeeLogin as varchar(25);
CREATE DOMAIN employeeName as varchar(30);


CREATE TABLE employees (
	empFirstName employeeName NOT NULL,
	empLastName employeeName NOT NULL,
	empAccessLevel smallint NOT NULL, -- will make a check but something like 1 is normal employees, 2 is managers, 3 is owner/admin
	empLogin employeeLogin NOT NULL, -- depends on how you want to do login, either by empNum or a username
	empPassword bytea NOT NULL,  -- I think this will be an hash or some sort, IM FILLING WITH PLAIN TEXT to start
	empEmail varchar(60) NOT NULL,
	empWage decimal NOT NULL,
	PRIMARY KEY (empLogin)
);

--this table links two employees together by who is that persons boss, or their manager. it is assumed that workers will have a boss whos empAccessLevel is >1 and managers will have a boss that is empAccessLev\del is >2
CREATE TABLE BossManager (
	employee employeeLogin NOT NULL,
	manager employeeLogin NOT NULL,
	PRIMARY KEY (employee, manager),
	FOREIGN KEY (employee) REFERENCES employees (empLogin),
	FOREIGN KEY (manager) REFERENCES employees (empLogin)
);

CREATE TABLE employeeShifts (
	shiftEmployeeLogin employeeLogin NOT NULL,
	shiftStartTime timestamp NOT NULL,
	shiftEndTime timestamp NOT NULL,
	FOREIGN KEY (shiftEmployeeLogin) REFERENCES employees (empLogin),
	PRIMARY KEY (shiftEmployeeLogin, shiftStartTime, shiftEndTime)
);


--INSERT Some demo values
/*--Commenting these Inserts out because we have better sample stuff now
INSERT 
	INTO employees (
		empFirstName,
		empLastName,
		empAccessLevel,
		empLogin,
		empPassword,
		empEmail,
		empWage)
	VALUES (
		'Andrew',
		'Magnus',
		1,
		'magnusandy',
		'[B@677327b6',
		'testemail@test.com',
		10)
;

INSERT 
	INTO employees (
		empFirstName,
		empLastName,
		empAccessLevel,
		empLogin,
		empPassword,
		empEmail,
		empWage)
	VALUES (
		'Rick',
		'James',
		2,
		'rickjames',
		'[B@14ae5a5',
		'rickjames@email.com',
		10)
;

INSERT 
	INTO employees (
		empFirstName,
		empLastName,
		empAccessLevel,
		empLogin,
		empPassword,
		empEmail,
		empWage)
	VALUES (
		'Nic',
		'Cage',
		3,
		'oneTrueGod',
		'[B@7f31245a',
		'nicCage@email.com',
		10)
;

INSERT 
	INTO employeeShifts (
			shiftEmployeeLogin,
			shiftStartTime,
			shiftEndTime)
	VALUES (
		'magnusandy',
		'2014-10-13 10:15',
		'2014-10-13 18:30')
;

INSERT 
	INTO employeeShifts (
			shiftEmployeeLogin,
			shiftStartTime,
			shiftEndTime)
	VALUES (
		'rickjames',
		'2014-10-13 10:15',
		'2014-10-13 18:30')
;

INSERT 
	INTO employeeShifts (
			shiftEmployeeLogin,
			shiftStartTime,
			shiftEndTime)
	VALUES (
		'magnusandy',
		'2014-10-14 10:15',
		'2014-10-14 18:30')
;

INSERT 
	INTO employeeShifts (
			shiftEmployeeLogin,
			shiftStartTime,
			shiftEndTime)
	VALUES (
		'rickjames',
		'2014-10-13 19:15',
		'2014-10-13 21:30')
;

INSERT 
	INTO employeeShifts (
			shiftEmployeeLogin,
			shiftStartTime,
			shiftEndTime)
	VALUES (
		'magnusandy',
		'2014-10-20 11:15',
		'2014-10-13 19:30')
;
*/
-- VIEWS
CREATE VIEW vw_sample_all_employee_week
	AS SELECT
		CONCAT (empLastName, ', ',empFirstName) AS "Employee",
		shiftStartTime AS "Start Time",
		shiftEndTime AS "End Time",
		shiftEndTime - shiftStartTime AS "Duration"
	FROM
		employees,
		employeeShifts
	WHERE
		empLogin = shiftEmployeeLogin AND
		shiftStartTime >= '2014-10-12 00:00' AND
		shiftStartTime < '2014-10-20 00:00'
	ORDER BY
		shiftStartTime,
		empLastName
;


CREATE VIEW login
	AS SELECT
		empLogin,
		empPassword
	FROM
		employees
;

CREATE VIEW full_employee_info 
	AS SELECT
		empFirstName,
		empLastName,
		empAccessLevel,
		empLogin,
		empPassword,
		empEmail,
		manager AS empManager
	FROM
		employees,
		BossManager
	WHERE
		empLogin = employee
;




