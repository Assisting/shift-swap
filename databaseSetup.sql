BEGIN;

-- needs a few domains i missed, and a run incase i mispelled
-- also needs the defaults (forgot how to do)
/*
DROP DOMAIN employeeLogin CASCADE;
DROP DOMAIN employeeName CASCADE;

DROP TABLE employees CASCADE;
DROP TABLE BossManager CASCADE;
DROP TABLE employeeShifts CASCADE;
*/
CREATE DOMAIN employeeLogin as varchar(25);
CREATE DOMAIN employeeName as varchar(30);
CREATE DOMAIN trantype char(4) CHECK (VALUE IN ('swap','take','give'));


CREATE TABLE employees (
	empFirstName employeeName NOT NULL,
	empLastName employeeName NOT NULL,
	empAccessLevel smallint NOT NULL,
	empLogin employeeLogin NOT NULL, 
	empPassword bytea NOT NULL, 
	empEmail varchar(60) NOT NULL,
	empWage numeric NOT NULL,
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

CREATE TABLE employeeinbox (
	mssgreciever employeelogin NOT NULL,
	mssgsender employeelogin NOT NULL,
	mssgsendtime timestamp NOT NULL DEFAULT current_timestamp, 
	mssgtext text NOT NULL,
	mssgisread boolean DEFAULT FALSE, 
	PRIMARY KEY (mssgreciever, mssgsender, mssgsendtime)
);

CREATE TABLE giveshifts (
	giverlogin employeelogin NOT NULL,
	givershiftstart timestamp NOT NULL,
	givershiftend timestamp NOT NULL,
	PRIMARY KEY (giverlogin, givershiftstart, givershiftend),
	FOREIGN KEY (giverlogin, givershiftstart, givershiftend) REFERENCES employeeshifts(shiftemployeelogin, shiftstarttime, shiftendtime)
);

CREATE TABLE managerapproval (
	ma_approval boolean not null,
	ma_manager employeelogin not null,
	PRIMARY KEY (ma_approval, ma_manager),
	FOREIGN KEY (ma_manager) references employees (emplogin)
);

CREATE TABLE shifttransaction (
	transactionid serial NOT NULL,
	initlogin employeelogin not null,
	initshiftstart timestamp,
	initshiftend timestamp,
	finallogin employeelogin not null,
	finalshiftstart timestamp not null,
	finalshiftend timestamp not null,
	transactiontype trantype not null,
	finalsign boolean DEFAULT FALSE, 
	finalmanagerlogin employeelogin not null,
	initmanagerlogin employeelogin not null,
	initmanagersign boolean DEFAULT FALSE, 
	finaltmanagersign boolean DEFAULT FALSE, 
	PRIMARY KEY (transactionid)
);



ROLLBACK;