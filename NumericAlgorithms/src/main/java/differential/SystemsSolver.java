/************************************************************************
 * Copyright 2019 VMware, Inc.  All rights reserved. VMware Confidential
 ***********************************************************************/
package differential;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class SystemsSolver {

   /**
    * @param args
    */
   public static void main(String[] args) {
      List<Double> init = new ArrayList<>();
      init.add(1.5);
      init.add(-2.5);

      List<BiFunction<Double, List<Double>, Double>> f = new ArrayList<>();
      f.add((t, x) -> x.get(1));
      f.add((t, x) -> -4*x.get(1)-3*x.get(0));

      double x1 = 1.0;
      double t0 = 0.0;
      double h = 0.025;
      int n = (int)(x1/h);
      printResult(calc(init, t0, h, n, (values, t) -> calcEulerStep(values, t, h, f)), n);
      printResult(calc(init, t0, h, n, (values, t) -> calcRungeKuttaStep(values, t, h, f)), n);
   }

   public static List<Double> calc(List<Double> init, double t0, double h, int n, BiFunction<List<Double>, Double, List<Double>> calculator) {
      List<Double> values = init;

      for(int i=0; i<n; i++) {
         values = calculator.apply(values, t0 + (double)i*h);
      }
      return values;
   }

   public static List<Double> calcEulerStep(List<Double> values, double t, double h, List<BiFunction<Double, List<Double>, Double>> f) {
      List<Double> newValues = new ArrayList<>(values.size());

      for(int i=0; i<values.size(); i++) {
         newValues.add(values.get(i) + h*f.get(i).apply(t, values));
      }
      return newValues;
   }

   public static List<Double> calcRungeKuttaStep(List<Double> values, double t, double h, List<BiFunction<Double, List<Double>, Double>> f) {
      List<Double> newValues = new ArrayList<>(values.size());
      double [][] k = new double[values.size()][4];
      for(int i=0; i<values.size(); i++) {
         k[i][0] = h*f.get(i).apply(t, values);
      }
      for(int i=0; i<values.size(); i++) {
         k[i][1] = h*f.get(i).apply(t + h/2.0, IntStream.range(0, values.size()).mapToObj((j) -> values.get(j) + 0.5*k[j][0]).collect(Collectors.toList()));
      }
      for(int i=0; i<values.size(); i++) {
         k[i][2] = h*f.get(i).apply(t + h/2.0, IntStream.range(0, values.size()).mapToObj((j) -> values.get(j) + 0.5*k[j][1]).collect(Collectors.toList()));
      }
      for(int i=0; i<values.size(); i++) {
         k[i][3] = h*f.get(i).apply(t + h, IntStream.range(0, values.size()).mapToObj((j) -> values.get(j) + k[j][1]).collect(Collectors.toList()));
      }
      for(int i=0; i<values.size(); i++) {
         newValues.add(values.get(i) + (k[i][0]+2.0*k[i][1]+2.0*k[i][2]+k[i][3])/6.0);
      }
      return newValues;
   }

   public static void printResult(List<Double> values, int n) {
      System.out.println("Result for n = " + n + ":");
      for(int i=0; i<values.size(); i++) {
         System.out.println("x" + i + ": " + String.format("%.5g%n",values.get(i)));
      }
   }
}
