# full-compiler
A flexible and customizable complete compiler with your manual configuration written with java language and javafx library

## Documentation
This project consists of two major sections:

 - **lexical analysis**
 - **synthesis analysis**
 
 And at last they have been concatenated.
 
 ***Note That***
 
 - This project has been implemented with **java** and **javafx** in **net beans** ide.
 - The **demo** of project is available in exe and jar format. you can download it [here](https://alirezakay.github.io/showcase/term5).
 - This compiler takes two customized **Configuration**.
   - one for lexemes
   - the other for grammars
 
So you must first write a config file for lexical analyser and one, for sythesis analyser in the format below:

#### lexeme.conf format

```
lexeme_name_upper_case := lexeme_regex
```
For example: `DIGIT := [0-9]`

### You can define the lexems using **regexes** with these operators:

> **\*** : for a phrase repetition for zero or more times
>> a* : {ε, a, aa, aaa, ...}

> **\+** : for a phrase repetition for more than one times
>> a+ : {ε, a, aa, aaa, ...}

> **|** : for using *or* concept
>> (a|b) : {a} or {b}

> **()** : for keeping a pharse alongside together
>> (ab)+ : {ab, abab, ababab, ...}

> **[]** : for defining a period
>> [0-9] : {0,1,2,3,4,5,6,7,8,9}

> **\{LEXEME_NAME}** : for using some onother defined lexeme
>> DIGIT := [0-9]
>> LETTER := [a-z]|[A-Z]
>> ID := \LETTER(\LETTER|_|\DIGIT)+
 
 

<hr />

## Authors

  - Alireza Kavian ( [@alirezakay](https://github.com/alirezakay) )
  - Soheil Changizi ( [@cocolico14](https://github.com/cocolico14) )
  
## Org.

  - ***[Brilacasck](https://brilacasck.ir)*** 
  
## Team
  
  - ***ASCK TEAM***

## License

This project is licensed under the GNU GPLv3 License - see the [LICENSE](./LICENSE) file for details
