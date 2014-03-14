grammar cSQL ;
@lexer::members {
	private boolean isHttp = false;
	
	private void clearSign()
	{
		isHttp=false;
	}
}
// main entry rule
selectExpr  : SELECT contents FROM address (WHERE condition)? (WITH params)? NL ;

address     : protocols (COMMA protocols)*  ;
protocols   : (http|file|ftp|database) ;
http        : HTTP '://' (domains|IP) (':' INT)? ('/' domains)* '/'? ('?' httpparam ('&' httpparam)* )? ;
file        : FILE '://' (windows|unix)+ ;
ftp         : FTP '://'  ;
database    : DATABASE '://'  ;

// For http address
domains     : HTTPCHARS ('.' HTTPCHARS)* ;
httpparam	: HTTPCHARS (EQU HTTPCHARS? )? ;

// For files
windows		: ':\\' ; //ANYCHAR ':\\' ANYCHAR+ ('\\' ANYCHAR+)* ;
unix		: '/' ; //'/' ANYCHAR+ ('/' ANYCHAR+)* ;

condition	: expr ;
params		: prop (COMMA prop)* ;
contents	: '*'|expr ;

expr		: Identifier '(' exprList? ')' 	#Func	// match function call like f(), f(x), f(1,2)
			| expr '[' expr ']'				#Array	// match array index
			| '-' expr						#Minus
			| NOT expr						#Not
			| expr MULDIV expr				#MulDiv
			| expr ADDSUB expr				#AddSub
			| expr EQUAL expr				#Equal
			| Identifier					#Var	// variables reference
			| INT							#Int
			| Number						#Num
			| '(' expr ')'					#Bracket
			;
exprList	: expr (COMMA expr)* ;
prop		: Identifier '=' .*? ;

// functions definition
fundecl		: FUNC Identifier '(' funcParms ')' block ;
funcParms	: Identifier (COMMA Identifier)* ;

block		: BEGIN stat* END NL ;
stat		: block
			| varDecl NL
			| IF expr THEN stat (ELSEIF stat)* (ELSE stat)? NL
			| RET expr? NL
			| VAR? expr '=' expr NL	// assignment
			| expr NL				// function call
			;
varDecl		: VAR Identifier (COMMA Identifier)* NL ;

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
FTP			: [Ff][Tt][Pp] ;
FILE		: [Ff][Ii][Ll][Ee][Ss] ;
DATABASE	: [Dd][Bb] ;
FUNC		: [Ff][Uu][Nn] ;

// Tokens
IP			: ([1-9] DIGIT*) '.' ([1-9] DIGIT*) '.' ([1-9] DIGIT*) '.' ([1-9] DIGIT*) ;
INT			: DIGIT+ ;
Number		: MINUS? (DOT DIGIT+ | DIGIT+ (DOT DIGIT*)? ) ;
Identifier	: {isHttp==false}? (DOLLAR|UNDERLINE|LETTER) (NUMSIGN|DOLLAR|UNDERLINE|LETTER|DIGIT)* ;
HTTPCHARS	: (NUMSIGN|DOLLAR|UNDERLINE|LETTER|DIGIT)+ {isHttp}? ;
String		: '"' .*? '"' ;

fragment
VARCHAR		: UNDERLINE|LETTER ;
fragment
DIGIT		: [0-9] ;
fragment
LETTER		: [A-Za-z] ;
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
COMMENT		: '//' .*? -> skip ;
WS			: [ \t]+ -> skip ;
