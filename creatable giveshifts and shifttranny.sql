BEGIN;

-- PK : emplogin and shiftstart and shiftend

CREATE DOMAIN signoff AS boolean;
CREATE DOMAIN trantype AS char(4) CHECK (VALUE IN('give', 'take', 'swap')); 



CREATE TABLE giveshifts (
	giverlogin employeelogin NOT NULL,
	givershiftstart timestamp NOT NULL,
	givershiftend  timestamp NOT NULL,
	
	FOREIGN KEY (giverlogin, givershiftstart, givershiftend) REFERENCES employeeshifts (shiftemployeelogin, shiftstarttime, shiftendtime),
	PRIMARY KEY (giverlogin, givershiftstart, givershiftend)
	);




CREATE TABLE shifttransaction (
    Initlogin employeelogin NOT NULL,
	Initshiftstart timestamp NOT NULL,
	Initshiftend  timestamp NOT NULL,
	Finallogin employeelogin NOT NULL,
	finalshiftstart timestamp,
	finalshiftend timestamp,
	transactiontype trantype NOT NULL,
	finalsign signoff DEFAULT FALSE,
	managersign signoff DEFAULT FALSE
--added primary and foreign key manually because we derped the first time.
	);

	


--ROLLBACK;