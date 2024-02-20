# IntersectionTree

## Running the Code
To be able to run the code in IntelliJ, you must first navigate to File -> Project Structure -> Modules and add all of the .JAR files in the two .zip files in the setup_files folder.

To run the tests:
Test.runTests();

To run the data collection:
Test.collectData();
Test.parseDataFiles();

## Graphing Results
The Gnuplot script used to graph the results of the data collection lives in the Gnuplot.p file.

## Code Design
Data is collected in the Test file. The collectData() function collects all of the data and the parseDataFiles() function reformats it for use by Gnuplot.

For each of individual feasibility check (partition one node), tree path construction (build all nodes in a single path down the tree), and full tree construction (build the full tree), the time taken is determined for a varying number of the randomly generated inequalities, a varying number of dimensions for those inequalities, and a varying boundary length defining the space those inequalities must exist in.

The full and path Intersection Tree code lives in the Tree file. The single partition code lives in the Test file. The Simplex Algorithm is run via the NodeData file. MySQL API is in the TreeNode file.
