grammar cSQL ;
@lexer::members {
	private boolean isHttp = false;
	private boolean isFile = false;
	private boolean isFtp = false;
	private boolean isDatabase = false;
	
	private boolean isAllFalse()
	{
		return !isHttp&&!isFile&&!isFtp&&!isDatabase;
	}
	
	private void clearSign()
	{
		isHttp=isFile=isFtp=isDatabase=false;
	}
}
// main entry rule
program		: row+ ;
row			: stat
			| NL
			;
			
expr		: Identifier '.' expr			#Obj
			| Identifier '(' exprList? ')' 	#Func	// match function call like f(), f(x), f(1,2)
			| '(' expr ')'					#Bracket
			| expr '[' expr ']'				#Array	// match array index
			| '-' expr						#Minus
			| NOT expr						#Not
			| expr op=('*'|'/') expr		#MulDiv
			| expr ADDSUB expr				#AddSub
			| expr EQUAL expr				#Equal
			| varAssign						#Assign
			| Identifier					#Var	// variables reference
			| INT							#Int
			| Number						#Num
			| String						#String
			;
			
stat		: fundecl NL										#StatFuncDecl
			| selectExpr NL?									#StatSelect
			| block												#StatBlock
			| IF expr THEN stat (ELSEIF stat)* (ELSE stat)? NL	#StatIf
			| RET expr? NL										#StatReturn
			| expr NL											#StatExpr
			| varDecl NL										#StatVarDecl
			;
varDecl		: VAR varAssign (COMMA varAssign)* ;
varAssign	: Identifier (EQU (expr|selectExpr))? ;

selectExpr  : SELECT contents FROM address (WHERE condition)? (WITH params)? ;

address     : protocols (COMMA protocols)*  ;
protocols   : protocol (AS Identifier)? ;
protocol	: (http|file|ftp|database) ;
http        : HTTP '://' domains (':' INT)? ('/' domains)* '/'? ('?' httpparam ('&' httpparam)* )? ;
file        : FILE '://' (windows|unix)+ ;
ftp         : FTP '://' (user ':' password? '@')? domains ('/' remote?)* ;
database    : DATABASE '://'  ;

// For http address
domains     : (INT|URLChars) ('.' (INT|URLChars))* ;
httpparam	: URLChars (EQU (INT|URLChars)? )? ;

// For files
windows		: DriveLetter ':\\' PathChars ('\\' PathChars?)* ;
unix		: '/' PathChars ('/' PathChars?)* ;
// For ftp
user		: User ;
password	: .*? ;
remote		: FtpPath ('/' FtpPath?)* ;
// For database


condition	: expr ;
params		: prop (COMMA prop)* ;
contents	: '*'|exprList ;

exprList	: expr (COMMA expr)* ;
prop		: Identifier '=' .*? ;

// functions definition
fundecl		: FUNC Identifier '(' funcParms ')' NL? block ;
funcParms	: Identifier (COMMA Identifier)* ;

block		: BEGIN NL? stats END NL ;
stats		: stat* ;

// Keywords

VAR			: [Vv][Aa][Rr] ;
BEGIN		: [Bb][Ee][Gg][Ii][Nn] ;
END			: [Ee][Nn][Dd] ;
IF			: [Ii][Ff] ;
THEN		: [Tt][Hh][Ee][Nn] ;
ELSEIF		: [Ee][Ll][Ii][Ff] ;
ELSE		: [Ee][Ll][Ss][Ee] ;
RET			: [Rr][Ee][Tt][Rr] ;
SELECT		: [Ss][Ee][Ll][Ee][Cc][Tt] ;
FROM		: [Ff][Rr][Oo][Mm] ;
WHERE		: [Ww][Hh][Ee][Rr][Ee] {clearSign();} ;
WITH		: [Ww][Ii][Tt][Hh] ;
SET			: [Ss][Ee][Tt] ;
HTTP		: [Hh][Tt][Tt][Pp][Ss]? {isHttp=true;} ;
FTP			: [Ff][Tt][Pp] {isFtp=true;} ;
FILE		: [Ff][Ii][Ll][Ee][Ss] {isFile=true;} ;
DATABASE	: [Dd][Bb] {isDatabase=true;} ;
FUNC		: [Ff][Uu][Nn] ;
AS			: [Aa][Ss] {clearSign();} ;

// Tokens
INT			: DIGIT+ ;

Number		: MINUS? ( DIGIT+ (DOT DIGIT*)? ) {!isHttp&&!isFtp}? ;
// Common identifiers
Identifier	: {isAllFalse()}? (DOLLAR|UNDERLINE|LETTER) (NUMSIGN|DOLLAR|UNDERLINE|LETTER|DIGIT)* ;

// HTTP only
URLChars	: ('\\'|NUMSIGN|PERCSIGN|DOLLAR|UNDERLINE|MINUS|LETTER|DIGIT)+ {isHttp||isFtp}? ;
// File system only
DriveLetter	: LETTER {isFile}? ;
PathChars	: (NUMSIGN|PERCSIGN|DOLLAR|UNDERLINE|LETTER|DIGIT)+ {isFile}? ;
// Ftp only
User		: (UNDERLINE|LETTER|DIGIT)+ {isFtp}? ;
FtpPath		: (NUMSIGN|PERCSIGN|DOLLAR|UNDERLINE|LETTER|DIGIT)+ {isFtp}? ;
// Database only

String		: '"' .*? '"' ;

fragment
VARCHAR		: UNDERLINE|LETTER ;
fragment
DIGIT		: [0-9] ;
fragment
LETTER		: [A-Za-z] ;
fragment
PERCSIGN	: '%' ;
fragment
NUMSIGN		: '#' ;
fragment
DOLLAR		: '$' ;
fragment
UNDERLINE	: '_' ;
fragment
DOT			: '.' ;

NOT			: '!' ;
ADDSUB		: PLUS|MINUS ;

fragment
STAR		: '*' ;
fragment
SLASH		: '/' ;
fragment
PLUS		: '+' ;
fragment
MINUS		: '-' ;

EQU			: '=' ;
EQUAL		: '==' ;
COMMA		: ',' {clearSign();} ;
NL		    : '\r'? '\n' {clearSign();} ;



// Line comment definition
COMMENT		: '//' ~[\r\n]* NL -> skip ;
WS			: [ \t]+ -> skip ;
