# Learn-z3-Java
Collection of experimental code for working with z3 Java API.
All classes are readily executable in VSCode, with z3 Java binding installed. See https://github.com/Z3Prover/z3.

The `smt` directory holds dumped SMT-LIB2 encodings of some of the models specified by the Java API. They can mostly be executed by the `z3` CLI. It is particularly interesting to examine the bit-blasted encodings generated for BitVector operations, under different z3 **Tactics**, which sheds light on optimizing for BitVector formulas.

* `Basic.java` Mundane invocation of z3 Java API.
* `Util.java` Utility methods for invoking z3 to prove, instantiate model or dump SMT encoding.
* `GettingStarted.java` Explores more z3 Java basic behavior.
* `Boolean.java` Example for working with booleans, by proving the de Morgan law.
* `Array.java` Example for working with Array, which is handled by z3 as EUF (Equality over Uninterpreted Functions) plus Array Axioms. Notably, z3 *do not* learn inductive facts, as shown in a chained-deduction example.
* `DataType.java` Example for working with custom data types, including String, Enum and Tuple.
* `Quantifier.java` Example for working with z3 quantifiers. The Java API for quantifiers (`mkForall`, etc.) is rather obscure. A named argument list is annotated here.
* `BitVector.java` Example for working with z3 BitVectors. We use it to prove a GCC trick for integer division (converted to multiplication and shifting). Notably, this proof **scales poorly**, not finishing for 32-bit width. We explore a slightly more efficient encoding of assertions here as well.
* `BVSign.java` Playground for experimenting with z3 Tactics on BitVectors. We first check the possible differences in bit-blasted results of signed and unsigned BitVector formulas, then dig the z3 source code for the general Tactics that are applied to BitVector formulas before bit-blasting. Dump the generated SMT encoding, and explore the most effcient combination of optimizing Tactics.
