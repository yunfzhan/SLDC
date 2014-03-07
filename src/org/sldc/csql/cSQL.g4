grammar cSQL ;
// main entry rule
selectExpr  : SELECT contents FROM address (WHERE condition)? (WITH params)? NL ;

address     : protocols (',' protocols)*  ;
protocols   : (http|file|ftp|database) ;
http        : HTTP '://' (domains|IP) (':' INT)? ('/' domains* )* ('?' httpparams+ )? ;
file        : FILE '://' (windows|unix)+ ;
ftp         : FTP '://'  ;
database    : DATABASE '://'  ;

// For http address
domains     : ID ('.' ID)* ;
httpparams  : httpparams ('&' httpparams)*
            | HTTPCHAR ('=' VARID)?
            ;

// For files
windows		: LETTER ':\\' VARID ('\\' VARID)* ;
unix		: '/' VARID ('/' VARID)* ;

condition	: expr ;
params		: prop (',' prop)* ;
contents	: '*'|expr ;

expr		: FUNCID '(' exprList? ')' 	#Func	// match function call like f(), f(x), f(1,2)
			| expr '[' expr ']'			#Array	// match array index
			| '-' expr					#Minus
			| NOT expr					#Not
			| expr MULDIV expr			#MulDiv
			| expr ADDSUB expr			#AddSub
			| expr EQUAL expr			#Equal
			| VARID						#Var	// variables reference
			| NUMBER					#Num
			| '(' expr ')'				#Bracket
			;
exprList	: expr (',' expr)* ;
prop		: ID '=' STRING ;

// functions definition
fundecl		: FUNC FUNCID '(' funcParms ')' block ;
funcParms	: VARID (',' VARID)* ;

block		: BEGIN stat* END NL ;
stat		: block
			| varDecl NL
			| IF expr THEN stat (ELSEIF stat)* (ELSE stat)? NL
			| RET expr? NL
			| VAR? expr '=' expr NL	// assignment
			| expr NL				// function call
			;
varDecl		: VAR VARID (',' VARID)* NL ;

// Tokens
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
WHERE		: [Ww][Hh][Ee][Rr][Ee] ;
WITH		: [Ww][Ii][Tt][Hh] ;
SET			: [Ss][Ee][Tt] ;
HTTP		: [Hh][Tt][Tt][Pp] ;
FTP			: [Ff][Tt][Pp] ;
FILE		: [Ff][Ii][Ll][Ee][Ss] ;
DATABASE	: [Dd][Bb] ;
FUNC		: [Ff][Uu][Nn] ;
IP			: INT '.' INT '.' INT '.' INT ;
fragment
DIGIT		: [0-9] ;
INT			: DIGIT+ ;
LETTER		: [A-Za-z] ;
ID			: LETTER (LETTER|DIGIT)* ;
fragment
ALPHABET	: '_'|LETTER ;
HTTPCHAR	: '#'|(ALPHABET|DIGIT)+ ;
VARID    	: ALPHABET (ALPHABET|DIGIT)* ;
FUNCID		: VARID|('$' (ALPHABET|DIGIT)*) ;
STRING		: '"' .*? '"' ;
NUMBER		: '-'? ('.' DIGIT+ | DIGIT+ ('.' DIGIT*)? ) ;
NOT			: '!' ;
MULDIV		: '*'|'/' ;
ADDSUB		: '+'|'-' ;
EQUAL		: '==' ;
NL		    : '\r'? '\n' ;

// Line comment definition
COMMENT		: '//' .*? -> skip ;
WS			: [ \t]+ -> skip ;