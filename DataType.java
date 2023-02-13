import com.microsoft.z3.*;

/**
 * #6 More data types
 */

public class DataType {

    @SuppressWarnings("unchecked")
    static void basicString() {
        Util.log("BasicString");
        var ctx = new Context();
        var s = ctx.mkSolver();

        var str = ctx.mkString("str");

        // Prove that if lengths of prefix and suffix add up
        // to length of str, they concatenate into str.
        var pref = ctx.mkString("pref");
        var suff = ctx.mkString("suff");

        s.add(ctx.mkPrefixOf(pref, str));
        s.add(ctx.mkSuffixOf(suff, str));
        s.add(ctx.mkEq(
            ctx.mkAdd(ctx.mkLength(pref), ctx.mkLength(suff)),
            ctx.mkLength(str)
        ));

        // Goal
        s.add(ctx.mkNot(
            ctx.mkEq(
                ctx.mkConcat(pref, suff),
                str
            )
        ));

        Util.solve(s);
        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void basicEnum() {
        Util.log("BasicEnum");
        var ctx = new Context();
        var s = ctx.mkSolver();

        var Enum = ctx.mkEnumSort("Enum", "A", "B", "C");
        var xs = new Expr[4];
        for (int i=0;i<4;i++) {
            xs[i] = ctx.mkConst(String.format("x_%d", i), Enum);
        }

        // Impossible to have 4 distinct enums of type Enum
        s.add(ctx.mkDistinct(xs));

        Util.solve(s);
        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void basicTuple() {
        Util.log("BasicTuple");
        var ctx = new Context();
        var s = ctx.mkSolver();

        var Z2 = ctx.mkTupleSort(
            ctx.mkSymbol("Z2"),
            new Symbol[]{ctx.mkSymbol("x"), ctx.mkSymbol("y")},
            new Sort[]{ctx.mkIntSort(), ctx.mkIntSort()}
        );

        // Find the inner product of Z^2

        var inProd = ctx.mkFuncDecl("inner-product", new Sort[]{Z2, Z2}, ctx.mkIntSort());
        var getx = (FuncDecl<IntSort>) Z2.getFieldDecls()[0];
        var gety = (FuncDecl<IntSort>) Z2.getFieldDecls()[1];
        var getZ2 = Z2.mkDecl();

        var x = ctx.mkConst("x", Z2);
        var y = ctx.mkConst("y", Z2);
        var z = ctx.mkConst("z", Z2);
        var a = ctx.mkIntConst("a");
        var b = ctx.mkIntConst("b");
 
        // Define the scale operation
        var scale = ctx.mkFuncDecl("scale", new Sort[]{ctx.mkIntSort(), Z2}, Z2);
        s.add(ctx.mkForall(
            new Expr[] {x, a},
            ctx.mkEq(
                ctx.mkApp(scale, a, x),
                ctx.mkApp(getZ2, ctx.mkMul(a, ctx.mkApp(getx, x)), ctx.mkMul(a, ctx.mkApp(gety, x)))
            ),
            1, null, null, null, null
        ));

        // Define the add operation
        var add = ctx.mkFuncDecl("add", new Sort[]{Z2, Z2}, Z2);
        s.add(ctx.mkForall(
            new Expr[] {x, y},
            ctx.mkEq(
                ctx.mkApp(add, x, y),
                ctx.mkApp(getZ2, ctx.mkAdd(ctx.mkApp(getx, x), ctx.mkApp(getx, y)), ctx.mkAdd(ctx.mkApp(gety, x), ctx.mkApp(gety, y)))
            ),
            1, null, null, null, null
        ));


        // Symmetric
        s.add(
            ctx.mkForall(
                new Expr[] {x, y},
                ctx.mkEq(ctx.mkApp(inProd, x, y), ctx.mkApp(inProd, y, x)),
                1, null, null, null, null
            )
        );

        // Linearity
        s.add(
            ctx.mkForall(
                new Expr[] {x, y, z, a, b},
                ctx.mkEq(
                    ctx.mkApp(
                        inProd,
                        ctx.mkApp(
                            add,
                            ctx.mkApp(scale, a, x),
                            ctx.mkApp(scale, b, y)
                        ),
                        z
                    ),
                    ctx.mkAdd(
                        ctx.mkMul(a, ctx.mkApp(inProd, x, z)),
                        ctx.mkMul(b, ctx.mkApp(inProd, y, z))
                    )
                ),
                1, null, null, null, null
            )
        );

        // Positive-definiteness
        s.add(
            ctx.mkForall(
                new Expr[] {x},
                ctx.mkImplies(
                    ctx.mkEq(ctx.mkApp(inProd, x, x), ctx.mkInt(0)),
                    ctx.mkEq(x, ctx.mkApp(getZ2, ctx.mkInt(0), ctx.mkInt(0)))
                ),
                1, null, null, null, null
            )
        );

        Util.solve(s);

        // Restrict to [-5, 5]^2
        // Util.solve(
        //     s, 
        //     new BoolExpr[] {
        //         ctx.mkAnd(
        //             ctx.mkGe(ctx.mkApp(getx, x), ctx.mkInt(-5)),
        //             ctx.mkLe(ctx.mkApp(getx, x), ctx.mkInt(5)),
        //             ctx.mkGe(ctx.mkApp(gety, x), ctx.mkInt(-5)),
        //             ctx.mkLe(ctx.mkApp(gety, x), ctx.mkInt(5))
        //         )
        //     }
        // );

        ctx.close();
    }

    public static void main(String[] args) {

        basicString();

        basicEnum();

        basicTuple();
    }
}
