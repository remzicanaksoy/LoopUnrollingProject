#!/bin/bash
#output file is in format $run_count$file_prefix$loop_depth$unroll_factor
#for example a file 1loop_features35.txt means run 1, loop_depth3, unroll factor 5 loop features file. Same for loop_exec
clang -emit-llvm -o p.bc -c ../mypass/measure.c
LFP="loopFeaturesFile"; #prefix for feature file
EFC="loopExecCount"; #prefix for execution_time_file
rm ../run/*
for run_count in 1 2 3 4 5; do #iterate over 5 runs, we will use average of these
  for depth in 1 2 3 4 5; do #go through loops in depth 1 through 5
     for factor in  1 2 3 4 5 8 10 16 ; do #go through these unroll factors
        rm -r *.txt;
        for i in *.c; do #go through all .c files in folder
         bci=${i%??};
         link_bc="${bci}.bc";
         link2_bc="${bci}2.bc";
         clang -emit-llvm -o $bci -c $i;
         llvm-link p.bc $bci -S -o=$link_bc;
         opt -load ../mypass/Release+Asserts/lib/mypass.so -loop-simplify -loop-rotate -mem2reg -myunroll -my-depth=$depth -my-count=$factor $link_bc -f -o $link2_bc;
         lli $link2_bc;
         rm $bci
         rm $link_bc;
         rm $link2_bc;
       done
       ec="${run_count}_${EFC}_${depth}_${factor}.txt"; #exec_file specific to this run
       ff="${run_count}_${LFP}_${depth}_${factor}.txt"; #feature_file specific to this run
       mv loop_features.txt $ff; #autogen of loop_features.txt is renamed
       mv $ff ../runs/.; #moved out of benchmark to upper level folder
       mv loop_exec_time.txt $ec; #same as feature file
       mv $ec ../runs/;
       rm -r *.txt;
       echo "$ff";  #health check 
     done
  done
# or do whatever with individual element of the array
done
#for i in *.c; do
#  bci=${i%??};
#  link_bc="${bci}.bc";
#  link2_bc="${bci}2.bc";
#  clang -emit-llvm -o $bci -c $i;
#  llvm-link p.bc $bci -S -o=$link_bc;
#  opt -load ../Release+Asserts/lib/mypass.so -loop-simplify -loop-rotate -mem2reg -myunroll -my-depth=1 -my-count=2 $link_bc -f -o $link2_bc;
#  lli $link2_bc;
#  rm $bci
#  rm $link_bc;
#  rm $link2_bc;
#done
#mv loop_features.txt ../.;
#mv loop_exec_time.txt ../.;
#rm -r *.txt;
