# Class Declarations
A simple class declaration:

```c#
class A { }
```

A class declaration with inheritance:

```c#
class A { }

class B : A { }

class C : B { }
```

!!! hint "Cyclic inheritance"

    Direct cyclic inheritance is not allowed:

    ```c#
    class C : C { }
    ```

    Indirect cyclic inheritance is also not allowed:

    ```c#
    class B : C { }

    class C : B { }
    ```
