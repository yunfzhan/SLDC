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
// each line contains
row			: stat ;
// expression syntax
expr		: Identifier '(' exprList? ')' 	#Func	// match function call like f(), f(x), f(1,2)
			| expr '[' expr ']'				#Array	// match array index
			| expr '(' exprList ')'			#ExprFunc
			| '(' exprList ')'				#Bracket
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
			| arrayValues					#ArrayConst
			| varAssign						#Assign			
			| INT							#Int
			| Number						#Num
			| String						#String
			;
exprList	: expr (COMMA expr)* ;
// syntax for a whole row or rows		
statement	: fundecl NL?										#StatFuncDecl
			| selectExpr NL?									#StatSelect
			| block												#StatBlock
			| ifStat elifStat* NL? elseStat?					#StatIf
			| loopStat NL?										#StatLoop
			| RET expr? NL										#StatReturn
			| expr NL											#StatExpr
			| varAssign (COMMA varAssign)* 						#StatVar
			| BREAK												#StatBreak
			| CONTINUE											#StatContinue
			| NL												#StatWS
			;
// because I would like to deal with statement in loop, I add this rule.
stat		: statement ;
stats		: stat* ;
block		: BEGIN NL? stats END NL? ;
// Loop
loopStat	: whileStat						#WhileLoop
			| forStat						#ForLoop
			;
whileStat	: WHILE expr NL stat ;
forStat		: FOR varAssign ',' expr ',' expr NL stat ;
// Judgement
ifStat		: IF expr THEN NL? stat ;
elifStat	: ELSEIF expr THEN NL? stat ;
elseStat	: ELSE NL? stat ;
// grammar
varAssign	: Identifier (EQU (expr|selectExpr))? ;
assignList	: varAssign (COMMA varAssign)* ;
arrayValues	: '[' exprList ']' ;

// functions definition
fundecl		: FUNC Identifier '(' funcParms? ')' NL? block ;
funcParms	: Identifier (COMMA Identifier)* ;

prop		: Identifier EQU (String|Identifier) ;
// select grammar definition
selectExpr  : SELECT contents FROM address (WHERE condition)? ;

address     : protocols (COMMA protocols)* ;
protocols   : (expr|protocol) (AS Identifier)? ;
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


condition	: assignList ;
contents	: '*'|contentList ;
contentList	: content (COMMA content)* ;
content		: expr (AS String)? ;


// Keywords

BEGIN		: [Bb][Ee][Gg][Ii][Nn] ;
END			: [Ee][Nn][Dd] ;
IF			: [Ii][Ff] ;
THEN		: [Tt][Hh][Ee][Nn] ;
ELSEIF		: [Ee][Ll][Ii][Ff] ;
ELSE		: [Ee][Ll][Ss][Ee] ;
RET			: [Rr][Ee][Tt][Uu][Rr][Nn] ;
SELECT		: [Ss][Ee][Ll][Ee][Cc][Tt] ;
FROM		: [Ff][Rr][Oo][Mm] ;
WHERE		: [Ww][Hh][Ee][Rr][Ee] {clearSign();} ;
SET			: [Ss][Ee][Tt] ;
WHILE		: [Ww][Hh][Ii][Ll][Ee] ;
FOR			: [Ff][Oo][Rr] ;
BREAK 		: [Bb][Rr][Ee][Aa][Kk] ;
CONTINUE 	: [Cc][Oo][Nn][Tt][Ii][Nn][Uu][Ee] ;
HTTP		: [Hh][Tt][Tt][Pp][Ss]? {isHttp=true;} ;
FTP			: [Ff][Tt][Pp] {isFtp=true;} ;
FILE		: [Ff][Ii][Ll][Ee][Ss] {isFile=true;} ;
DATABASE	: [Dd][Bb] {isDatabase=true;} ;
FUNC		: [Ff][Uu][Nn] ;
AND			: [Aa][Nn][Dd] ;
OR			: [Oo][Rr] ;
AS			: [Aa][Ss] {clearSign();} ;

// Tokens
INT			: MINUS? DIGIT+ ;

Number		: MINUS? ( DIGIT+ (DOT DIGIT*)? ) {!isHttp&&!isFtp}? ;
// Common identifiers
Identifier	: {isAllFalse()}? (DOLLAR|UNDERLINE|LETTER) (NUMSIGN|MINUS|DOLLAR|UNDERLINE|LETTER|DIGIT)* ;

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
