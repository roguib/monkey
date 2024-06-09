# Monkey programming language

A monorepo containing the following projects:

## Table of Contents
- [Monkey interpreter](##overview-of-monkey)
- [Online playground](##online-playground) 
  - [Microservices](##microservices)
    - [playground-ws](###playground-ws)
    - [monkey-ws](###monkey-ws)
  - [Online editor](##online-editor)
- [How to run the project](##how-to-run-the-project)

## Overview of monkey
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

## Online playground
An online code editor to let users try out the different features of the language.

## Microservices
A Helidon MP microservice architecture that runs the entire business logic of the project.

### playground-ws
A microservice that handles the entire playground logic such as creating new playgrounds (from scratch or from an already prepared template) and keeping a history for a given playground.

### monkey-ws
A microservice that exposes the monkey interpreter. It waits for incoming requests with programs that have to be evaluated and returns the result from the compiler.

## Online editor
A React web application that uses the monaco editor to let users try the monkey features.

## How to run the project
From a CLI where docker compose is installed. 

```bash
docker-compose --env-file dev.env build --no-cache --progress=plain
docker-compose up
```