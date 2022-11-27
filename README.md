# A Java 8 interpreter for Monkey

This is my  Java 8 implementation of the interpreter described in the book [Writing an interpreter in Go](https://interpreterbook.com/). I always have been interested in learning how interpreters and compilers work under the hood, and found Thorsten's book an excellent introduction to this broader subject.

## System requirements

Any compatible Java 8 JRE should be enough to run this project. Execute the following command to install all dependencies:

```
mvn clean install
```

After that you will be able to run the tests by typing in your terminal:

```
mvn test
```

## Overview of Monkey

Monkey language was created to showcase the different topics the book covers. It has a similar syntax as the C language and some of its features are:

- Variable bindings
- Integers and booleans
- Arithmetic expressions
- Built-in functions
- First-class and higher-order functions
- Closures
- A string, array and hash data structure

A simple Monkey program, that declares two variables and a function that returns the addition of two parameters:
```
let five = 5;
let ten = 10;

let add = fn(x, y) {
    x + y;
};

let result = add(five, ten);
```

