grammar cSQL ;

selectExpr  : select contents from address (where condition)? (with params)? NEWLINE ;
select      : 'select' ;
from        : 'from' ;
where       : 'where' ;
with        : 'with' ;

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
windows     : ('a-zA-Z')? ':\\' ('_'|ALPHABET)+ ('\\' ('_'|ALPHABET))* ;
unix        : '/' ('_'|ALPHABET)+ ('/' ('_'|ALPHABET))* ;

condition   : ;
params      : SET;
contents    : '*';

// Tokens
SET         : 'set' ;
NEWLINE     : ';'|('\r'? '\n') ;
HTTP        : [Hh][Tt][Tt][Pp] ;
FTP         : [Ff][Tt][Pp] ;
FILE        : [Ff][Ii][Ll][Ee][Ss] ;
DATABASE    : [Dd][Bb] ;
IP          : INT '.' INT '.' INT '.' INT ;
ALPHABET    : [A-Za-z0-9]+ ;
INT         : [0-9]+ ;

WS          : [ \t\n\r]+ -> skip ;