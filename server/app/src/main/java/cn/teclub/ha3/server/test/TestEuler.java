package cn.teclub.ha3.server.test;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings({"WeakerAccess", "unused"})
public class TestEuler {
    public static void main(String[] args) {
        System.out.println("Hello Momo & Lanlan!");

        TestEuler te = new TestEuler();
        te.p01();   // result: 233168
        te.p23();   // result: 4179871
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////
    // P01
    // ref: https://projecteuler.net/problem=1
    ///////////////////////////////////////////////////////////////////////////////////////////////


    void p01(){
        final long MAX = 1000;
        long res = 0;
        for(int i=0; i < MAX; i++ ) {
           if(i % 3 == 0 || i % 5 == 0){
               res += i;
           }
        }
        System.out.println("P01: result: " + res);
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    // P23
    // ref: https://projecteuler.net/problem=23
    ///////////////////////////////////////////////////////////////////////////////////////////////

    private int[] properDivisors(int v) {
        ArrayList<Integer> list =new ArrayList<>();
        for(int i=1; i<v; i++) {
            if( v% i == 0) {
                list.add(i);
            }
        }
        int[] res = new int[list.size()];
        int j=0;
        for(Integer it: list) {
            res[j++] = it;
        }
        return res;
    }


    private int properDivisorSum(int v) {
        int[] pd_array = properDivisors(v);
        int sum = 0;
        for(int i : pd_array) {
           sum += i;
        }
        return sum;
    }


    private boolean isAbundant(int v){
        return (properDivisorSum(v) > v) ;
    }


    private final static int ABUNDANT_MIN = 12;
    private final static int P23_MAX      = 28123;
    private final HashMap<Integer, String>  p23Map = new HashMap<>();

    private ArrayList<Integer> allAbundantUnder(int v) {
        ArrayList<Integer> list = new ArrayList<>();
        for(int i=ABUNDANT_MIN; i < v; i++) {
            if(isAbundant(i)) {
               list.add(i);
               p23Map.put(i, "OK");
            }
        }
        final int N = list.size();
        System.out.println("INF: num of abundant: " + N );
        System.out.println("--- [0]" + list.get(0));
        System.out.println("--- [1]" + list.get(1));
        System.out.println("--- [2]" + list.get(2));
        System.out.println("--- [n-2]" + list.get(N-2));
        System.out.println("--- [n-1]" + list.get(N-1));

        return list;
    }


    private boolean isOurDigital(ArrayList<Integer> list, int v ){
        for(int i: list){
            int j = v - i;
            if( j < ABUNDANT_MIN) {
                //return false;
                continue;
            }
            /*
             [Theodor: 2019/5/31] is isAbundant() is used here, p23 costs O(N^2)!
            if (isAbundant(j)) {
                return false;
            }
            */
            if(p23Map.get(j) != null) {
                return false;
            }
        }
        return true;
    }


    void p23(){
        final ArrayList<Integer> list = allAbundantUnder(P23_MAX);
        int sum = 0;

        for(int i = 1; i <= P23_MAX; i++) {
            if(i % 1000 ==0 ) {
                System.out.println("DBG: check: : " + i);
            }
            if(isOurDigital(list, i)) {
                sum += i;
                if(sum < 100) {
                    System.out.println("DBG: find our ditigal: " + i);
                }
            }
        }
        System.out.println("P23: result: " + sum);
    }

}
