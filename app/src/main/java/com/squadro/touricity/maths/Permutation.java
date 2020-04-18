package com.squadro.touricity.maths;

    import java.util.ArrayList;
import java.util.List;

    public class Permutation {

        public List<List<Integer>> permute(int[] arr) {
            List<List<Integer>> list = new ArrayList<>();
            permuteHelper(list, new ArrayList<>(), arr);
            return list;
        }

        private void permuteHelper(List<List<Integer>> list, List<Integer> resultList, int [] arr){
            // Base case
            if(resultList.size() == arr.length){
                list.add(new ArrayList<>(resultList));
            }
            else{
                for(int i = 0; i < arr.length; i++){

                    if(resultList.contains(arr[i]))
                    {
                        // If element already exists in the list then skip
                        continue;
                    }
                    // Choose element
                    resultList.add(arr[i]);
                    // Explore
                    permuteHelper(list, resultList, arr);
                    // Unchoose element
                    resultList.remove(resultList.size() - 1);
                }
            }
        }

        public int[] getZeroToN(int n){
            int[] arr = new int[n];

            int counter = 0;
            while(counter < n){
                arr[counter] = counter;
                counter++;
            }
            return arr;
        }
    }

