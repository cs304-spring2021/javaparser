package com.github.javaparser.symbolsolver;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.jupiter.api.Test;

import java.util.List;

public class Issue3200Test {

    @Test
    public void test3200() {
        ParserConfiguration config = new ParserConfiguration();
        CombinedTypeSolver cts = new CombinedTypeSolver();
        cts.add(new ReflectionTypeSolver(false));
        config.setSymbolResolver(new JavaSymbolSolver(cts));
        StaticJavaParser.setConfiguration(config);

        String str = "public class Test {\n" +
                "    private void bad() {\n" +
                "        Test test = new Test();\n" +
                "        test.setRunnable(\"\", new Runnable() {\n" +
                "            @Override\n" +
                "            public void run() {\n" +
                "                getContext(Test.this);\n" +
                "            }\n" +
                "        });\n" +
                "    }\n" +
                "\n" +
                "    private void getContext(Test test) {\n" +
                "    }\n" +
                "\n" +
                "    private void setRunnable(String str, Runnable runnable) {\n" +
                "    }\n" +
                "}";
        CompilationUnit cu = StaticJavaParser.parse(str);
        List<MethodCallExpr> mce = cu.findAll(MethodCallExpr.class);
        mce.forEach(m -> System.out.println(m.resolve().getQualifiedSignature()));
    }


    @Test
    public void test1() {
        ParserConfiguration config = new ParserConfiguration();
        CombinedTypeSolver cts = new CombinedTypeSolver();
        cts.add(new ReflectionTypeSolver(false));
        config.setSymbolResolver(new JavaSymbolSolver(cts));
        StaticJavaParser.setConfiguration(config);

        String str = "public class Test {\n" +
                "    void getContext(Test test) {  }\n" +
                "    {\n" +
                "        new Test () {\n" +
                "            {\n" +
                "                getContext(Test.this);\n" +
                "            }\n" +
                "        };\n" +
                "    }\n" +
                "}";
        CompilationUnit cu = StaticJavaParser.parse(str);
        MethodCallExpr mce = cu.findFirst(MethodCallExpr.class).get();
        ResolvedMethodDeclaration rmd = mce.resolve();
        String sig = rmd.getQualifiedSignature();
        System.out.println(sig);
    }

    @Test
    public void test2() {
        ParserConfiguration config = new ParserConfiguration();
        CombinedTypeSolver cts = new CombinedTypeSolver();
        cts.add(new ReflectionTypeSolver(false));
        config.setSymbolResolver(new JavaSymbolSolver(cts));
        StaticJavaParser.setConfiguration(config);

        String str = "public class Test {\n" +
                "    class Inner { }" +
                "    void getContext(Test test) {  }\n" +
                "    {\n" +
                "        new Inner () {\n" +
                "            {\n" +
                "                getContext(Test.this);\n" +
                "            }\n" +
                "        };\n" +
                "    }\n" +
                "}";
        CompilationUnit cu = StaticJavaParser.parse(str);

        ClassOrInterfaceDeclaration t = cu.findFirst(ClassOrInterfaceDeclaration.class).get();
        System.out.println(t.getFullyQualifiedName().get());
        System.out.println(t.getFullyQualifiedName().get().endsWith("Test"));
        MethodCallExpr mce = cu.findFirst(MethodCallExpr.class).get();
        ResolvedMethodDeclaration rmd = mce.resolve();
        String sig = rmd.getQualifiedSignature();
        System.out.println(sig);
    }

    @Test
    public void test3() {
        ParserConfiguration config = new ParserConfiguration();
        CombinedTypeSolver cts = new CombinedTypeSolver();
        cts.add(new ReflectionTypeSolver(false));
        config.setSymbolResolver(new JavaSymbolSolver(cts));
        StaticJavaParser.setConfiguration(config);

        String str = "public class Test {\n" +
                "    class Inner { }" +
                "    void getContext(Test test) {  }\n" +
                "    {\n" +
                "        new Inner () {\n" +
                "            {\n" +
                "                getContext(new Test());\n" +
                "            }\n" +
                "        };\n" +
                "    }\n" +
                "}";
        CompilationUnit cu = StaticJavaParser.parse(str);
        MethodCallExpr mce = cu.findFirst(MethodCallExpr.class).get();
        ResolvedMethodDeclaration rmd = mce.resolve();
        String sig = rmd.getQualifiedSignature();
        System.out.println(sig);
    }

    class Test3200 {
        class Inner { }
        void getContext(Test3200 test) { }
        {
            new Inner() {
                {
                    getContext(Test3200.this);
                }
            };
        }
    }
}

