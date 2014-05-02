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
			
expr		: Identifier '(' exprList? ')' 	#Func	// match function call like f(), f(x), f(1,2)
			| '(' expr ')'					#Bracket
			| expr '[' expr ']'				#Array	// match array index
			| '-' expr						#Minus
			| NOT expr						#Not
			| expr op=('*'|'/') expr		#MulDiv
			| expr ADDSUB expr				#AddSub
			| expr EQUAL expr				#Equal
			| expr NE expr					#Unequal
			| expr GE expr					#GreaterEqual
			| expr LE expr					#LowerEqual
			| expr GT expr					#Greater
			| expr LT expr					#Lower
			| expr AND expr					#And
			| expr OR expr					#Or
			| Identifier					#Var	// variables reference
			| varAssign						#Assign			
			| INT							#Int
			| Number						#Num
			| String						#String
			;
			
stat		: fundecl NL										#StatFuncDecl
			| selectExpr NL?									#StatSelect
			| block												#StatBlock
			| ifStat elifStat* NL? elseStat? END NL?			#StatIf
			| loopStat NL										#StatLoop
			| RET expr? NL										#StatReturn
			| expr NL											#StatExpr
			| varAssign (COMMA varAssign)* 						#StatVar
			;
loopStat	: whileStat						#WhileLoop
			| forStat						#ForLoop
			;
whileStat	: WHILE expr NL stats END ;
forStat		: FOR varAssign ',' expr ',' expr NL stats END ;
ifStat		: IF expr THEN NL? stats ;
elifStat	: ELSEIF expr THEN NL? stats ;
elseStat	: ELSE NL? stats ;
varAssign	: Identifier (EQU (expr|selectExpr))? ;

exprList	: expr (COMMA expr)* ;
prop		: Identifier '=' String ;

// functions definition
fundecl		: FUNC Identifier '(' funcParms ')' NL? block ;
funcParms	: Identifier (COMMA Identifier)* ;

block		: BEGIN NL? stats END NL ;
stats		: stat* ;

selectExpr  : SELECT contents FROM address (WHERE condition)? (WITH params)? ;

address     : protocols (COMMA protocols)* ;
protocols   : protocol (AS Identifier)? ;
protocol	: (http|file|ftp|database) ;
http        : HTTP '://' domains (':' INT)? ('/' domains)* '/'? ('?' httpparam ('&' httpparam)* )? ;
file        : FILE '://' (windows|unix)+ ;
ftp         : FTP '://' (user ':' password '@')? domains ('/' remote?)* ;
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


// Keywords

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
WHILE		: [Ww][Hh][Ii][Ll][Ee] ;
FOR			: [Ff][Oo][Rr] ;
HTTP		: [Hh][Tt][Tt][Pp][Ss]? {isHttp=true;} ;
FTP			: [Ff][Tt][Pp] {isFtp=true;} ;
FILE		: [Ff][Ii][Ll][Ee][Ss] {isFile=true;} ;
DATABASE	: [Dd][Bb] {isDatabase=true;} ;
FUNC		: [Ff][Uu][Nn] ;
AND			: [Aa][Nn][Dd] ;
OR			: [Oo][Rr] ;
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

String		: '"' .*? '"'
			| '\'' .*? '\'' 
			;

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
NE			: '!=' ;
GE			: '>=' ;
LE			: '<=' ;
GT			: '>' ;
LT			: '<' ;
COMMA		: ',' {clearSign();} ;
NL		    : '\r'? '\n' {clearSign();} ;



// Line comment definition
COMMENT		: '//' ~[\r\n]* NL -> skip ;
WS			: [ \t]+ -> skip ;
