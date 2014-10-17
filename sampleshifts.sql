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
		'Damon',
		'L',
		1,
		'tdamon',
		'hashthis',
		'damon@telus.com',
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
		'Tori',
		'M',
		1,
		'ttori',
		'hashthis',
		'tori@telus.com',
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
		'Dorian',
		'R',
		1,
		'tdorian',
		'hashthis',
		'dorian@telus.com',
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
		'Mike',
		'S',
		2,
		'tmike',
		'cantscheduleforshit',
		'mike@telus.com',
		999999)
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
		'Ken',
		'Slawinski',
		1,
		't884027',
		'thisismyrealpasswordimnotkidding#imkidding',
		'ken.slawinski@telus.com',
		1)
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
		'Sanjay',
		'S',
		1,
		'tsanjay',
		'learningisgreat',
		'sanjay@telus.com',
		25)
;

INSERT 
	INTO employeeShifts (
			shiftEmployeeLogin,
			shiftStartTime,
			shiftEndTime)
	VALUES
		('tdamon', '2014-10-19 10:45', '2014-10-19 17:15'),
		('tdamon', '2014-10-20 08:45', '2014-10-20 18:30'),
		('tdamon', '2014-10-21 09:00', '2014-10-21 18:30'),
		('tdamon', '2014-10-22 09:00', '2014-10-22 17:00'),
		('tdamon', '2014-10-23 13:00', '2014-10-23 21:30'),
		('tdamon', '2014-10-24 09:00', '2014-10-24 17:00'),
		('ttori', '2014-10-19 10:45', '2014-10-19 17:15'),
		('ttori', '2014-10-22 15:00', '2014-10-22 21:30'),
		('ttori', '2014-10-23 14:30', '2014-10-23 21:30'),
		('t884027', '2014-10-21 09:00', '2014-10-21 13:00'),
		('t884027', '2014-10-24 15:00', '2014-10-24 21:30'),
		('t884027', '2014-10-25 09:00', '2014-10-25 18:30'),
		('tmike', '2014-10-20 08:45', '2014-10-20 18:30'),
		('tmike', '2014-10-21 09:00', '2014-10-21 18:30'),
		('tmike', '2014-10-22 09:00', '2014-10-22 17:00'),
		('tmike', '2014-10-23 09:00', '2014-10-23 17:00'),
		('tmike', '2014-10-24 09:00', '2014-10-24 17:00'),
		('tmike', '2014-10-25 12:00', '2014-10-25 18:30'),
		('tsanjay', '2014-10-20 10:00', '2014-10-20 18:00'),
		('tsanjay', '2014-10-21 10:00', '2014-10-21 18:00'),
		('tsanjay', '2014-10-22 10:00', '2014-10-22 18:00'),
		('tsanjay', '2014-10-23 10:00', '2014-10-23 18:00'),
		('tsanjay', '2014-10-24 13:00', '2014-10-24 21:30'),
		('tdamon', '2014-10-26 10:45', '2014-10-26 17:15'),-- just copied and changed dates fuck data entry
		('tdamon', '2014-10-27 08:45', '2014-10-27 18:30'),
		('tdamon', '2014-10-28 09:00', '2014-10-28 18:30'),
		('tdamon', '2014-10-29 09:00', '2014-10-29 17:00'),
		('tdamon', '2014-10-30 13:00', '2014-10-30 21:30'),
		('tdamon', '2014-10-31 09:00', '2014-10-31 17:00'),
		('ttori', '2014-10-26 10:45', '2014-10-26 17:15'),
		('ttori', '2014-10-29 15:00', '2014-10-29 21:30'),
		('ttori', '2014-10-30 14:30', '2014-10-30 21:30'),
		('t884027', '2014-10-28 09:00', '2014-10-28 13:00'),
		('t884027', '2014-10-31 15:00', '2014-10-31 21:30'),
		('t884027', '2014-11-1 09:00', '2014-11-1 18:30'),
		('tmike', '2014-10-27 08:45', '2014-10-27 18:30'),
		('tmike', '2014-10-28 09:00', '2014-10-28 18:30'),
		('tmike', '2014-10-29 09:00', '2014-10-29 17:00'),
		('tmike', '2014-10-30 09:00', '2014-10-30 17:00'),
		('tmike', '2014-10-31 09:00', '2014-10-31 17:00'),
		('tmike', '2014-11-1 12:00', '2014-11-01 18:30'),
		('tsanjay', '2014-10-27 10:00', '2014-10-27 18:00'),
		('tsanjay', '2014-10-28 10:00', '2014-10-28 18:00'),
		('tsanjay', '2014-10-29 10:00', '2014-10-29 18:00'),
		('tsanjay', '2014-10-30 10:00', '2014-10-30 18:00'),
		('tsanjay', '2014-10-31 13:00', '2014-10-31 21:30');
;
