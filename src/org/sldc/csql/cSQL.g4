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
row			: selectExpr NL?
			| fundecl
			| varDecl NL
			| varAssign NL
			| NL
			;
selectExpr  : SELECT contents FROM address (WHERE condition)? (WITH params)? ;

address     : protocols (COMMA protocols)*  ;
protocols   : (http|file|ftp|database) (AS Identifier)? ;
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
contents	: '*'|expr ;

expr		: Identifier '.' expr			#Obj
			| Identifier '(' exprList? ')' 	#Func	// match function call like f(), f(x), f(1,2)
			| expr '[' expr ']'				#Array	// match array index
			| '-' expr						#Minus
			| NOT expr						#Not
			| expr MULDIV expr				#MulDiv
			| expr ADDSUB expr				#AddSub
			| expr EQUAL expr				#Equal
			| expr COMMA expr				#List
			| Identifier					#Var	// variables reference
			| INT							#Int
			| Number						#Num
			| String						#String
			| '(' expr ')'					#Bracket
			;
exprList	: expr (COMMA expr)* ;
prop		: Identifier '=' .*? ;

// functions definition
fundecl		: FUNC Identifier '(' funcParms ')' NL? block ;
funcParms	: Identifier (COMMA Identifier)* ;

block		: BEGIN NL? stat* END NL ;
stat		: block
			| varDecl NL
			| IF expr THEN stat (ELSEIF stat)* (ELSE stat)? NL
			| RET expr? NL
			| VAR? expr '=' expr NL	// assignment
			| expr NL				// function call
			;
varDecl		: VAR Identifier (EQU (expr|selectExpr))? (COMMA Identifier (EQU (expr|selectExpr))?)* ;
varAssign	: Identifier EQU (expr|selectExpr) ;

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
URLChars	: ('\\'|NUMSIGN|PERCSIGN|DOLLAR|UNDERLINE|LETTER|DIGIT)+ {isHttp||isFtp}? ;
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
MULDIV		: '*'|'/' ;
ADDSUB		: PLUS|MINUS ;
fragment
PLUS		: '+' ;
fragment
MINUS		: '-' ;
EQU			: '=' ;
EQUAL		: '==' ;
COMMA		: ',' {clearSign();} ;
NL		    : '\r'? '\n' {clearSign();} ;



// Line comment definition
COMMENT		: '//' ~[\r\n]* -> skip ;
WS			: [ \t]+ -> skip ;
