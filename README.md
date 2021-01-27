# Mini C#
This is an implementation of a subset of C# in the [Spoofax Language Workbench][1]. Its syntax is declared in SDF3, and its semantics are declared in Statix. This version only support the syntactic and semantic analysis, and can neither run nor produce a C# class file.

The implementation is based on the [ECMA-334 standard][2].


## Features
The following features are demonstrated in Mini C#:

- class declarations
- class inheritance
- instance field and method declarations
- method overrides
- references to local variables, arguments, `this`, `base`, (inherited) fields, and (inherited) methods


## Unsupported Features
Most features of C# are not supported in Mini C#, but the following are explicitly not implemented:

- interface extension
- method overloading
- generics
- extension methods
- abstract/sealed classes
- virtual/new/abstract methods
- nullable types


## License
Copyright 2021 Daniel A. A. Pelsmaeker

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an **"as is" basis, without warranties or conditions of any kind**, either express or implied. See the License for the specific language governing permissions and limitations under the License.


[1]: https://spoofax.org/
[2]: https://www.ecma-international.org/publications-and-standards/standards/ecma-334/