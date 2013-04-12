DROP PROCEDURE IF EXISTS	`vspPerson`
;

DELIMITER //
CREATE PROCEDURE	vspPerson
(
	Person_id		int signed		
,	Person_tp		varchar(80)		
,	Person_nm		varchar(128)		
,	First_nm		varchar(128)		
,	Last_nm		varchar(128)		
,	Middle_nm		varchar(128)		
,	Gender_cd		varchar(48)		
,	FirstSNDX_cd		varchar(48)		
,	LastSNDX_cd		varchar(48)		
,	Birth_dm		datetime		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspPerson
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblPerson
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblPerson';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspPerson';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblPerson
		WHERE
				Person_id	= _Person_id
			AND	Person_tp	= _Person_tp
			AND	Person_nm	= _Person_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspPerson
		(
			@Person_id	:= _Person_id
		,	@Person_tp	:= _Person_tp
		,	@Person_nm	:= _Person_nm
		,	@First_nm	:= _First_nm
		,	@Last_nm	:= _Last_nm
		,	@Middle_nm	:= _Middle_nm
		,	@Gender_cd	:= _Gender_cd
		,	@FirstSNDX_cd	:= _FirstSNDX_cd
		,	@LastSNDX_cd	:= _LastSNDX_cd
		,	@Birth_dm	:= _Birth_dm

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblPerson'
			,	@Key_nm		:= ', Person_tp, Person_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspPersonType`
;

DELIMITER //
CREATE PROCEDURE	vspPersonType
(
	Person_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspPersonType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblPersonType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblPersonType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspPersonType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblPersonType
		WHERE
				Person_tp	= _Person_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspPersonType
		(
			@Person_tp	:= _Person_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblPersonType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspReport`
;

DELIMITER //
CREATE PROCEDURE	vspReport
(
	Report_id		int signed		
,	Report_tp		varchar(80)		
,	Report_nm		varchar(128)		
,	Report_cd		varchar(48)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspReport
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblReport
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblReport';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspReport';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblReport
		WHERE
				Report_id	= _Report_id
			AND	Report_tp	= _Report_tp
			AND	Report_nm	= _Report_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspReport
		(
			@Report_id	:= _Report_id
		,	@Report_tp	:= _Report_tp
		,	@Report_nm	:= _Report_nm
		,	@Report_cd	:= _Report_cd

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblReport'
			,	@Key_nm		:= ', Report_tp, Report_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspReportType`
;

DELIMITER //
CREATE PROCEDURE	vspReportType
(
	Report_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspReportType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblReportType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblReportType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspReportType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblReportType
		WHERE
				Report_tp	= _Report_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspReportType
		(
			@Report_tp	:= _Report_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblReportType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspResource`
;

DELIMITER //
CREATE PROCEDURE	vspResource
(
	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Resrc_nm		varchar(128)		
,	Resrc_tx		mediumtext		
,	ADD_dm		datetime		
,	ADD_nm		varchar(128)		
,	UPD_dm		datetime		
,	UPD_nm		varchar(128)		
,	DEL_dm		datetime		
,	DEL_nm		varchar(128)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspResource
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblResource
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblResource';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspResource';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblResource
		WHERE
				Resrc_id	= _Resrc_id
			AND	Resrc_tp	= _Resrc_tp
			AND	Resrc_nm	= _Resrc_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResource
		(
			@Resrc_id	:= _Resrc_id
		,	@Resrc_tp	:= _Resrc_tp
		,	@Resrc_nm	:= _Resrc_nm
		,	@Resrc_tx	:= _Resrc_tx
		,	@ADD_dm	:= _ADD_dm
		,	@ADD_nm	:= _ADD_nm
		,	@UPD_dm	:= _UPD_dm
		,	@UPD_nm	:= _UPD_nm
		,	@DEL_dm	:= _DEL_dm
		,	@DEL_nm	:= _DEL_nm

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblResource'
			,	@Key_nm		:= ', Resrc_tp, Resrc_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;

	IF
		_SYSRIGHT = 'INSERT'
	THEN
		IF	_ADD_dm	IS NULL
		THEN
			SET _ADD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	_ADD_nm	IS NULL
		THEN
			SET	_ADD_nm	= CURRENT_USER();
		END IF;
	END IF;

	IF
		_SYSRIGHT = 'UPDATE'
	THEN
		IF	_UPD_dm	IS NULL
		THEN
			SET	_UPD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	_UPD_nm	IS NULL
		THEN
			SET	_UPD_nm	= CURRENT_USER();
		END IF;
	END IF;

	IF
		_SYSRIGHT = 'DELETE'
	THEN
		IF	_ADD_dm	IS NULL
		THEN
			SET	_ADD_dm	= UTC_TIMESTAMP();
		END IF;
		IF	_ADD_nm	IS NULL
		THEN
			SET	_ADD_nm	= CURRENT_USER();
		END IF;
	END IF;
	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspResourceType`
;

DELIMITER //
CREATE PROCEDURE	vspResourceType
(
	Resrc_tp		varchar(80)		
,	ParentResrc_tp		varchar(80)		
,	ResrcType_tx		mediumtext		
,	Left_id		int signed		
,	Right_id		int signed		
,	Level_id		int signed		
,	Order_id		int signed		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspResourceType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblResourceType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblResourceType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspResourceType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblResourceType
		WHERE
				Resrc_tp	= _Resrc_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResourceType
		(
			@Resrc_tp	:= _Resrc_tp
		,	@ParentResrc_tp	:= _ParentResrc_tp
		,	@ResrcType_tx	:= _ResrcType_tx
		,	@Left_id	:= _Left_id
		,	@Right_id	:= _Right_id
		,	@Level_id	:= _Level_id
		,	@Order_id	:= _Order_id

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblResourceType'
			,	@Key_nm		:= ', Resrc_tp'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspRightType`
;

DELIMITER //
CREATE PROCEDURE	vspRightType
(
	Right_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRightType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRightType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRightType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblRightType
		WHERE
				Right_tp	= _Right_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRightType
		(
			@Right_tp	:= _Right_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblRightType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspRole`
;

DELIMITER //
CREATE PROCEDURE	vspRole
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Role_nm		varchar(128)		
,	Role_cd		varchar(48)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRole
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRole
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRole';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRole';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblRole
		WHERE
				Role_id	= _Role_id
			AND	Role_tp	= _Role_tp
			AND	Role_nm	= _Role_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRole
		(
			@Role_id	:= _Role_id
		,	@Role_tp	:= _Role_tp
		,	@Role_nm	:= _Role_nm
		,	@Role_cd	:= _Role_cd

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblRole'
			,	@Key_nm		:= ', Role_tp, Role_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspRoleType`
;

DELIMITER //
CREATE PROCEDURE	vspRoleType
(
	Role_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRoleType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRoleType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRoleType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRoleType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblRoleType
		WHERE
				Role_tp	= _Role_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRoleType
		(
			@Role_tp	:= _Role_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblRoleType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspUser`
;

DELIMITER //
CREATE PROCEDURE	vspUser
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	User_nm		varchar(128)		
,	Password_cd		varchar(48)		
,	Domain_nm		varchar(128)		
,	Email_tx		mediumtext		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspUser
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblUser
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblUser';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspUser';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblUser
		WHERE
				User_id	= _User_id
			AND	User_tp	= _User_tp
			AND	User_nm	= _User_nm

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspUser
		(
			@User_id	:= _User_id
		,	@User_tp	:= _User_tp
		,	@User_nm	:= _User_nm
		,	@Password_cd	:= _Password_cd
		,	@Domain_nm	:= _Domain_nm
		,	@Email_tx	:= _Email_tx

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblUser'
			,	@Key_nm		:= ', User_tp, User_nm'
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspUserType`
;

DELIMITER //
CREATE PROCEDURE	vspUserType
(
	User_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspUserType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblUserType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblUserType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspUserType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblUserType
		WHERE
				User_tp	= _User_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspUserType
		(
			@User_tp	:= _User_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblUserType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspResource_RightType`
;

DELIMITER //
CREATE PROCEDURE	vspResource_RightType
(
	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspResource_RightType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblResource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblResource_RightType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspResource_RightType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblResource_RightType
		WHERE
				Resrc_id	= _Resrc_id
			AND	Resrc_tp	= _Resrc_tp
			AND	Right_tp	= _Right_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResource_RightType
		(
			@Resrc_id	:= _Resrc_id
		,	@Resrc_tp	:= _Resrc_tp
		,	@Right_tp	:= _Right_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblResource_RightType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspResourceType_RightType`
;

DELIMITER //
CREATE PROCEDURE	vspResourceType_RightType
(
	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspResourceType_RightType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblResourceType_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblResourceType_RightType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspResourceType_RightType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblResourceType_RightType
		WHERE
				Resrc_tp	= _Resrc_tp
			AND	Right_tp	= _Right_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspResourceType_RightType
		(
			@Resrc_tp	:= _Resrc_tp
		,	@Right_tp	:= _Right_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblResourceType_RightType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspRole_Resource_RightType`
;

DELIMITER //
CREATE PROCEDURE	vspRole_Resource_RightType
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Resrc_id		int signed		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRole_Resource_RightType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRole_Resource_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRole_Resource_RightType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRole_Resource_RightType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblRole_Resource_RightType
		WHERE
				Role_id	= _Role_id
			AND	Role_tp	= _Role_tp
			AND	Resrc_id	= _Resrc_id
			AND	Resrc_tp	= _Resrc_tp
			AND	Right_tp	= _Right_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRole_Resource_RightType
		(
			@Role_id	:= _Role_id
		,	@Role_tp	:= _Role_tp
		,	@Resrc_id	:= _Resrc_id
		,	@Resrc_tp	:= _Resrc_tp
		,	@Right_tp	:= _Right_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblRole_Resource_RightType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspRole_ResourceType_RightType`
;

DELIMITER //
CREATE PROCEDURE	vspRole_ResourceType_RightType
(
	Role_id		int signed		
,	Role_tp		varchar(80)		
,	Resrc_tp		varchar(80)		
,	Right_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspRole_ResourceType_RightType
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblRole_ResourceType_RightType
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblRole_ResourceType_RightType';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspRole_ResourceType_RightType';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblRole_ResourceType_RightType
		WHERE
				Role_id	= _Role_id
			AND	Role_tp	= _Role_tp
			AND	Resrc_tp	= _Resrc_tp
			AND	Right_tp	= _Right_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspRole_ResourceType_RightType
		(
			@Role_id	:= _Role_id
		,	@Role_tp	:= _Role_tp
		,	@Resrc_tp	:= _Resrc_tp
		,	@Right_tp	:= _Right_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblRole_ResourceType_RightType'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspUser_Person`
;

DELIMITER //
CREATE PROCEDURE	vspUser_Person
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	Person_id		int signed		
,	Person_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspUser_Person
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblUser_Person
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblUser_Person';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspUser_Person';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblUser_Person
		WHERE
				User_id	= _User_id
			AND	User_tp	= _User_tp
			AND	Person_id	= _Person_id
			AND	Person_tp	= _Person_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspUser_Person
		(
			@User_id	:= _User_id
		,	@User_tp	:= _User_tp
		,	@Person_id	:= _Person_id
		,	@Person_tp	:= _Person_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblUser_Person'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;
DROP PROCEDURE IF EXISTS	`vspUser_Role`
;

DELIMITER //
CREATE PROCEDURE	vspUser_Role
(
	User_id		int signed		
,	User_tp		varchar(80)		
,	Role_id		int signed		
,	Role_tp		varchar(80)		

,	_SYSRIGHT		VARCHAR(30)		-- Intended DML action
,	_Mode_cd		VARCHAR(16)		-- Database cascade mode code
,	OUT 	_IsValid_fg	BOOLEAN
)
BEGIN
/*
**	Name:		vspUser_Role
**	Type:		Validation Stored Procedure
**	Purpose:	Validate a record in tblUser_Role
**	Author:		Solomon S. Shacter
**	Company:	Innovella, Inc.
**
**	Modified:	4/12/2013
**	Modnumber:
**	Modification:	
**
*/
###############################################################################
DECLARE	_SYSTABLE	VARCHAR(255) DEFAULT 'tblUser_Role';
DECLARE	_Proc_nm	VARCHAR(255) DEFAULT 'vspUser_Role';
DECLARE	_Key_cd		VARCHAR(16) DEFAULT 'PK';
DECLARE _RowExists_fg	TINYINT	DEFAULT 0;
DECLARE _ProcFailed_fg	BOOLEAN DEFAULT FALSE;
###############################################################################
VSP:
BEGIN
	#######################################################################
	-- Initialize
	#######################################################################
	#######################################################################
	-- Validate:
	--
	--	Duplicate names within a type are not allowed
	--	Alternate (Candidate) Key Check
	#######################################################################


	IF
	EXISTS
	(
		SELECT	1
		FROM
			tblUser_Role
		WHERE
				User_id	= _User_id
			AND	User_tp	= _User_tp
			AND	Role_id	= _Role_id
			AND	Role_tp	= _Role_tp

	)
	THEN
		SET _IsValid_fg	= TRUE;	-- Return if the attributes did not change
		LEAVE VSP;
	ELSE
		CALL	rspUser_Role
		(
			@User_id	:= _User_id
		,	@User_tp	:= _User_tp
		,	@Role_id	:= _Role_id
		,	@Role_tp	:= _Role_tp

		,	@Key_cd		:= 'AK'
		,	@RowExists_fg
		);

		IF
			@RowExists_fg	= 1	-- AK exists and not for this PK
		THEN
			SET _IsValid_fg	= FALSE;
			CALL 	errAKExist
			(
				@Proc_nm	:= _Proc_nm
			,	@Table_nm	:= 'tblUser_Role'
			,	@Key_nm		:= ''
			);
			LEAVE VSP;
		END IF;
	END IF;






	#######################################################################
END;
###############################################################################
END
//
DELIMITER ;
;

