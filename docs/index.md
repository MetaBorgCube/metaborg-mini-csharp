
# Mini C# #
This is an implementation of a subset of C# in the [Spoofax Language Workbench][1]. Its syntax is declared in SDF3,
and its semantics are declared in Statix. This version only supports the syntactic and semantic analysis, and can
neither run nor produce a C# class file.

The implementation is based on the [ECMA-334 standard][2].


## Features
The following features are demonstrated in Mini C#:

- class declarations
- class inheritance
- instance field and method declarations
- static field and method declarations
- references to local variables, arguments, `this`, `base`, (inherited/static) fields, and (inherited/static) methods


## Unsupported Features
Most features of C# are not supported in Mini C#, but the following are explicitly not implemented:

- interface extension
- method overloading
- generics
- extension methods
- abstract/sealed classes
- virtual/new/abstract methods
- nullable types


## Scope Graph
A class declaration gets two scopes: a `classScope`, which contains the static members of the class, and an `instanceScope`, which contains the instance member of the class. The `instanceScope` has a `P` edge to the `classScope`, which has a `P` edge to the global scope.


## Backlog
The following aspects have been implemented:

- [x] Class declarations
    - [x] Class Inheritance
- [x] Fields
    - [x] Instance
    - [x] Static
    - [ ] New
- [x] Methods
    - [x] Instance
    - [x] Static
    - [ ] Override
    - [ ] New
    - [ ] Overloading
- [ ] Statements
    - [ ] Variable declarations
        - [ ] Typed
        - [ ] Typed and initialized
        - [ ] Untyped and initialized
    - [ ] Variable assignments
    - [ ] If statements
    - [ ] Return statements
- [x] Expressions
    - [x] `this`
    - [x] `base`
    - [x] Variable and member references
    - [x] Function calls
    - [x] Constructors
    - [x] Member accessors
    - [x] Boolean operators
    - [x] Integer operators
    - [x] Ternary conditional operator


[1]: https://spoofax.org/
[2]: https://www.ecma-international.org/publications-and-standards/standards/ecma-334/
