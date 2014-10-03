CREATE DOMAIN employeeNumber as integer;

CREATE TABLE employees (
	empNum employeeNumber NOT NULL, --employees unique identifier number
	empFirstName varchar(30) NOT NULL,
	empLastName varchar(30) NOT NULL,
	empAccessLevel smallint NOT NULL, -- will make a check but something like 1 is normal employees, 2 is managers, 3 is owner/admin
	empUserName varchar(30) NOT NULL, -- depends on how you want to do login, either by empNum or a username
	empPassword varchar(30) NOT NULL,  -- I think this will be an hash or some sort, IM FILLING WITH PLAIN TEXT to start
	PRIMARY KEY (empNum)
);

--this table links two employees together by who is that persons boss, or their manager. it is assumed that workers will have a boss whos empAccessLevel is >1 and managers will have a boss that is empAccessLev\del is >2
CREATE TABLE BossManager (
	employee employeeNumber NOT NULL,
	manager employeeNumber NOT NULL,
	PRIMARY KEY (employee, manager),
	FOREIGN KEY (employee) REFERENCES employees (empNum),
	FOREIGN KEY (manager) REFERENCES employees (empNum)
);


--INSERT Some demo values
INSERT 
	INTO employees (
		empNum,
		empFirstName,
		empLastName,
		empAccessLevel,
		empUserName,
		empPassword)
	VALUES (
		12345,
		'Andrew',
		'Magnus',
		1,
		'magnusandy',
		'doge')
;

INSERT 
	INTO employees (
		empNum,
		empFirstName,
		empLastName,
		empAccessLevel,
		empUserName,
		empPassword)
	VALUES (
		23456,
		'Rick',
		'James',
		2,
		'IamRickJames',
		'RICKJAMES')
;

INSERT 
	INTO employees (
		empNum,
		empFirstName,
		empLastName,
		empAccessLevel,
		empUserName,
		empPassword)
	VALUES (
		34567,
		'Nic',
		'Cage',
		3,
		'oneTrueGod',
		'allHail')
;


CREATE VIEW login
	AS SELECT
		empNum,
		empPassword
	FROM
		employees
	;

CREATE VIEW full_employee_info 
	AS SELECT
		empNum,
		empFirstName,
		empLastName,
		empAccessLevel,
		empUserName,
		empPassword,
		manager AS empManager
	FROM
		employees,
		BossManager
	WHERE
		empNum = employee
	;
