import java.util.HashMap;
import com.microsoft.z3.*;

/**
 * #3 Bit Vectors
 */

public class BitVector {

    static void basicTest() {

        // x = BitVec('x', 16)
        // y = BitVec('y', 16)
        // print x + 2
        // # Internal representation
        // print (x + 2).sexpr()

        // # -1 is equal to 65535 for 16-bit integers 
        // print simplify(x + y - 1)


        Util.log("BasicTest");
        var ctx = new Context();

        var x = ctx.mkBVConst("x", 16);
        var y = ctx.mkBVConst("y", 16);

        // 1
        var exp = ctx.mkBVAdd(x, ctx.mkBV(2, 16));
        System.out.println(exp.getSExpr());

        // 2
        System.out.println(
            ctx.mkBVAdd(
                ctx.mkBVAdd(x, y),
                ctx.mkBV(-1, 16)
            ).simplify().getSExpr()
        );

        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void basicDump() {
        Util.log("BasicDump");
        var ctx = new Context();
        var s = ctx.mkSolver();

        var x = ctx.mkBVConst("x", 16);

        s.add(ctx.mkEq(
            ctx.mkBVAdd(x, ctx.mkBV(1, 16)),
            ctx.mkBV(0, 16)
        ));

        try {
            Util.dumpBitBlast(ctx, s, "basicDump.smt2", false);
        } catch (Exception e) {}
        Util.solve(s);

        ctx.close();
    }

    @SuppressWarnings("unchecked")
    static void divHack(int width) {
        Util.log(String.format("DivisionHacks (%d)", width));
        var options = new HashMap<String, String>();
        var ctx = new Context(options);

        /* With GCC, this function
         *
         * int f(int x) {return x/3;}
         * 
         * compiles to this:
         * 
         * 0000000000000000 <f>:
         * 0:   55                      push   %rbp
         * 1:   48 89 e5                mov    %rsp,%rbp
         * 4:   89 7d fc                mov    %edi,-0x4(%rbp)
         * 7:   8b 4d fc                mov    -0x4(%rbp),%ecx
         * a:   ba 56 55 55 55          mov    $0x55555556,%edx
         * f:   89 c8                   mov    %ecx,%eax
         * 11:   f7 ea                   imul   %edx
         * 13:   89 c8                   mov    %ecx,%eax
         * 15:   c1 f8 1f                sar    $0x1f,%eax
         * 18:   29 c2                   sub    %eax,%edx
         * 1a:   89 d0                   mov    %edx,%eax
         * 1c:   5d                      pop    %rbp
         * 1d:   c3                      retq   
         */

        var s = ctx.mkSolver();
        var x = ctx.mkBVConst("x", width);

        // Note: use zero-extend, sign-extend and extract to
        // change bitwidth.

        var magic = ((1l<<width)-1)/3 + 1;

        // s.add(ctx.mkEq(
        //     // Target
        //     ctx.mkBVSDiv(x, ctx.mkBV(3, width)),

        //     // Hack
        //     ctx.mkBVSub(
        //         ctx.mkExtract(
        //             2*width-1, width, 
        //             // Extend before multiplication; sign-ext for imul
        //             ctx.mkBVMul(ctx.mkSignExt(width, x), ctx.mkBV(magic, 2*width))
        //         ), // ...and extract afterwards
        //         ctx.mkBVASHR(x, ctx.mkBV(width-1, width))
        //     )
        // )); 

        // This version is quicker, and yields much shorter bitblasted results.
        // Takeaway: BVDiv is expensive?
        var diff = ctx.mkBVSub(
            x,
            ctx.mkBVMul(
                ctx.mkBV(3, width), 
                ctx.mkBVSub(
                    ctx.mkExtract(
                        2*width-1, width, 
                        // Extend before multiplication; sign-ext for imul
                        ctx.mkBVMul(ctx.mkSignExt(width, x), ctx.mkBV(magic, 2*width))
                    ), // ...and extract afterwards
                    ctx.mkBVASHR(x, ctx.mkBV(width-1, width))
                )
            )
        );
        s.add(ctx.mkITE(
            ctx.mkBVSGE(x, ctx.mkBV(0, width)),
            ctx.mkAnd(ctx.mkBVSGE(diff, ctx.mkBV(0, width)), ctx.mkBVSLE(diff, ctx.mkBV(2, width))),
            ctx.mkAnd(ctx.mkBVSGE(diff, ctx.mkBV(-2, width)), ctx.mkBVSLE(diff, ctx.mkBV(0, width)))
        )); 


        // try {
        //     Util.dumpBitBlast(ctx, s, "divHack.smt2", true);
        // }
        // catch(Exception e) {}

        Util.prove(ctx, s);
        ctx.close();
    }


    public static void main(String[] args) {

        basicTest();

        basicDump();

        // This verification scales poorly!
        // width    time/ms
        // 8        69
        // 9        47
        // 10       123
        // 11       132
        // 12       239
        // 13       574
        // 14       1108
        // 15       3382
        // 16       6079
        // 17       17561
        // 18       38628
        // 19       92758
        // 20       210568
        for (int width = 16; width <= 16; width++) {
            long startTime = System.nanoTime();
            divHack(width);
            long endTime = System.nanoTime();

            long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
            System.out.println(String.format("[Time] %d ms", duration/1000000));
        }
    }
}
