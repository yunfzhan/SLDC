grammar cSQL ;
// main entry rule
selectExpr  : SELECT contents FROM address (WHERE condition)? (WITH params)? NL ;

address     : protocols (',' protocols)*  ;
protocols   : (http|file|ftp|database) ;
http        : HTTP '://' (domains|IP)+ (':' INT)* ('/' domains ('?' httpparams)* )* ;
file        : FILE '://' (windows|unix)+ ;
ftp         : FTP '://'  ;
database    : DATABASE '://'  ;

// For http address
domains     : (ALPHABET ('.' ALPHABET)*) ;
httpparams  : httpparams '&' httpparams
            | ALPHABET '=' ALPHABET*
            ;

// For files
windows     : LETTER ':\\' ('_'|ALPHABET)+ ('\\' ('_'|ALPHABET))* ;
unix        : '/' ('_'|ALPHABET)+ ('/' ('_'|ALPHABET))* ;

condition   : expr ;
params      : prop (',' prop)* ;
contents    : '*'|expr;
expr		: ID '(' exprList? ')' NL	// match function call like f(), f(x), f(1,2)
			| expr '[' expr ']' NL		// match array index
			| '-' expr NL
			| NOT expr NL
			| expr MULDIV expr NL
			| expr ADDSUB expr NL
			| expr EQUAL expr NL
			| ALPHABET NL				// variables reference
			| INT NL
			| '(' expr ')' NL
			;
exprList	: expr (',' expr)* ;
prop		: ALPHABET '=' STRING NL ;

// functions definition
fundecl		: FUNC ID '(' funcParms ')' block;
funcParms	: ALPHABET (',' ALPHABET)* ;

block		: BEGIN stat* END NL ;
stat		: block
			| varDecl NL
			| IF expr THEN stat (ELSEIF stat)* (ELSE stat)? NL
			| RET expr? NL
			| VAR? expr '=' expr NL	// assignment
			| expr NL				// function call
			;
varDecl		: VAR ALPHABET (',' ALPHABET)* NL ;

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
SET         : [Ss][Ee][Tt] ;
HTTP        : [Hh][Tt][Tt][Pp] ;
FTP         : [Ff][Tt][Pp] ;
FILE        : [Ff][Ii][Ll][Ee][Ss] ;
DATABASE    : [Dd][Bb] ;
FUNC		: [Ff][Uu][Nn] ;
IP          : INT '.' INT '.' INT '.' INT ;
LETTER		: [A-Za-z] ;
ALPHABET    : [A-Za-z0-9]+ ;
STRING		: '"' .*? '"' ;
INT         : [0-9]+ ;
ID			: ('$' [A-Z_a-z0-9]*|[A-Z_a-z0-9]+) ;
NOT			: '!' ;
MULDIV		: '*'|'/' ;
ADDSUB		: '+'|'-' ;
EQUAL		: '==' ;
NL		    : ';'|('\r'? '\n') ;

// Line comment definition
COMMENT		: '//' .*? '\n' -> skip ;
WS          : [ \t\n\r]+ -> skip ;