/**
 * Copyright 2015 NICTA
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.n1analytics.paillier;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import static com.n1analytics.paillier.TestConfiguration.CONFIGURATIONS;
import static com.n1analytics.paillier.TestUtil.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@Category(SlowTests.class)
public class FuzzTest {
  private PaillierContext context;
  private PaillierPrivateKey privateKey;

  static private int maxIteration = 100;

  @Parameterized.Parameters
  public static Collection<Object[]> configurations() {
    Collection<Object[]> configurationParams = new ArrayList<>();

    for(TestConfiguration[] confs : CONFIGURATIONS) {
      for(TestConfiguration conf : confs) {
        configurationParams.add(new Object[]{conf});
      }
    }
    return configurationParams;
  }

  public FuzzTest(TestConfiguration conf) {
    context = conf.context();
    privateKey = conf.privateKey();
  }

  @Test
  public void fuzzDoubleMixOperations1() throws Exception {
    double a, b, c, plainResult, decodedResult, tolerance;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = randomFiniteDouble();
      b = randomFiniteDouble();
      c = randomFiniteDouble();

      // Check if the numbers are "close enough"
      double minVal = a - (a * EPSILON), maxVal = a + (a * EPSILON);
      if((b > maxVal || b < minVal) && (c > maxVal || c < minVal))
        continue;

      plainResult = (a + b) * c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);

      encryptedResult = (ciphertextA.add(ciphertextB)).multiply(c);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeDouble();

        double absValue = Math.abs(plainResult);
        if(absValue == 0.0 || absValue > 1.0) {
          tolerance = EPSILON * Math.pow(2.0, Math.getExponent(plainResult));
        } else {
          tolerance = EPSILON;
        }

        assertEquals(plainResult, decodedResult, tolerance);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzDoubleMixOperations2() throws Exception {
    double a, b, c, plainResult, decodedResult, tolerance;
    EncryptedNumber ciphertextA, ciphertextC, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = randomFiniteDouble();
      b = randomFiniteDouble();
      c = randomFiniteDouble();

      // Check if the numbers are "close enough"
      double minVal = a - (a * EPSILON), maxVal = a + (a * EPSILON);
      if((b > maxVal || b < minVal) && (c > maxVal || c < minVal))
        continue;

      plainResult = a * b + c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextC = context.encrypt(c);

      encryptedResult = ciphertextA.multiply(b).add(ciphertextC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeDouble();

        double absValue = Math.abs(plainResult);
        if(absValue == 0.0 || absValue > 1.0) {
          tolerance = EPSILON * Math.pow(2.0, Math.getExponent(plainResult));
        } else {
          tolerance = EPSILON;
        }

        assertEquals(plainResult, decodedResult, tolerance);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzDoubleMixOperations3() throws Exception {
    double a, b, c, d, plainResult, decodedResult, tolerance;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;
    EncodedNumber encodedC, encodedD;

    for(int i = 0; i < maxIteration; i++) {
      a = randomFiniteDouble();
      b = randomFiniteDouble();
      c = randomFiniteDouble();
      d = randomFiniteDouble();

      // Check if the numbers are "close enough"
      double minVal = a - (a * EPSILON), maxVal = a + (a * EPSILON);
      if((b > maxVal || b < minVal) && (c > maxVal || c < minVal) && (d > maxVal || d < minVal))
        continue;

      plainResult = a + b * (c + d);

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || d < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      encodedC = context.encode(c);
      encodedD = context.encode(d);

      encryptedResult = ciphertextA.add(ciphertextB.multiply(encodedC.add(encodedD)));

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeDouble();

        double absValue = Math.abs(plainResult);
        if(absValue == 0.0 || absValue > 1.0) {
          tolerance = EPSILON * Math.pow(2.0, Math.getExponent(plainResult));
        } else {
          tolerance = EPSILON;
        }

        assertEquals(plainResult, decodedResult, tolerance);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzDoubleMixOperations4() throws Exception {
    double a, b, c, d, plainResult, decodedResult, tolerance;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;
    EncodedNumber encodedC;

    for(int i = 0; i < maxIteration; i++) {
      a = randomFiniteDouble();
      b = randomFiniteDouble();
      c = randomFiniteDouble();
      d = randomFiniteDouble();

      // Check if the numbers are "close enough"
      double minVal = a - (a * EPSILON), maxVal = a + (a * EPSILON);
      if((b > maxVal || b < minVal) && (c > maxVal || c < minVal))
        continue;

      if(Double.isInfinite(1 / d) || Double.isNaN(1/d)) {
        continue;
      }

      plainResult = (a + (b * c)) / d;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || d < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      encodedC = context.encode(c);

      encryptedResult = ciphertextA.add(ciphertextB.multiply(encodedC)).divide(d);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeDouble();

        double absValue = Math.abs(plainResult);
        if(absValue == 0.0 || absValue > 1.0) {
          tolerance = EPSILON * Math.pow(2.0, Math.getExponent(plainResult));
        } else {
          tolerance = EPSILON;
        }

        assertEquals(plainResult, decodedResult, tolerance);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzDoubleMixOperations5() throws Exception {
    double a, b, c, plainResult, decodedResult, tolerance;
    EncryptedNumber ciphertextA, ciphertextB, ciphertextC, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = randomFiniteDouble();
      b = randomFiniteDouble();
      c = randomFiniteDouble();

      // Check if the numbers are "close enough"
      double minVal = a - (a * EPSILON), maxVal = a + (a * EPSILON);
      if((b > maxVal || b < minVal) && (c > maxVal || c < minVal))
        continue;

      plainResult = a + b + c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      ciphertextC = context.encrypt(c);

      encryptedResult = ciphertextA.add(ciphertextB).add(ciphertextC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeDouble();

        double absValue = Math.abs(plainResult);
        if(absValue == 0.0 || absValue > 1.0) {
          tolerance = EPSILON * Math.pow(2.0, Math.getExponent(decodedResult));
        } else {
          tolerance = EPSILON;
        }

        assertEquals(plainResult, decodedResult, tolerance);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzDoubleMixOperations6() throws Exception {
    double a, b, c, plainResult, decodedResult, tolerance;
    EncryptedNumber ciphertextA, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = randomFiniteDouble();
      b = randomFiniteDouble();
      c = randomFiniteDouble();

      // Check if the numbers are "close enough"
      double minVal = a - (a * EPSILON), maxVal = a + (a * EPSILON);
      if((b > maxVal || b < minVal) && (c > maxVal || c < minVal))
        continue;

      plainResult = a * b * c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);

      encryptedResult = ciphertextA.multiply(b).multiply(c);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeDouble();

        double absValue = Math.abs(plainResult);
        if(absValue == 0.0 || absValue > 1.0) {
          tolerance = EPSILON * Math.pow(2.0, Math.getExponent(plainResult));
        } else {
          tolerance = EPSILON;
        }

        assertEquals(plainResult, decodedResult, tolerance);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzLongMixOperations1() throws Exception {
    long a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = random.nextLong();
      b = random.nextLong();
      c = random.nextLong();

      plainResult = (a + b) * c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);

      encryptedResult = (ciphertextA.add(ciphertextB)).multiply(c);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeLong();
        assertEquals(plainResult, decodedResult);
      } catch (ArithmeticException e) {
      } catch (DecodeException e) {
      }
    }
  }

  @Test
  public void fuzzLongMixOperations2() throws Exception {
    long a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextC, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = random.nextLong();
      b = random.nextLong();
      c = random.nextLong();

      plainResult = a * b + c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextC = context.encrypt(c);

      encryptedResult = ciphertextA.multiply(b).add(ciphertextC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeLong();
        assertEquals(plainResult, decodedResult);
      } catch (ArithmeticException e) {
      } catch (DecodeException e) {
      }
    }
  }

  @Test
  public void fuzzLongMixOperations3() throws Exception {
    long a, b, c, d, plainResult, decodedResult;
    EncodedNumber encodedC, encodedD;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = random.nextLong();
      b = random.nextLong();
      c = random.nextLong();
      d = random.nextLong();

      plainResult = a + b * (c + d);

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || d < 0 || plainResult < 0)) {
        continue;
      }

//      if(!isValid(context, BigInteger.valueOf(c + d)))
//        continue;

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      encodedC = context.encode(c);
      encodedD = context.encode(d);

      encryptedResult = ciphertextA.add(ciphertextB.multiply(encodedC.add(encodedD)));

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeLong();
        assertEquals(plainResult, decodedResult);
      } catch (ArithmeticException e) {
      } catch (DecodeException e) {
      }
    }
  }

  @Test
  public void fuzzLongMixOperations5() throws Exception {
    long a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextB, ciphertextC, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = random.nextLong();
      b = random.nextLong();
      c = random.nextLong();

      plainResult = a + b + c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      ciphertextC = context.encrypt(c);

      encryptedResult = ciphertextA.add(ciphertextB).add(ciphertextC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeLong();
        assertEquals(plainResult, decodedResult);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzLongMixOperations6() throws Exception {
    long a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = random.nextLong();
      b = random.nextLong();
      c = random.nextLong();

      plainResult = a * b * c;

      if(context.isUnsigned() && (a < 0 || b < 0 || c < 0 || plainResult < 0)) {
        continue;
      }

      ciphertextA = context.encrypt(a);

      encryptedResult = ciphertextA.multiply(b).multiply(c);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeLong();
        assertEquals(plainResult, decodedResult);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzBigIntegerMixOperations1() throws Exception {
    BigInteger a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;
    EncodedNumber encodedC;

    for(int i = 0; i < maxIteration; i++) {
      a = new BigInteger(context.getPrecision(), random);
      b = new BigInteger(context.getPrecision(), random);
      c = new BigInteger(context.getPrecision(), random);

      plainResult = (a.add(b)).multiply(c);

      if(!isValid(context, a) || !isValid(context, b) || !isValid(context, c) || !isValid(context, plainResult))
        continue;

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      encodedC = context.encode(c);

      encryptedResult = (ciphertextA.add(ciphertextB)).multiply(encodedC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeBigInteger();
        assertEquals(plainResult.toString(), decodedResult.toString());
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzBigIntegerMixOperations2() throws Exception {
    BigInteger a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextC, encryptedResult;
    EncodedNumber encodedB;

    for(int i = 0; i < maxIteration; i++) {
      a = new BigInteger(context.getPrecision(), random);
      b = new BigInteger(context.getPrecision(), random);
      c = new BigInteger(context.getPrecision(), random);

      plainResult = a.multiply(b).add(c);

      if(!isValid(context, a) || !isValid(context, b) || !isValid(context, c) || !isValid(context, plainResult))
        continue;

      ciphertextA = context.encrypt(a);
      encodedB = context.encode(b);
      ciphertextC = context.encrypt(c);

      encryptedResult = ciphertextA.multiply(encodedB).add(ciphertextC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeBigInteger();
        assertEquals(plainResult, decodedResult);
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzBigIntegerMixOperations3() throws Exception {
    BigInteger a, b, c, d, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextB, encryptedResult;
    EncodedNumber encodedC, encodedD;

    for(int i = 0; i < maxIteration; i++) {
      a = new BigInteger(context.getPrecision(), random);
      b = new BigInteger(context.getPrecision(), random);
      c = new BigInteger(context.getPrecision(), random);
      d = new BigInteger(context.getPrecision(), random);

      plainResult = a.add(b.multiply(c.add(d)));

      if(!isValid(context, a) || !isValid(context, b) || !isValid(context, c) || !isValid(context, d)
              || !isValid(context, plainResult))
        continue;

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      encodedC = context.encode(c);
      encodedD = context.encode(d);

      encryptedResult = ciphertextA.add(ciphertextB.multiply(encodedC.add(encodedD)));

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeBigInteger();
        assertEquals(plainResult, decodedResult);
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzBigIntegerMixOperations5() throws Exception {
    BigInteger a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, ciphertextB, ciphertextC, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = new BigInteger(context.getPrecision(), random);
      b = new BigInteger(context.getPrecision(), random);
      c = new BigInteger(context.getPrecision(), random);

      plainResult = a.add(b).add(c);

      if(!isValid(context, a) || !isValid(context, b) || !isValid(context, c) || !isValid(context, plainResult))
        continue;

      ciphertextA = context.encrypt(a);
      ciphertextB = context.encrypt(b);
      ciphertextC = context.encrypt(c);

      encryptedResult = ciphertextA.add(ciphertextB).add(ciphertextC);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeBigInteger();
        assertEquals(plainResult, decodedResult);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

  @Test
  public void fuzzBigIntegerMixOperations6() throws Exception {
    BigInteger a, b, c, plainResult, decodedResult;
    EncryptedNumber ciphertextA, encryptedResult;

    for(int i = 0; i < maxIteration; i++) {
      a = new BigInteger(context.getPrecision(), random);
      b = new BigInteger(context.getPrecision(), random);
      c = new BigInteger(context.getPrecision(), random);

      plainResult = a.multiply(b).multiply(c);

      if(!isValid(context, a) || !isValid(context, b) || !isValid(context, c) || !isValid(context, plainResult))
        continue;

      ciphertextA = context.encrypt(a);

      encryptedResult = ciphertextA.multiply(b).multiply(c);

      try {
        decodedResult = privateKey.decrypt(encryptedResult).decodeBigInteger();
        assertEquals(plainResult, decodedResult);
      } catch (DecodeException e) {
      } catch (ArithmeticException e) {
      }
    }
  }

}
