ALTER TABLE	employee_competency
ADD UNIQUE INDEX 	idxEmployee_Competency
(
	employeeID
,	competencyID
)
;
