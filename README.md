# HRM2J
### Compile Human Resource Machine code into java.

##Why should I use this instead of other interpreters?
HRM2J does not interpret the hrm assembly, instead it converts it into java. This gives a huge speed improvment.

|Program|Time(ms/1000 runs)|
|-----|-----|
|HRM2J (Standard)|255|
|HRM2J (Fast)|76|
|[hrmsandbox](https://github.com/sixlettervariables/hrmsandbox)|602|

Benchmark was performed with [this code (for level 41)](https://github.com/atesgoral/hrm-solutions/blob/9c92d7137f6a7593ab35389ab284fd3dcebd2a74/solutions/41-Sorting-Floor-34.714/20.651.selection-sniperrifle2004.asm),
being measured on 1000 runs. The measure was in running only, parsing the input data. The input data consisted of 61 characters:
1,2,3,4,0,4,3,2,1,0,1,3,3,2,2,1,4,0,4,3,2,1,4,3,2,1,4,3,2,1,0,2,1,3,4,7,8,3,2,1,4,6,0,2,7,3,5,1,2,3,0,4,3,1,2,4,2,3,1,4,0
