import com.microsoft.z3.*;

/**
 * #5 Quantifiers
 */

public class Quantifier {

    static void quantifierExample() {
        Util.log("QuantifierExample");
        var ctx = new Context();
        var s = ctx.mkSolver();

        // The Java API for quantifiers is especially obscure
        // Context.mkForall():
        // * Sort[]             sorts
        // * Symbol[]           names (note that these are specified in reverse order compared to boundConstants)
        // * Expr<BoolSort>     body
        // * int                weight (used to indicate priority for qunatifier instantiation; use 1 as default)
        // * Pattern[]          patterns
        // * Expr<?>[]          no_patterns
        // * Symbol[]           quantifierID
        // * Symbol[]           skolemID
        // --------------------------------
        // * Expr<?>[]          boundConstants (essentially, invoke mkConst beforehand)
        // * Expr<BoolSort>     body
        // * int                weight (used to indicate priority for qunatifier instantiation; use 1 as default)
        // * Pattern[]          patterns (nullable)
        // * Expr<?>[]          no_patterns (nullable)
        // * Symbol[]           quantifierID (nullable)
        // * Symbol[]           skolemID (nullable)


        IntSort[] types = new IntSort[3];
        IntExpr[] xs = new IntExpr[3];
        Symbol[] names = new Symbol[3];
        IntExpr[] vars = new IntExpr[3];

        for (int j = 0; j < 3; j++)
        {
            types[j] = ctx.getIntSort();
            names[j] = ctx.mkSymbol("x_" + Integer.toString(j));
            xs[j] = (IntExpr) ctx.mkConst(names[j], types[j]);
            vars[j] = (IntExpr) ctx.mkBound(2 - j, types[j]); // <-- vars
                                                              // reversed!
        }

        Expr<BoolSort> body_vars = ctx.mkAnd(
                ctx.mkEq(ctx.mkAdd(vars[0], ctx.mkInt(1)), ctx.mkInt(2)),
                ctx.mkEq(ctx.mkAdd(vars[1], ctx.mkInt(2)),
                        ctx.mkAdd(vars[2], ctx.mkInt(3))));

        Expr<BoolSort> body_const = ctx.mkAnd(
                ctx.mkEq(ctx.mkAdd(xs[0], ctx.mkInt(1)), ctx.mkInt(2)),
                ctx.mkEq(ctx.mkAdd(xs[1], ctx.mkInt(2)),
                        ctx.mkAdd(xs[2], ctx.mkInt(3))));

        Expr<BoolSort> x = ctx.mkForall(types, names, body_vars, 1, null, null,
                ctx.mkSymbol("Q1"), ctx.mkSymbol("skid1"));
        System.out.println("Quantifier X: " + x.toString());

        Expr<BoolSort> y = ctx.mkForall(xs, body_const, 1, null, null,
                ctx.mkSymbol("Q2"), ctx.mkSymbol("skid2"));
        System.out.println("Quantifier Y: " + y.toString());

        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void basicQuantifier() {
        Util.log("BasicQuantifier");
        var ctx = new Context();
        var s = ctx.mkSolver();

        var x = ctx.mkIntConst("x");

        // Forall Int x, x**2 >= 0
        s.add(ctx.mkForall(
            new Expr[] {x}, // boundConstants
            ctx.mkGe(ctx.mkPower(x, ctx.mkInt(2)), ctx.mkInt(0)), // body
            1, // weight
            null, null, null, null
        ));
        Util.prove(ctx, s);

        s.reset();

        // Forall Int x, Exists 0 such that 0 <= abs(x)
        var abs_x = ctx.mkITE(
            ctx.mkGe(x, ctx.mkInt(0)),
            x,
            ctx.mkSub(ctx.mkInt(0), x)
        );
        var _0 = ctx.mkIntConst("0");
        Expr<BoolSort> exists = ctx.mkExists(
            new Expr[] {_0}, // boundConstants
            ctx.mkGe(abs_x, _0), // body
            1, null, null, null, null
        );
        s.add(ctx.mkForall(
            new Expr[] {x}, 
            exists,
            1, null, null, null, null
        ));
        Util.prove(ctx, s);

        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void checkInheritance() {
        // Construct a system with Single Inheritance
        // that is a partial order (which is also monotone for
        // array of objects), where there exists a root type.

        Util.log("CheckInheritance");
        var ctx = new Context();
        var s = ctx.mkSolver();

        // We deal with "Type"s in this system
        Sort Type = ctx.mkUninterpretedSort("Type");
        // Inheritance is described with "sub-type" relation
        var sub_type = ctx.mkFuncDecl("sub_type", new Sort[]{Type, Type}, ctx.getBoolSort());
        // There is also an array relation
        var array_of = ctx.mkFuncDecl("array-of", Type, Type);

        var x = ctx.mkConst("x", Type);
        var y = ctx.mkConst("y", Type);
        var z = ctx.mkConst("z", Type);
        
        // This relation is:
        s.add(
            ctx.mkForall(
                new Expr[] {x}, 
                ctx.mkApp(sub_type, x, x), 
                1, null, null, null, null
            )
        ); // Reflexive
        s.add(
            ctx.mkForall(
                new Expr[] {x, y, z},
                ctx.mkImplies(
                    ctx.mkAnd(
                        ctx.mkApp(sub_type, x, y),
                        ctx.mkApp(sub_type, y, z)
                    ),
                    ctx.mkApp(sub_type, x, z)
                ),
                1, null, null, null, null
            )
        ); // Transitive
        s.add(
            ctx.mkForall(
                new Expr[] {x, y},
                ctx.mkImplies(
                    ctx.mkAnd(
                        ctx.mkApp(sub_type, x, y),
                        ctx.mkApp(sub_type, y, x)
                    ),
                    ctx.mkEq(x, y)
                ),
                1, null, null, null, null
            )
        ); // Anti-symmetric

        // The inheritance is single. Namely, x -> y and x -> z implies y->z or z->y
        s.add(
            ctx.mkForall(
                new Expr[] {x, y ,z},
                ctx.mkImplies(
                    ctx.mkAnd(
                        ctx.mkApp(sub_type, x, y),
                        ctx.mkApp(sub_type, x, z)
                    ),
                    ctx.mkOr(
                        ctx.mkApp(sub_type, y, z),
                        ctx.mkApp(sub_type, z, y)
                    )
                ),
                1, null, null, null, null
            )
        );

        // Finally, if x -> y, then array-of(x) -> array-of(y)
        s.add(
            ctx.mkForall(
                new Expr[] {x, y},
                ctx.mkImplies(
                    ctx.mkApp(sub_type, x, y),
                    ctx.mkApp(
                        sub_type,
                        ctx.mkApp(array_of, x),
                        ctx.mkApp(array_of, y)
                    )
                ),
                1, null, null, null, null   
            )
        );

        // Let's prove there exists a root type
        var root = ctx.mkConst("root", Type);
        s.add(
            ctx.mkForall(
                new Expr[] {x},
                ctx.mkApp(sub_type, x, root),
                1, null, null, null, null
            )
        );

        Util.solve(s);
        ctx.close();
    }
    public static void main(String[] args) {

        quantifierExample();

        basicQuantifier();

        checkInheritance();
    }
}
