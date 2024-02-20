set terminal png

set   autoscale                        # scale axes automatically
unset label                            # remove any previous labels
set xtic auto                          # set xtics automatically
set ytic auto                          # set ytics automatically

##### Individual Inequalities ######
set title "Individual Partition: Time Cost vs. Number of Inequalities"
set xlabel "Number of Inequalities"
set ylabel "Time Cost (Seconds)"
set output "Graph_Single_Partition_Variable_Inequalities.png"
plot    "Data_Individual_Partition_Variable_Inequalities_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Individual_Partition_Variable_Inequalities_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \

##### Individual Dimensions ######
unset label 
set title "Individual Partition: Time cost vs. Number of Dimensions"
set xlabel "Number of Dimensions"
set ylabel "Time Cost (Seconds)"
set output "Graph_Single_Partition_Variable_Dimensions.png"
plot    "Data_Individual_Partition_Variable_Dimensions_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Individual_Partition_Variable_Dimensions_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \

##### Individual Domain ######
unset label 
set title "Individual Partition: Time cost vs. Domain Size"
set xlabel "Domain Length"
set ylabel "Time Cost (Seconds)"
set output "Graph_Single_Partition_Variable_Domain.png"
plot    "Data_Individual_Partition_Variable_Domain_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Individual_Partition_Variable_Domain_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \



##### Path Inequalities ######
unset label 
set title "Tree Path: Time Cost vs. Number of Inequalities"
set xlabel "Number of Inequalities"
set ylabel "Time Cost (Seconds)"
set output "Graph_Tree_Path_Variable_Inequalities.png"
plot    "Data_Tree_Path_Variable_Inequalities_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Tree_Path_Variable_Inequalities_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \

##### Path Dimensions ######
unset label 
set title "Tree Path: Time cost vs. Number of Dimensions"
set xlabel "Number of Dimensions"
set ylabel "Time Cost (Seconds)"
set output "Graph_Tree_Path_Variable_Dimensions.png"
plot    "Data_Tree_Path_Variable_Dimensions_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Tree_Path_Variable_Dimensions_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \

##### Path Domain ######
unset label 
set title "Tree Path: Time cost vs. Domain Size"
set xlabel "Domain Length"
set ylabel "Time Cost (Seconds)"
set output "Graph_Tree_Path_Variable_Domain.png"
plot    "Data_Tree_Path_Variable_Domain_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Tree_Path_Variable_Domain_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \



##### Full Inequalities ######
set title "Full Tree: Time Cost vs. Number of Inequalities"
set xlabel "Number of Inequalities"
set ylabel "Time Cost (Seconds)"
set output "Graph_Full_Tree_Variable_Inequalities.png"
plot    "Data_Full_Tree_Variable_Inequalities_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Full_Tree_Variable_Inequalities_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \

##### Full Dimensions ######
unset label 
set title "Full Tree: Time cost vs. Number of Dimensions"
set xlabel "Number of Dimensions"
set ylabel "Time Cost (Seconds)"
set output "Graph_Full_Tree_Variable_Dimensions.png"
plot    "Data_Full_Tree_Variable_Dimensions_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Full_Tree_Variable_Dimensions_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \

##### Full Domain ######
unset label 
set title "Full Tree: Time cost vs. Domain Size"
set xlabel "Domain Length"
set ylabel "Time Cost (Seconds)"
set output "Graph_Full_Tree_Variable_Domain.png"
plot    "Data_Full_Tree_Variable_Domain_Simplex.txt" using 1:($2*5.2) title "Naive Simplex" w linespoints, \
        "Data_Full_Tree_Variable_Domain_Sign_Changing_Simplex.txt" using 1:($2*5.2) title "Sign-Changing Simplex"  w linespoints, \
