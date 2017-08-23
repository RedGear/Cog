package com.redgear.cog.core.impl;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.Function;

public class CasifierTest {

    private static final String[] inputs = {"this_is_a_test", "This_Is_A_Test", "thisIsATest", "ThisIsATest", "this-is-a-test", "THIS-IS-A-TEST", "this is a test", "This Is A Test"};


    private void runTest(String expected, Function<String, String> func) {
        for(String input : inputs) {
            Assert.assertEquals(expected, func.apply(input));
        }
    }


    @Test
    public void camelCaseTest() {
        runTest("thisIsATest", Caseifier::camelCase);
    }

    @Test
    public void pascalCaseTest() {
        runTest("ThisIsATest", Caseifier::pascalCase);
    }

    @Test
    public void snakeCaseTest() {
        runTest("this_is_a_test", Caseifier::snakeCase);
    }

    @Test
    public void capsCaseTest() {
        runTest("THIS_IS_A_TEST", Caseifier::capsCase);
    }

    @Test
    public void kebabCaseTest() {
        runTest("this-is-a-test", Caseifier::kebabCase);
    }

    @Test
    public void trainCaseTest() {
        runTest("This-Is-A-Test", Caseifier::trainCase);
    }

    @Test
    public void sentenceCaseTest() {
        runTest("This is a test", Caseifier::sentenceCase);
    }

    @Test
    public void titleCaseTest() {
        runTest("This Is A Test", Caseifier::titleCase);
    }

}
