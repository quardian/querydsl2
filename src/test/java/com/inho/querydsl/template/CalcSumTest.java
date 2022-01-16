package com.inho.querydsl.template;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class CalcSumTest {
    Calculator calculator;
    String numFilepath;

    @BeforeEach
    public void beforeEach()
    {
        calculator = new Calculator();
        URL resource = getClass().getResource("numbers.txt");
        numFilepath = resource.getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException{
        int sum = calculator.calcSum(this.numFilepath);
        assertThat(sum).isEqualTo(10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException{
        int sum = calculator.calcMultiply(this.numFilepath);
        assertThat(sum).isEqualTo(24);
    }
}
